package ma.dxc.app.entity;

/**
 * SLA classes from pfa_formule.pdf, table 2. Ordinal order matters: BLACK is worst.
 * Thresholds and SLA days used to be hardcoded here; they're now user-configurable
 * (see RiskConfig's sla* fields) - RiskCalculationService.classify()/slaDays() apply them.
 */
public enum RiskClass {
    BLACK,
    RED,
    ORANGE,
    YELLOW,
    GREEN
}
