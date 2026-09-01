package ma.dxc.app.repository;

import ma.dxc.app.entity.File;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {
    Optional<File> getFileByName(@Nullable String originalFilename);

    boolean existsByHash(String hash);

    // store() (the only caller) isn't itself @Transactional and runs on the IO_EXECUTOR
    // thread, so this derived delete needs its own transaction boundary or
    // EntityManager.remove() throws "No EntityManager with actual transaction available".
    @Transactional
    void deleteByName(String name);

}
