package ma.dxc.app.entity;

// Severity weights (wCritical..wLow) and the external/mitigation factor multipliers
// live as typed columns on RiskConfig instead - that set is fixed by the formula.
// Role/Env/SlaClass weights are keyed by a variable enum value each, so they use
// this generic (category, key) -> weight table instead.
public enum WeightCategory {
    ROLE,
    ENV,
    SLA_CLASS
}
