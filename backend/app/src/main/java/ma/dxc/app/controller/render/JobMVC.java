package ma.dxc.app.controller.render;


import ma.dxc.app.entity.Type;
import ma.dxc.app.service.JobService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Controller
public class JobMVC {
    private final JobService jobService;

    public JobMVC(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/joblaunch/{type}")
    public CompletableFuture<String> jobLaunch(Model model,
    @PathVariable Type type,
    @RequestHeader(value = "HX-Request",required = false) String hx){
        // Returning the future (rather than discarding it, as before) lets Spring await
        // it and route a failed launch (e.g. FileProcessedException) to
        // ExceptionHandlerAdvice instead of always reporting fake success.
        return jobService.launchJob(type).thenApply(status -> {
            model.addAttribute("message", "Le job " + type + " a été lancé avec succès (" + status + ")");
            model.addAttribute("status", status.toString());
            if ("true".equals(hx)) {
                return "partials/datasource-content :: result";
            }
            return "pages/DataSources";
        });
    }



}
