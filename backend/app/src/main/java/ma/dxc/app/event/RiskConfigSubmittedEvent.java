package ma.dxc.app.event;

import ma.dxc.app.entity.WeightCategory;

/**
 * Published whenever the user submits a risk-weight form (severity, role, env,
 * MTTR-per-class or the external/mitigation factors). Consumed by
 * RiskRecalculationListener to auto-trigger ARS computation once ingestion data
 * exists too. {@code category} is null for the severity/factor forms, which live
 * on the singleton RiskConfig rather than the generic RiskWeightConfig table.
 */
public record RiskConfigSubmittedEvent(WeightCategory category) {
}
