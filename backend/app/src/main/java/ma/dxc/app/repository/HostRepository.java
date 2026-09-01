package ma.dxc.app.repository;

import ma.dxc.app.dto.AssetSummaryDto;
import ma.dxc.app.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {

    @Query("SELECT new ma.dxc.app.dto.AssetSummaryDto(h.id, h.dns, h.ip, c.env, h.acs, h.ars, h.riskClass, h.slaStatus, h.slaDueDate, " +
            "CASE WHEN c.ip IS NULL THEN ma.dxc.app.entity.ReconciliationStatus.SHADOW_QUALYS_ONLY ELSE ma.dxc.app.entity.ReconciliationStatus.RECONCILED END) " +
            "FROM Host h left JOIN CmdbCi c on h.ip = c.ip ")
    List<AssetSummaryDto> findAllQualysHostWithOptionalCmdb();

    // Eager-loads vulns in one query so RiskCalculationService can hand each Host
    // straight to a background thread without triggering lazy-loading there.
    @Query("SELECT DISTINCT h FROM Host h LEFT JOIN FETCH h.vulns")
    List<Host> findAllWithVulns();

    // Same shape as findAllWithVulns, filtered to one host - for the /assets/{id}
    // detail page's ARS formula breakdown.
    @Query("SELECT h FROM Host h LEFT JOIN FETCH h.vulns WHERE h.id = :id")
    java.util.Optional<Host> findByIdWithVulns(Long id);
}
