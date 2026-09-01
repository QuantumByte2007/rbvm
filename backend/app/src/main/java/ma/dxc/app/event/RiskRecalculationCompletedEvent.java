package ma.dxc.app.event;

/**
 * Published at the end of RiskCalculationService.recomputeAll(). Consumed by
 * DashboardSseController to push a fresh metrics snapshot to every connected
 * dashboard over SSE.
 */
public record RiskRecalculationCompletedEvent() {
}
