package ma.dxc.app.service.fileService;

import ma.dxc.app.FileProperties;
import ma.dxc.app.entity.Type;
import ma.dxc.app.repository.FileRepository;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TempFileStorageServiceimpl extends FileStorageUtil implements TempFileStorageService {
    public TempFileStorageServiceimpl(FileRepository fileRepository, FileProperties fileProperties, Tika tika) {
        super(fileRepository, fileProperties, tika);
    }

    @Override
    public Path buildFilePath(Type type) throws IOException {
        return Files.createDirectories(rootPath.resolve(type.name().toLowerCase()));
    }


    @Override
    public long store(MultipartFile file,Type type) throws IOException {
        String extension = getExtensionFromMimeType(detectMimeType(file));
        Path path = buildFilePath(type).resolve(type.name().toLowerCase() +"."+ extension);
        return writeOnDisk(file,path);
    }
}