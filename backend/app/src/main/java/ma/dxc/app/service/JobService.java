package ma.dxc.app.service;


import ma.dxc.app.entity.Type;
import ma.dxc.app.execption.FileProcessedException;
import ma.dxc.app.service.fileService.TempFileStorageService;
import ma.dxc.app.service.fileService.TempFileStorageServiceimpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class JobService {

    private final JobOperator jobOperator;
    private final Map<String, Job> allJobs;
    private final TempFileStorageServiceimpl fileService;
    private final Logger logger = LoggerFactory.getLogger(JobService.class);
    public JobService(JobOperator jobOperator, Map<String, Job> allJobs, TempFileStorageServiceimpl fileService) {
        this.jobOperator = jobOperator;
        this.allJobs = allJobs;
        this.fileService = fileService;
    }
    private JobParameters initJobParam(Type type){
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input",fileService.getFilePattern(type))
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        return jobParameters;
    }

    protected Job getJob(Type type){
        return allJobs.get(type.name().toLowerCase()+"Job");
    }
    @Async
    public CompletableFuture<BatchStatus> launchJob(Type type){
        return CompletableFuture.supplyAsync(()-> {
                logger.info("calling from another thread");
        try {
            if(!fileService.getStatus(type)){
                fileService.process(type);
                JobExecution jobExecution = jobOperator.start(getJob(type),initJobParam(type));
                return jobExecution.getStatus();
            } else {
                throw new FileProcessedException();
            }
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 InvalidJobParametersException | JobRestartException e) {
            throw new RuntimeException(e);
        } });

    }
}
