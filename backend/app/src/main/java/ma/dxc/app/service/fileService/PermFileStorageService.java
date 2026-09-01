package ma.dxc.app.service.fileService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

public interface PermFileStorageService {
    Path buildFilePath() throws IOException;
    CompletableFuture<Long> store(MultipartFile file) throws IOException;
}