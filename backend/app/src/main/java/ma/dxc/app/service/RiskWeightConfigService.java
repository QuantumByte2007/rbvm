package ma.dxc.app.service;

import ma.dxc.app.entity.RiskWeightConfig;
import ma.dxc.app.entity.WeightCategory;
import ma.dxc.app.event.RiskConfigSubmittedEvent;
import ma.dxc.app.repository.RiskWeightConfigRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

// Role/Env/SlaClass weights: one row per enum value, unlike the fixed-shape
// severity/factor weights on RiskConfig. Also doubles as the "has the user
// configured this category yet" gate RiskRecalculationListener checks.
@Service
public class RiskWeightConfigService {

    private final RiskWeightConfigRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public RiskWeightConfigService(RiskWeightConfigRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public Map<String, Double> getWeights(WeightCategory category) {
        return repository.findByCategory(category).stream()
                .collect(Collectors.toMap(RiskWeightConfig::getKey, RiskWeightConfig::getWeight));
    }

    @Transactional(readOnly = true)
    public boolean isConfigured(WeightCategory category) {
        return repository.existsByCategory(category);
    }

    @Transactional
    public void saveWeights(WeightCategory category, Map<String, Double> weights) {
        weights.forEach((key, weight) -> {
            RiskWeightConfig config = repository.findByCategoryAndKey(category, key)
                    .orElseGet(() -> new RiskWeightConfig(null, category, key, weight));
            config.setWeight(weight);
            repository.save(config);
        });
        eventPublisher.publishEvent(new RiskConfigSubmittedEvent(category));
    }
}
