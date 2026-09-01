package ma.dxc.app.dto;

// One RiskClass's configured (minArs, slaDays) pair, ascending by minArs - lets the
// dashboard draw its risk-zone bands/SLA-limit line from the user's actual config
// instead of a hardcoded copy of it.
public record SlaZoneDto(String riskClass, double minArs, int slaDays) {
}
