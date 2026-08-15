package ma.dxc.app.controller;


import lombok.AllArgsConstructor;
import ma.dxc.app.entity.Type;
import ma.dxc.app.service.fileService.PermFileStorageService;
import ma.dxc.app.service.fileService.PermFileStorageServiceimpl;
import ma.dxc.app.service.fileService.TempFileStorageService;
import ma.dxc.app.service.fileService.TempFileStorageServiceimpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
public class FileController {
    private final PermFileStorageService fileService;
    private final TempFileStorageService tempFileService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.store(file));
    }

    @PostMapping("/tempfile/{type}")
    public ResponseEntity<?> uploadCmdb(@RequestParam MultipartFile file, @PathVariable Type type) throws IOException {
        return ResponseEntity.ok(tempFileService.store(file,type));
    }



}
