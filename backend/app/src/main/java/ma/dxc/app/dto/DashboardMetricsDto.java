package ma.dxc.app.dto;

import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.SlaStatus;

import java.util.List;
import java.util.Map;

public record DashboardMetricsDto(
        Map<RiskClass, Long> classCounts,
        Map<String, Map<RiskClass, Long>> ownerClassCounts,
        List<SankeyLinkDto> sankeyLinks,
        List<SlaPointDto> slaPoints,
        Map<RiskClass, Double> mttrByClass,
        Double weightedMttrDays,
        List<RiskSnapshotDto> history,
        Map<SlaStatus, Long> slaStatusCounts,
        boolean weightsConfigured,
        // Enterprise Risk Score: average ARS (already capped at 1000) across every
        // scored host - the org-wide "how bad is it" number shown on the dashboard.
        Double ers,
        long shadowItCount,
        // Ascending by minArs (GREEN..BLACK) - the user's actual SLA-class config, so the
        // dashboard's zone bands/colors never drift from what ARS was really classified against.
        List<SlaZoneDto> slaZones
) {
}
