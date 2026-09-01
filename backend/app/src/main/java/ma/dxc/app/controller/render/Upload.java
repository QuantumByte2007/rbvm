package ma.dxc.app.controller.render;


import ma.dxc.app.entity.Type;
import ma.dxc.app.service.fileService.TempFileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Controller
public class Upload {

    private final TempFileStorageService tempFileStorageService;

    public Upload(TempFileStorageService tempFileStorageService) {
        this.tempFileStorageService = tempFileStorageService;
    }

    @GetMapping("/upload")
    public String uploadPage(){
        return "upload";
    }
    @GetMapping("/joblaunch")
    public String getJobPage(@RequestHeader(value = "HX-Request", required = false) String hx){
        return "true".equals(hx) ? "partials/datasource-content :: content" : "pages/DataSources";
    }

    @PostMapping("/upload/{type}")
    public CompletableFuture<String> uploadFile(@PathVariable Type type,
                                                 @RequestParam MultipartFile file,
                                                 Model model,
                                                 @RequestHeader(value = "HX-Request", required = false) String hx) throws IOException {
        return tempFileStorageService.store(file, type).thenApply(id -> {
            model.addAttribute("message", "Fichier " + type + " uploadé avec succès");
            model.addAttribute("status", "COMPLETED");
            return "true".equals(hx) ? "partials/datasource-content :: result" : "pages/DataSources";
        });
    }
}
