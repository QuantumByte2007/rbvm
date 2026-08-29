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
    public String jobLaunch(Model model,
    @PathVariable Type type,
    @RequestHeader(value = "HX-Request",required = false) String hx){
        CompletableFuture<BatchStatus> status = jobService.launchJob(type);
        model.addAttribute("message","Le job "+type +"a été lancé avec succées" );
//        model.addAttribute("status",status.wait());
        if("true".equals(hx)){
        return "partials/datasource-content :: result";}
        return "pages/DataSources";
    }



}
