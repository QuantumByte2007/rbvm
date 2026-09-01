package ma.dxc.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// One row per RiskCalculationService.recomputeAll() run: aggregate counts per SLA
// class, feeding the dashboard's history/trend chart (there's no other source of
// time-series data since Host only ever holds the latest computed values).
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Instant computedAt;
    private long blackCount;
    private long redCount;
    private long orangeCount;
    private long yellowCount;
    private long greenCount;
    private Double avgArs;
}
