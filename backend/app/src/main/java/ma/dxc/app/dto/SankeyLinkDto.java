package ma.dxc.app.dto;

// One edge in the Env -> Role -> RiskClass impact-flow Sankey diagram.
public record SankeyLinkDto(String from, String to, long value) {
}
