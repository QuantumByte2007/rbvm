package ma.dxc.app.service.fileService;

import ma.dxc.app.entity.Type;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface TempFileStorageService {
    long store(MultipartFile file, Type type) throws IOException;
    Path buildFilePath(Type type) throws IOException;
}
