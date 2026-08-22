package ma.dxc.app.service.fileService;

import ma.dxc.app.FileProperties;
import ma.dxc.app.entity.File;
import ma.dxc.app.entity.Type;
import ma.dxc.app.execption.FileProcessedException;
import ma.dxc.app.repository.FileRepository;
import org.apache.commons.io.FileExistsException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

@Service
public class TempFileStorageServiceimpl extends FileStorageUtil implements TempFileStorageService {
    private final FileRepository fileRepository;
    public TempFileStorageServiceimpl(FileRepository fileRepository, FileProperties fileProperties, Tika tika, FileRepository fileRepository1) {
        super(fileRepository, fileProperties, tika);
        this.fileRepository = fileRepository1;
    }

    @Override
    public String detectMimeType(MultipartFile multipartFile) throws IOException {
        return super.detectMimeType(multipartFile);
    }

    @Override
    public Path buildFilePath(Type type) throws IOException {
        return Files.createDirectories(rootPath.resolve(type.name().toLowerCase()));
    }


    @Override
    public long store(MultipartFile file,Type type) throws IOException {

        try {
            String digest = computeHash(file);
            if(fileRepository.existsByHash(digest)){
                throw new FileProcessedException();
            }
            fileRepository.save(new File(UUID.randomUUID(),null,file.getSize(),detectMimeType(file), Instant.now(), type.name().toLowerCase(), digest,false));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String extension = getExtensionFromMimeType(detectMimeType(file));
        Path path = buildFilePath(type).resolve(type.name().toLowerCase() +"."+ extension);
        return writeOnDisk(file,path);
    }
}