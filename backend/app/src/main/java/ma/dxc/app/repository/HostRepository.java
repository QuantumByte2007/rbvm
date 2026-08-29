package ma.dxc.app.repository;

import ma.dxc.app.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {

    @Query("SELECT h.dns,c.env FROM Host h left JOIN CmdbCi c on h.ip = c.ip ")
    List<Object[]> findAllQualysHostWithOptionalCmdb();
}
