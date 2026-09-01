package ma.dxc.app.dto;

import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.SlaStatus;

import java.time.Instant;

public record SlaPointDto(String dns, Instant slaDueDate, Double ars, Double ageInDays,
                           RiskClass riskClass, SlaStatus slaStatus) {
}
