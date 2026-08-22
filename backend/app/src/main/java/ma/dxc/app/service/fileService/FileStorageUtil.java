package ma.dxc.app.service.fileService;


import ma.dxc.app.FileProperties;
import ma.dxc.app.repository.FileRepository;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

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


    public String detectMimeType(MultipartFile multipartFile) throws IOException {
        try(InputStream in = multipartFile.getInputStream()){
        return  getExtensionFromMimeType(tika.detect(in));}
    }
    //we probably need to compute file hash before writing on disk.
    protected String computeHash(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        try(InputStream in = multipartFile.getInputStream()){
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) !=-1){


                md.update(buffer,0,bytesRead);


            }
            byte[] digest = md.digest();

            return HexFormat.of().formatHex(digest);
        }
    }
    protected long writeOnDisk(MultipartFile multipartFile, Path destinationPath) throws IOException {
        try(InputStream in = multipartFile.getInputStream()){
            return Files.copy(in,destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }




    //the store() method takes an input stream coming from network then it writes on disk

}