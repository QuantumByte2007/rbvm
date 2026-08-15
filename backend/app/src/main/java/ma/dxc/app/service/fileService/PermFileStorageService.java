package ma.dxc.app.service.fileService;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface PermFileStorageService {
    Path buildFilePath() throws IOException;
    long store(MultipartFile file) throws IOException;
}