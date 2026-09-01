package ma.dxc.app.dto;

import java.time.Instant;

public record RiskSnapshotDto(Instant computedAt, long black, long red, long orange, long yellow, long green, Double avgArs) {
}
