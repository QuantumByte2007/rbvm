package ma.dxc.app.dto;

import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.SlaStatus;

import java.time.Instant;
import java.util.List;

// Every intermediate term behind a Host's ARS (pfa_formule.pdf eq. 1), for the
// /assets/{id} detail page - mirrors RiskCalculationService.applyRiskScore's math
// exactly, just returning the terms instead of mutating Host.
public record RiskBreakdownDto(
        Long hostId,
        String dns,
        String ip,
        String role,
        String env,
        String cmdbOwner,

        double roleWeight,
        double envWeight,
        double acs,

        List<SeverityTermDto> severityTerms,
        double weightedQds,

        boolean internetExposed,
        double iExternal,

        double mSoc,
        boolean mSocApplied,
        double mAv,
        boolean mAvApplied,
        double mPatch,
        boolean mPatchApplied,
        double mControls,

        double ars,
        RiskClass riskClass,
        SlaStatus slaStatus,
        Instant slaDueDate,
        Double ageInDays
) {
    public record SeverityTermDto(String severity, double avgQds, double weight, long count, double product) {
    }
}
