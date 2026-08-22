package ma.dxc.app.controller;


import ma.dxc.app.FileProperties;
import ma.dxc.app.entity.File;
import ma.dxc.app.entity.Type;
import ma.dxc.app.execption.FileProcessedException;
import ma.dxc.app.repository.FileRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobInstance;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/job")

public class JobController {
    private FileProperties fileProperties;
    private final String path;
    private final FileRepository fileRepository;
    private final JobOperator jobOperator;
    private final Map<String,Job> alljobs;
//    private final Job jobInstnace;
//    private final Step step;
    @PostMapping("/launchJob/{type}")
    ResponseEntity<?> launchJob(@PathVariable Type type)  {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input", "file:"+ fileProperties.getBasePath()+"/"+type.name().toLowerCase()+"/"+type.name().toLowerCase()+"*.xml")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        Job jobInstnace = alljobs.get(type.name().toLowerCase()+"Job");
        try {

            Optional<File> opfile = fileRepository.getFileByName(type.name().toLowerCase());
            if(opfile.isPresent()){
                File file = opfile.get();
                if(file.isProcessed() == false){
                    jobOperator.start(jobInstnace,jobParameters);
                    file.setProcessed();
                    fileRepository.save(file);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            } throw new FileProcessedException();

        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 InvalidJobParametersException | JobRestartException e) {
            throw new RuntimeException(e);
        }


    }


    public JobController(FileProperties fileProperties, FileRepository fileRepository, JobOperator jobOperator, Map<String, Job> alljobs) {
        this.fileProperties = fileProperties;
        this.path = fileProperties.getBasePath();
        this.fileRepository = fileRepository;
        this.jobOperator = jobOperator;
        this.alljobs = alljobs;

    }




}
