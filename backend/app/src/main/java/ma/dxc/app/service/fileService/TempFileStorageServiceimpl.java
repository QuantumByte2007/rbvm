package ma.dxc.app.service.fileService;

import ma.dxc.app.FileProperties;
import ma.dxc.app.config.AsyncConfig;
import ma.dxc.app.entity.File;
import ma.dxc.app.entity.Type;
import ma.dxc.app.execption.FileNotUploaded;
import ma.dxc.app.execption.FileProcessedException;
import ma.dxc.app.repository.FileRepository;
import org.apache.commons.io.FileExistsException;
import org.apache.tika.Tika;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    public boolean getStatus(Type type) {
        String normalizedType = type.name().toLowerCase();
        boolean status = false;

            Optional<File> opfile = fileRepository.getFileByName(normalizedType);
            if (opfile.isPresent()) {
                File file = opfile.get();
                status = file.isProcessed();
                return status;
            }else {
            throw new FileNotUploaded("please upload file first");}

    }
    @Transactional
    @Override
    public void process(Type type) {
        String normalizedType = type.name().toLowerCase();
        try {
            Optional<File> opfile = fileRepository.getFileByName(normalizedType);
            if(opfile.isPresent()){
                File file = opfile.get();
                if(!file.isProcessed()){
                    file.setProcessed();
                    fileRepository.save(file);
                }   else {
                throw new FileProcessedException();}

            }
            else { throw new FileNotUploaded("please upload file");}
        } catch(Exception e){
            throw new RuntimeException("File could not be processed, please retry or contact ADMIN",e);
        }

    }

    @Override
    public String getFilePattern(Type type) {
        return super.getFilePattern(type);
    }

    @Async(AsyncConfig.IO_EXECUTOR)
    @Override
    public CompletableFuture<Long> store(MultipartFile file,Type type) throws IOException {

        try {
            String digest = computeHash(file);
            if(fileRepository.existsByHash(digest)){
                throw new FileProcessedException();
            }
            // A re-upload of the same type replaces its bookkeeping row rather than
            // accumulating one per upload - getFileByName/getStatus expect exactly one
            // row per type, and a second row makes that lookup throw
            // IncorrectResultSizeDataAccessException.
            fileRepository.deleteByName(type.name().toLowerCase());
            fileRepository.save(new File(UUID.randomUUID(),null,file.getSize(),detectMimeType(file), Instant.now(), type.name().toLowerCase(), digest,false));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String extension = getExtensionFromMimeType(detectMimeType(file));
        Path path = buildFilePath(type).resolve(type.name().toLowerCase() +"."+ extension);
        return CompletableFuture.completedFuture(writeOnDisk(file,path));
    }
}

//getFile().process().save().orElseThrow()
//