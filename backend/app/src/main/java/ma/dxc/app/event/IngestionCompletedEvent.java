package ma.dxc.app.event;

import ma.dxc.app.entity.Type;

/**
 * Published once a CMDB or Qualys Spring Batch job reaches BatchStatus.COMPLETED.
 * Consumed by RiskRecalculationListener to auto-trigger ARS computation when a
 * RiskConfig has already been submitted by the user.
 */
public record IngestionCompletedEvent(Type type) {
}
