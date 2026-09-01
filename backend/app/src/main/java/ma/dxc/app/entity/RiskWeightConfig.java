package ma.dxc.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_category_key", columnNames = {"category", "config_key"}))
public class RiskWeightConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeightCategory category;

    // Severity name, Role name, Env name, SlaClass name, or a FACTOR key
    // (I_EXTERNAL / M_SOC / M_AV / M_PATCH).
    @Column(name = "config_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private Double weight;
}
