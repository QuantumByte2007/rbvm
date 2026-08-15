package ma.dxc.app.controller;


import ma.dxc.app.FileProperties;
import ma.dxc.app.entity.Type;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.batch.core.step.Step;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vi/job")

public class JobController {
    private FileProperties fileProperties;
    private final String path;
    private final JobOperator jobOperator;
    private final Job jobInstnace;
    private final Step step;
    @PostMapping("/launchJob/{type}")
    ResponseEntity<?> launchJob(@PathVariable Type type) throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input", "file:"+ fileProperties.getBasePath()+"/"+type.name().toLowerCase()+"/"+type.name().toLowerCase()+"*.xml")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobOperator.start(jobInstnace,jobParameters);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    public JobController(FileProperties fileProperties, JobOperator jobOperator, Job jobInstnace, Step step) {
        this.fileProperties = fileProperties;
        this.path = fileProperties.getBasePath();
        this.jobOperator = jobOperator;
        this.jobInstnace = jobInstnace;
        this.step = step;
    }




}
