package ma.dxc.app.controller;


import ma.dxc.app.entity.Type;
import ma.dxc.app.service.JobService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/v1/job")

public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @PostMapping("/launchJob/{type}")
    ResponseEntity<CompletableFuture<BatchStatus>> launchJob(@PathVariable Type type)  {
        return ResponseEntity.ok(jobService.launchJob(type));
    }







}
