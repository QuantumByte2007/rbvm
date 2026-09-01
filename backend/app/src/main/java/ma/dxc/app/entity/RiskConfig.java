package ma.dxc.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Single active configuration row driving the ARS formula (pfa_formule.pdf, eq. 1-2).
 * Severity weights come from the "Weights" page; external/mitigation multipliers come
 * from their own pages. Defaults below are the reference values from table 1.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // wi in the ARS sum, one per severity
    @DecimalMin("0.0") @DecimalMax("1.0")
    private Double wCritical = 1.0;
    @DecimalMin("0.0") @DecimalMax("1.0")
    private Double wHigh = 0.2;
    @DecimalMin("0.0") @DecimalMax("1.0")
    private Double wMedium = 0.05;
    @DecimalMin("0.0") @DecimalMax("1.0")
    private Double wLow = 0.01;

    // I_External: applied when the asset carries QID 45004 (internet-facing)
    @DecimalMin("1.0")
    private Double iExternal = 1.2;

    // M_Controls factors
    @DecimalMin("0.0")
    private Double mSoc = 0.8;
    @DecimalMin("0.0")
    private Double mAv = 0.9;
    @DecimalMin("0.0")
    private Double mPatch = 1.3;

    // SLA class thresholds (min ARS to fall into each class, pfa_formule.pdf table 2)
    // and the remediation SLA (days) each class carries - user-configurable instead of
    // hardcoded on the RiskClass enum. GREEN has no floor: it's whatever is left below
    // yellowMinArs. ARS itself is capped at 1000 (see RiskCalculationService), so these
    // should stay under that ceiling.
    @DecimalMin("0.0") @DecimalMax("1000.0")
    private Double blackMinArs = 700.0;
    @DecimalMin("0.0") @DecimalMax("1000.0")
    private Double redMinArs = 600.0;
    @DecimalMin("0.0") @DecimalMax("1000.0")
    private Double orangeMinArs = 400.0;
    @DecimalMin("0.0") @DecimalMax("1000.0")
    private Double yellowMinArs = 200.0;

    private Integer slaBlackDays = 2;
    private Integer slaRedDays = 7;
    private Integer slaOrangeDays = 15;
    private Integer slaYellowDays = 30;
    private Integer slaGreenDays = 90;
}
