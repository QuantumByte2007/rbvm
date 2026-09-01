package ma.dxc.app.repository;

import ma.dxc.app.entity.RiskWeightConfig;
import ma.dxc.app.entity.WeightCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskWeightConfigRepository extends JpaRepository<RiskWeightConfig, Long> {

    List<RiskWeightConfig> findByCategory(WeightCategory category);

    Optional<RiskWeightConfig> findByCategoryAndKey(WeightCategory category, String key);

    boolean existsByCategory(WeightCategory category);
}
