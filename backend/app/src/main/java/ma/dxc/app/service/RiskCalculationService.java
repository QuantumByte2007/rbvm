package ma.dxc.app.service;

import ma.dxc.app.config.AsyncConfig;
import ma.dxc.app.dto.RiskBreakdownDto;
import ma.dxc.app.entity.*;
import ma.dxc.app.event.RiskRecalculationCompletedEvent;
import ma.dxc.app.repository.CmdbCiRepository;
import ma.dxc.app.repository.HostRepository;
import ma.dxc.app.repository.RiskSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Computes ARS (pfa_formule.pdf eq. 1) for every reconciled Host: ACS and the
 * weighted-QDS sum are computed in parallel per asset, then combined with the
 * external/mitigation factors into ARS and an SLA classification.
 */
@Service
public class RiskCalculationService {

    private static final String QID_INTERNET_EXPOSED = "45004";
    private final Logger logger = LoggerFactory.getLogger(RiskCalculationService.class);

    private final HostRepository hostRepository;
    private final CmdbCiRepository cmdbCiRepository;
    private final RiskConfigService riskConfigService;
    private final RiskWeightConfigService riskWeightConfigService;
    private final RiskSnapshotRepository riskSnapshotRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Executor calcExecutor;

    public RiskCalculationService(HostRepository hostRepository,
                                   CmdbCiRepository cmdbCiRepository,
                                   RiskConfigService riskConfigService,
                                   RiskWeightConfigService riskWeightConfigService,
                                   RiskSnapshotRepository riskSnapshotRepository,
                                   ApplicationEventPublisher eventPublisher,
                                   @Qualifier(AsyncConfig.CALC_EXECUTOR) Executor calcExecutor) {
        this.hostRepository = hostRepository;
        this.cmdbCiRepository = cmdbCiRepository;
        this.riskConfigService = riskConfigService;
        this.riskWeightConfigService = riskWeightConfigService;
        this.riskSnapshotRepository = riskSnapshotRepository;
        this.eventPublisher = eventPublisher;
        this.calcExecutor = calcExecutor;
    }

    @Transactional
    public void recomputeAll() {
        if (!riskWeightConfigService.isConfigured(WeightCategory.ROLE)
                || !riskWeightConfigService.isConfigured(WeightCategory.ENV)) {
            logger.warn("Skipping risk recomputation: Role and/or Env weights not configured yet "
                    + "(see /riskCalcul/role and /riskCalcul/env)");
            return; // ACS inputs not submitted yet - nothing to compute
        }

        RiskConfig config = riskConfigService.getConfig();
        Map<String, Double> roleWeights = riskWeightConfigService.getWeights(WeightCategory.ROLE);
        Map<String, Double> envWeights = riskWeightConfigService.getWeights(WeightCategory.ENV);

        Map<String, CmdbCi> cmdbByIp = cmdbCiRepository.findAll().stream()
                .collect(Collectors.toMap(CmdbCi::getIp, ci -> ci, (a, b) -> a));

        List<Host> hosts = hostRepository.findAllWithVulns();

        List<CompletableFuture<Void>> pending = hosts.stream()
                .map(host -> recomputeOne(host, cmdbByIp.get(host.getIp()), config, roleWeights, envWeights))
                .toList();

        CompletableFuture.allOf(pending.toArray(new CompletableFuture[0])).join();

        recordSnapshot(hosts);
        eventPublisher.publishEvent(new RiskRecalculationCompletedEvent());
    }

    private void recordSnapshot(List<Host> hosts) {
        Map<RiskClass, Long> counts = new EnumMap<>(RiskClass.class);
        double arsSum = 0;
        long arsCount = 0;
        for (Host host : hosts) {
            if (host.getRiskClass() != null) {
                counts.merge(host.getRiskClass(), 1L, Long::sum);
            }
            if (host.getArs() != null) {
                arsSum += host.getArs();
                arsCount++;
            }
        }
        Double avgArs = arsCount > 0 ? arsSum / arsCount : null;
        riskSnapshotRepository.save(new RiskSnapshot(null, Instant.now(),
                counts.getOrDefault(RiskClass.BLACK, 0L),
                counts.getOrDefault(RiskClass.RED, 0L),
                counts.getOrDefault(RiskClass.ORANGE, 0L),
                counts.getOrDefault(RiskClass.YELLOW, 0L),
                counts.getOrDefault(RiskClass.GREEN, 0L),
                avgArs));
    }

    private CompletableFuture<Void> recomputeOne(Host host, CmdbCi cmdbCi, RiskConfig config,
                                                   Map<String, Double> roleWeights, Map<String, Double> envWeights) {
        if (cmdbCi == null) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Double> acsFuture =
                CompletableFuture.supplyAsync(() -> computeAcs(cmdbCi, roleWeights, envWeights), calcExecutor);
        CompletableFuture<Double> weightedQdsFuture =
                CompletableFuture.supplyAsync(() -> computeWeightedQds(host, config), calcExecutor);

        return acsFuture.thenCombine(weightedQdsFuture,
                (acs, weightedQds) -> applyRiskScore(host, cmdbCi, config, acs, weightedQds));
    }

    private double computeAcs(CmdbCi cmdbCi, Map<String, Double> roleWeights, Map<String, Double> envWeights) {
        String roleKey = cmdbCi.getRole() != null ? cmdbCi.getRole().name() : Role.UNKNOWN.name();
        String envKey = cmdbCi.getEnv() != null ? cmdbCi.getEnv().name() : Env.UNKNOWN.name();
        double roleWeight = roleWeights.getOrDefault(roleKey, 0.5);
        double envWeight = envWeights.getOrDefault(envKey, 0.5);
        double avg = (roleWeight + envWeight) / 2.0;
        return 1 + avg * 4; // rescale 0..1 -> 1..5
    }

    // pfa_formule.pdf eq. 2 (updated): for each severity class,
    // weight * avg(QDS) * count(QDS)^(1/100) - the count factor gives classes with
    // more open vulnerabilities a (slowly growing) extra push, without letting a single
    // stray finding swing the term the way a raw count multiplier would.
    private double computeWeightedQds(Host host, RiskConfig config) {
        Map<Severity, java.util.IntSummaryStatistics> statsBySeverity = host.getVulns().stream()
                .filter(v -> v.getSeverity() != null && v.getQds() != null)
                .collect(Collectors.groupingBy(VulnInfo::getSeverity,
                        Collectors.summarizingInt(VulnInfo::getQds)));

        return java.util.Arrays.stream(Severity.values())
                .mapToDouble(sev -> severityTerm(sev, statsBySeverity.get(sev), config))
                .sum();
    }

    private double severityTerm(Severity severity, java.util.IntSummaryStatistics stats, RiskConfig config) {
        if (stats == null || stats.getCount() == 0) {
            return 0.0;
        }
        return severityWeight(severity, config) * stats.getAverage() * Math.pow(stats.getCount(), 1.0 / 100.0);
    }

    private double severityWeight(Severity severity, RiskConfig config) {
        return switch (severity) {
            case CRITICAL -> config.getWCritical();
            case HIGH -> config.getWHigh();
            case MEDIUM -> config.getWMedium();
            case LOW -> config.getWLow();
        };
    }

    // Classifies a (capped) ARS into a RiskClass using the user-configured thresholds -
    // replaces the old hardcoded RiskClass.fromArs(). GREEN is the floor: whatever
    // doesn't clear yellowMinArs.
    private RiskClass classify(double ars, RiskConfig config) {
        if (ars >= config.getBlackMinArs()) return RiskClass.BLACK;
        if (ars >= config.getRedMinArs()) return RiskClass.RED;
        if (ars >= config.getOrangeMinArs()) return RiskClass.ORANGE;
        if (ars >= config.getYellowMinArs()) return RiskClass.YELLOW;
        return RiskClass.GREEN;
    }

    private int slaDays(RiskClass riskClass, RiskConfig config) {
        return switch (riskClass) {
            case BLACK -> config.getSlaBlackDays();
            case RED -> config.getSlaRedDays();
            case ORANGE -> config.getSlaOrangeDays();
            case YELLOW -> config.getSlaYellowDays();
            case GREEN -> config.getSlaGreenDays();
        };
    }

    private Void applyRiskScore(Host host, CmdbCi cmdbCi, RiskConfig config, double acs, double weightedQds) {
        double iExternal = isInternetExposed(host) ? config.getIExternal() : 1.0;
        double mSoc = cmdbCi.isSoc() ? config.getMSoc() : 1.0;
        double mAv = cmdbCi.isAntivirus() ? config.getMAv() : 1.0;
        // Standard_Patching=No means the asset is excluded from the automated patch
        // cycle, which is when M_Patch applies (see CmdbCi.standardPatching javadoc).
        double mPatch = !cmdbCi.isStandardPatching() ? config.getMPatch() : 1.0;
        double mControls = mSoc * mAv * mPatch;
        // ARS is capped at 1000: the formula's inputs (ACS up to 5, weighted QDS,
        // exposure/mitigation multipliers) can otherwise compound past any class
        // threshold, and an unbounded score doesn't mean anything past "worst possible".
        double ars = Math.min(acs * weightedQds * iExternal * mControls, 1000.0);

        RiskClass riskClass = classify(ars, config);
        Instant now = Instant.now();
        java.util.Optional<Instant> earliestOpenFirstFound = earliestOpenVulnFirstFound(host);
        Instant slaDueDate = earliestOpenFirstFound
                .map(firstFound -> firstFound.plus(slaDays(riskClass, config), ChronoUnit.DAYS))
                .orElse(null);
        Double ageInDays = earliestOpenFirstFound
                .map(firstFound -> Duration.between(firstFound, now).toHours() / 24.0)
                .orElse(null);

        SlaStatus slaStatus;
        if (cmdbCi.isException()) {
            slaStatus = SlaStatus.ACCEPTED_RISK;
        } else if (slaDueDate != null && slaDueDate.isBefore(now)) {
            slaStatus = SlaStatus.VIOLATED;
        } else {
            slaStatus = SlaStatus.ON_TRACK;
        }

        host.setAcs(acs);
        host.setArs(ars);
        host.setRiskClass(riskClass);
        host.setSlaStatus(slaStatus);
        host.setSlaDueDate(slaDueDate);
        host.setAgeInDays(ageInDays);
        host.setLastCalculatedAt(now);
        return null;
    }

    // Read-only twin of applyRiskScore: same math, returned as explained terms
    // instead of being persisted onto Host - for the /assets/{id} detail page.
    @Transactional(readOnly = true)
    public RiskBreakdownDto explain(Host host, CmdbCi cmdbCi) {
        RiskConfig config = riskConfigService.getConfig();
        Map<String, Double> roleWeights = riskWeightConfigService.getWeights(WeightCategory.ROLE);
        Map<String, Double> envWeights = riskWeightConfigService.getWeights(WeightCategory.ENV);

        String roleKey = cmdbCi.getRole() != null ? cmdbCi.getRole().name() : Role.UNKNOWN.name();
        String envKey = cmdbCi.getEnv() != null ? cmdbCi.getEnv().name() : Env.UNKNOWN.name();
        double roleWeight = roleWeights.getOrDefault(roleKey, 0.5);
        double envWeight = envWeights.getOrDefault(envKey, 0.5);
        double acs = computeAcs(cmdbCi, roleWeights, envWeights);

        Map<Severity, java.util.IntSummaryStatistics> statsBySeverity = host.getVulns().stream()
                .filter(v -> v.getSeverity() != null && v.getQds() != null)
                .collect(Collectors.groupingBy(VulnInfo::getSeverity, Collectors.summarizingInt(VulnInfo::getQds)));
        List<RiskBreakdownDto.SeverityTermDto> severityTerms = java.util.Arrays.stream(Severity.values())
                .map(sev -> {
                    java.util.IntSummaryStatistics stats = statsBySeverity.get(sev);
                    long count = stats != null ? stats.getCount() : 0;
                    double avgQds = stats != null ? stats.getAverage() : 0.0;
                    double weight = severityWeight(sev, config);
                    return new RiskBreakdownDto.SeverityTermDto(sev.name(), avgQds, weight, count,
                            severityTerm(sev, stats, config));
                })
                .toList();
        double weightedQds = computeWeightedQds(host, config);

        boolean internetExposed = isInternetExposed(host);
        double iExternal = internetExposed ? config.getIExternal() : 1.0;

        boolean mSocApplied = cmdbCi.isSoc();
        double mSoc = mSocApplied ? config.getMSoc() : 1.0;
        boolean mAvApplied = cmdbCi.isAntivirus();
        double mAv = mAvApplied ? config.getMAv() : 1.0;
        boolean mPatchApplied = !cmdbCi.isStandardPatching();
        double mPatch = mPatchApplied ? config.getMPatch() : 1.0;
        double mControls = mSoc * mAv * mPatch;

        double ars = Math.min(acs * weightedQds * iExternal * mControls, 1000.0);
        RiskClass riskClass = classify(ars, config);

        Instant now = Instant.now();
        java.util.Optional<Instant> earliestOpenFirstFound = earliestOpenVulnFirstFound(host);
        Instant slaDueDate = earliestOpenFirstFound
                .map(firstFound -> firstFound.plus(slaDays(riskClass, config), ChronoUnit.DAYS))
                .orElse(null);
        Double ageInDays = earliestOpenFirstFound
                .map(firstFound -> Duration.between(firstFound, now).toHours() / 24.0)
                .orElse(null);
        SlaStatus slaStatus;
        if (cmdbCi.isException()) {
            slaStatus = SlaStatus.ACCEPTED_RISK;
        } else if (slaDueDate != null && slaDueDate.isBefore(now)) {
            slaStatus = SlaStatus.VIOLATED;
        } else {
            slaStatus = SlaStatus.ON_TRACK;
        }

        return new RiskBreakdownDto(host.getId(), host.getDns(), host.getIp(),
                roleKey, envKey, cmdbCi.getCmdbOwner(),
                roleWeight, envWeight, acs,
                severityTerms, weightedQds,
                internetExposed, iExternal,
                mSoc, mSocApplied, mAv, mAvApplied, mPatch, mPatchApplied, mControls,
                ars, riskClass, slaStatus, slaDueDate, ageInDays);
    }

    private boolean isInternetExposed(Host host) {
        return host.getVulns().stream().anyMatch(v -> QID_INTERNET_EXPOSED.equals(v.getQidValue()));
    }

    private java.util.Optional<Instant> earliestOpenVulnFirstFound(Host host) {
        return host.getVulns().stream()
                .filter(v -> v.getLastfixed() == null && v.getFirstFound() != null)
                .map(VulnInfo::getFirstFound)
                .min(Instant::compareTo);
    }
}
