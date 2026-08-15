package ma.dxc.app.service.fileService;


import ma.dxc.app.FileProperties;
import ma.dxc.app.repository.FileRepository;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public abstract class FileStorageUtil {
    protected final FileRepository fileRepository;
    protected final Path rootPath;
    protected final Tika tika;

    public FileStorageUtil(FileRepository fileRepository, FileProperties fileProperties, Tika tika) {
        this.fileRepository = fileRepository;
        this.rootPath = Paths.get(fileProperties.getBasePath());
        this.tika = tika;
    }
    protected String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            return "";
        }
        // The subtype is the part after the "/"
        // e.g. "application/xml" -> "xml"
        return mimeType.substring(mimeType.lastIndexOf('/') + 1);
    }


    protected String detectMimeType(MultipartFile multipartFile) throws IOException {
        try(InputStream in = multipartFile.getInputStream()){
        return  tika.detect(in);}
    }

    protected long writeOnDisk(MultipartFile multipartFile, Path destinationPath) throws IOException {
        try(InputStream in = multipartFile.getInputStream()){
            return Files.copy(in,destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }




    //the store() method takes an input stream coming from network then it writes on disk

}