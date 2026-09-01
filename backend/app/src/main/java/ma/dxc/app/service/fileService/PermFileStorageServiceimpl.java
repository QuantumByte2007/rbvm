package ma.dxc.app.service.fileService;

import ma.dxc.app.FileProperties;
import ma.dxc.app.config.AsyncConfig;
import ma.dxc.app.entity.File;
import ma.dxc.app.repository.FileRepository;
import org.apache.tika.Tika;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
public class PermFileStorageServiceimpl extends FileStorageUtil implements PermFileStorageService {
    public PermFileStorageServiceimpl(FileRepository fileRepository, FileProperties fileProperties, Tika tika) {
        super(fileRepository, fileProperties, tika);
    }

    @Override
    public Path buildFilePath() throws IOException {
        LocalDate now = LocalDate.now();
        Path datePath = rootPath.resolve(String.valueOf(now.getYear()))
                .resolve(String.format("%02d", now.getMonthValue()))
                .resolve(String.format("%02d", now.getDayOfMonth()));
        return Files.createDirectories(datePath);
    }

    @Async(AsyncConfig.IO_EXECUTOR)
    @Override
    public CompletableFuture<Long> store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        UUID uuid = UUID.randomUUID();
        // the next instruction will certainley break the SOLID principles
        File f = null;
        try {
            f = new File(uuid,null,file.getSize(), detectMimeType(file),null, file.getOriginalFilename(),computeHash(file),false);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        fileRepository.save(f);
        Path directory = buildFilePath();
        Path path = directory.resolve(uuid.toString()+getExtensionFromMimeType(detectMimeType(file)));

        return CompletableFuture.completedFuture(writeOnDisk(file, path));
    }


}
