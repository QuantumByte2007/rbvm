package ma.dxc.app.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.dxc.app.entity.Type;
import ma.dxc.app.service.fileService.PermFileStorageService;
import ma.dxc.app.service.fileService.PermFileStorageServiceimpl;
import ma.dxc.app.service.fileService.TempFileStorageService;
import ma.dxc.app.service.fileService.TempFileStorageServiceimpl;
import ma.dxc.app.validation.CheckXml;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/upload")
@Validated
public class FileController {
    private final PermFileStorageService fileService;
    private final TempFileStorageService tempFileService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<?>> uploadFile(@RequestParam MultipartFile file) throws IOException {
        return fileService.store(file).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/tempfile/{type}")
    public CompletableFuture<ResponseEntity<?>> uploadCmdb(@CheckXml @RequestParam MultipartFile file, @PathVariable Type type) throws IOException {
        return tempFileService.store(file,type).thenApply(ResponseEntity::ok);
    }



}
