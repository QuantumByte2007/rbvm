package ma.dxc.app.service;

import ma.dxc.app.entity.RiskConfig;
import ma.dxc.app.event.RiskConfigSubmittedEvent;
import ma.dxc.app.repository.RiskConfigRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// RiskConfig is a singleton row (id=1): severity weights + external/mitigation factor
// multipliers from pfa_formule.pdf table 1. Replaces the old Calcul static-map bug
// (shared mutable Map<Double,Severity> that collided whenever two sliders shared a value).
@Service
public class RiskConfigService {

    private final RiskConfigRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public RiskConfigService(RiskConfigRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    // Deliberately not pinning a fixed id (e.g. id=1) here: manually assigning an id
    // before save() on a @GeneratedValue(SEQUENCE) entity makes Spring Data pick
    // merge() over persist(), whose interaction with the id generator is ambiguous
    // across Hibernate versions. Just treat "the first row" as the singleton instead.
    // REQUIRES_NEW: callers like RiskCalculationService.explain() and
    // DashboardMetricsService.computeMetrics() run in a read-only transaction - the
    // lazy-create save() below needs its own writable transaction regardless of what
    // the caller's is, or Postgres rejects the sequence nextval() outright.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RiskConfig getConfig() {
        return repository.findAll().stream().findFirst()
                .orElseGet(() -> repository.save(new RiskConfig()));
    }

    @Transactional
    public RiskConfig updateSeverityWeights(Double wCritical, Double wHigh, Double wMedium, Double wLow) {
        RiskConfig config = getConfig();
        config.setWCritical(wCritical);
        config.setWHigh(wHigh);
        config.setWMedium(wMedium);
        config.setWLow(wLow);
        RiskConfig saved = repository.save(config);
        eventPublisher.publishEvent(new RiskConfigSubmittedEvent(null));
        return saved;
    }

    @Transactional
    public RiskConfig updateFactors(Double iExternal, Double mSoc, Double mAv, Double mPatch) {
        RiskConfig config = getConfig();
        config.setIExternal(iExternal);
        config.setMSoc(mSoc);
        config.setMAv(mAv);
        config.setMPatch(mPatch);
        RiskConfig saved = repository.save(config);
        eventPublisher.publishEvent(new RiskConfigSubmittedEvent(null));
        return saved;
    }

    @Transactional
    public RiskConfig updateSlaClasses(Double blackMinArs, Integer slaBlackDays,
                                        Double redMinArs, Integer slaRedDays,
                                        Double orangeMinArs, Integer slaOrangeDays,
                                        Double yellowMinArs, Integer slaYellowDays,
                                        Integer slaGreenDays) {
        RiskConfig config = getConfig();
        config.setBlackMinArs(blackMinArs);
        config.setSlaBlackDays(slaBlackDays);
        config.setRedMinArs(redMinArs);
        config.setSlaRedDays(slaRedDays);
        config.setOrangeMinArs(orangeMinArs);
        config.setSlaOrangeDays(slaOrangeDays);
        config.setYellowMinArs(yellowMinArs);
        config.setSlaYellowDays(slaYellowDays);
        config.setSlaGreenDays(slaGreenDays);
        RiskConfig saved = repository.save(config);
        eventPublisher.publishEvent(new RiskConfigSubmittedEvent(null));
        return saved;
    }
}
