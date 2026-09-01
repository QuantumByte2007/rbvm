package ma.dxc.app.repository;

import ma.dxc.app.entity.CmdbCi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CmdbCiRepository extends JpaRepository<CmdbCi, String> {
    Optional<CmdbCi> findByIp(String ip);

    // CMDB entries with no matching Qualys host - registered assets nobody has ever
    // scanned. Surfaced as Shadow IT alongside the reverse case (see HostRepository).
    @Query("SELECT c FROM CmdbCi c WHERE NOT EXISTS (SELECT 1 FROM Host h WHERE h.ip = c.ip)")
    List<CmdbCi> findAllWithoutHost();
}
