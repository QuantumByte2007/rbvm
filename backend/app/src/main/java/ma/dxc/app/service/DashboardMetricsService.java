package ma.dxc.app.service;

import ma.dxc.app.dto.*;
import ma.dxc.app.entity.*;
import ma.dxc.app.repository.CmdbCiRepository;
import ma.dxc.app.repository.HostRepository;
import ma.dxc.app.repository.RiskSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds the chart-ready aggregates for the dashboard (app_notes_dxc.txt section 5):
 * SLA-class distribution, per-team stacked bar, Env->Role->RiskClass Sankey flow,
 * SLA scatter points, and MTTR (plain per-class + weighted, reusing the SLA_CLASS
 * weights the user configured on the MTTR weights screen).
 */
@Service
public class DashboardMetricsService {

    private final HostRepository hostRepository;
    private final CmdbCiRepository cmdbCiRepository;
    private final RiskSnapshotRepository riskSnapshotRepository;
    private final RiskWeightConfigService riskWeightConfigService;
    private final RiskConfigService riskConfigService;

    public DashboardMetricsService(HostRepository hostRepository,
                                    CmdbCiRepository cmdbCiRepository,
                                    RiskSnapshotRepository riskSnapshotRepository,
                                    RiskWeightConfigService riskWeightConfigService,
                                    RiskConfigService riskConfigService) {
        this.hostRepository = hostRepository;
        this.cmdbCiRepository = cmdbCiRepository;
        this.riskSnapshotRepository = riskSnapshotRepository;
        this.riskWeightConfigService = riskWeightConfigService;
        this.riskConfigService = riskConfigService;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsDto computeMetrics() {
        List<Host> hosts = hostRepository.findAllWithVulns();
        Map<String, CmdbCi> cmdbByIp = cmdbCiRepository.findAll().stream()
                .collect(Collectors.toMap(CmdbCi::getIp, ci -> ci, (a, b) -> a));

        Map<RiskClass, Long> classCounts = new EnumMap<>(RiskClass.class);
        Map<String, Map<RiskClass, Long>> ownerClassCounts = new LinkedHashMap<>();
        Map<String, Map<String, Long>> envRoleCounts = new LinkedHashMap<>();
        Map<String, Map<RiskClass, Long>> roleClassCounts = new LinkedHashMap<>();
        Map<RiskClass, List<Double>> ttrByClass = new EnumMap<>(RiskClass.class);
        List<SlaPointDto> slaPoints = new ArrayList<>();

        for (Host host : hosts) {
            CmdbCi cmdbCi = cmdbByIp.get(host.getIp());
            RiskClass riskClass = host.getRiskClass();

            if (riskClass != null) {
                classCounts.merge(riskClass, 1L, Long::sum);
            }
            if (host.getSlaDueDate() != null) {
                slaPoints.add(new SlaPointDto(host.getDns(), host.getSlaDueDate(), host.getArs(),
                        host.getAgeInDays(), riskClass, host.getSlaStatus()));
            }
            if (cmdbCi == null) {
                continue;
            }
            if (riskClass != null) {
                String owner = cmdbCi.getCmdbOwner() != null && !cmdbCi.getCmdbOwner().isBlank()
                        ? cmdbCi.getCmdbOwner() : "Unassigned";
                ownerClassCounts.computeIfAbsent(owner, k -> new EnumMap<>(RiskClass.class))
                        .merge(riskClass, 1L, Long::sum);

                String env = cmdbCi.getEnv() != null ? cmdbCi.getEnv().name() : Env.UNKNOWN.name();
                String role = cmdbCi.getRole() != null ? cmdbCi.getRole().name() : Role.UNKNOWN.name();
                envRoleCounts.computeIfAbsent(env, k -> new LinkedHashMap<>()).merge(role, 1L, Long::sum);
                roleClassCounts.computeIfAbsent(role, k -> new EnumMap<>(RiskClass.class))
                        .merge(riskClass, 1L, Long::sum);
            }
            if (!cmdbCi.isException() && riskClass != null) {
                for (VulnInfo vuln : host.getVulns()) {
                    if (vuln.getLastfixed() != null && vuln.getFirstFound() != null) {
                        double ttrDays = Duration.between(vuln.getFirstFound(), vuln.getLastfixed()).toHours() / 24.0;
                        ttrByClass.computeIfAbsent(riskClass, k -> new ArrayList<>()).add(ttrDays);
                    }
                }
            }
        }

        List<SankeyLinkDto> sankeyLinks = new ArrayList<>();
        envRoleCounts.forEach((env, roleMap) -> roleMap.forEach((role, count) ->
                sankeyLinks.add(new SankeyLinkDto(env, role, count))));
        roleClassCounts.forEach((role, classMap) -> classMap.forEach((riskClass, count) ->
                sankeyLinks.add(new SankeyLinkDto(role, riskClass.name(), count))));

        Map<RiskClass, Double> mttrByClass = new EnumMap<>(RiskClass.class);
        ttrByClass.forEach((riskClass, ttrs) ->
                mttrByClass.put(riskClass, ttrs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0)));

        Double weightedMttrDays = computeWeightedMttr(ttrByClass);

        Map<SlaStatus, Long> slaStatusCounts = hosts.stream()
                .filter(h -> h.getSlaStatus() != null)
                .collect(Collectors.groupingBy(Host::getSlaStatus, Collectors.counting()));

        List<RiskSnapshotDto> history = riskSnapshotRepository.findAllByOrderByComputedAtAsc().stream()
                .map(s -> new RiskSnapshotDto(s.getComputedAt(), s.getBlackCount(), s.getRedCount(),
                        s.getOrangeCount(), s.getYellowCount(), s.getGreenCount(), s.getAvgArs()))
                .toList();

        boolean weightsConfigured = riskWeightConfigService.isConfigured(WeightCategory.ROLE)
                && riskWeightConfigService.isConfigured(WeightCategory.ENV);

        OptionalDouble avgArs = hosts.stream()
                .map(Host::getArs)
                .filter(java.util.Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average();
        Double ers = avgArs.isPresent() ? avgArs.getAsDouble() : null;

        long shadowItCount = hosts.stream().filter(h -> !cmdbByIp.containsKey(h.getIp())).count()
                + cmdbCiRepository.findAllWithoutHost().size();

        RiskConfig config = riskConfigService.getConfig();
        List<SlaZoneDto> slaZones = List.of(
                new SlaZoneDto(RiskClass.GREEN.name(), 0, config.getSlaGreenDays()),
                new SlaZoneDto(RiskClass.YELLOW.name(), config.getYellowMinArs(), config.getSlaYellowDays()),
                new SlaZoneDto(RiskClass.ORANGE.name(), config.getOrangeMinArs(), config.getSlaOrangeDays()),
                new SlaZoneDto(RiskClass.RED.name(), config.getRedMinArs(), config.getSlaRedDays()),
                new SlaZoneDto(RiskClass.BLACK.name(), config.getBlackMinArs(), config.getSlaBlackDays()));

        return new DashboardMetricsDto(classCounts, ownerClassCounts, sankeyLinks, slaPoints,
                mttrByClass, weightedMttrDays, history, slaStatusCounts, weightsConfigured, ers, shadowItCount, slaZones);
    }

    // pfa_formule.pdf: MTTR_w = sum(w_class * sum(TTR_class)) / sum(w_class * count_class)
    private Double computeWeightedMttr(Map<RiskClass, List<Double>> ttrByClass) {
        if (ttrByClass.isEmpty()) {
            return null;
        }
        Map<String, Double> slaClassWeights = riskWeightConfigService.getWeights(WeightCategory.SLA_CLASS);
        double numerator = 0;
        double denominator = 0;
        for (Map.Entry<RiskClass, List<Double>> entry : ttrByClass.entrySet()) {
            double weight = slaClassWeights.getOrDefault(entry.getKey().name(), 0.5);
            double sumTtr = entry.getValue().stream().mapToDouble(Double::doubleValue).sum();
            numerator += weight * sumTtr;
            denominator += weight * entry.getValue().size();
        }
        return denominator > 0 ? numerator / denominator : null;
    }
}
