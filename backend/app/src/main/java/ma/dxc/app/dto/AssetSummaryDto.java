package ma.dxc.app.dto;

import ma.dxc.app.entity.Env;
import ma.dxc.app.entity.ReconciliationStatus;
import ma.dxc.app.entity.RiskClass;
import ma.dxc.app.entity.SlaStatus;

import java.time.Instant;

// Reconciliation row for GET /assets (both the HTML table and the REST/mobile endpoint).
// A row with a non-RECONCILED status is "Shadow IT": either a Qualys-scanned host with no
// matching CMDB entry (reconciliation == SHADOW_QUALYS_ONLY, id/acs/ars/... populated as
// usual - just unscoreable without CMDB context), or a CMDB entry nobody has scanned
// (SHADOW_CMDB_ONLY, id null - there's no Host row to link to).
public record AssetSummaryDto(
        Long id,
        String dns,
        String ip,
        Env env,
        Double acs,
        Double ars,
        RiskClass riskClass,
        SlaStatus slaStatus,
        Instant slaDueDate,
        ReconciliationStatus reconciliation
) {
    public boolean isShadowIt() {
        return reconciliation != null && reconciliation != ReconciliationStatus.RECONCILED;
    }
}
