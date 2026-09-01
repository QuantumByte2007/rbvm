package ma.dxc.app.repository;

import ma.dxc.app.entity.RiskSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskSnapshotRepository extends JpaRepository<RiskSnapshot, Long> {

    List<RiskSnapshot> findAllByOrderByComputedAtAsc();
}
