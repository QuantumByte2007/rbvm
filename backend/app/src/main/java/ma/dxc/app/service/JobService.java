package ma.dxc.app.service;


import ma.dxc.app.config.AsyncConfig;
import ma.dxc.app.entity.Type;
import ma.dxc.app.event.IngestionCompletedEvent;
import ma.dxc.app.execption.FileProcessedException;
import ma.dxc.app.repository.CmdbCiRepository;
import ma.dxc.app.repository.HostRepository;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Transactional
public class JobService {

    private final JobOperator jobOperator;
    private final Map<String, Job> allJobs;
    private final TempFileStorageServiceimpl fileService;
    private final ApplicationEventPublisher eventPublisher;
    private final Executor ioExecutor;
    private final HostRepository hostRepository;
    private final CmdbCiRepository cmdbCiRepository;
    private final Logger logger = LoggerFactory.getLogger(JobService.class);
    // The cmdb/qualys readers are @StepScope beans resolved via jobParameters['input']
    // late-binding, which is thread-context based - running two ingestion jobs at once
    // let one step pick up the other's file pattern. Guard start() so only one batch
    // job executes at a time.
    private final ReentrantLock jobLock = new ReentrantLock();

    public JobService(JobOperator jobOperator, Map<String, Job> allJobs, TempFileStorageServiceimpl fileService,
                       ApplicationEventPublisher eventPublisher,
                       @Qualifier(AsyncConfig.IO_EXECUTOR) Executor ioExecutor,
                       HostRepository hostRepository, CmdbCiRepository cmdbCiRepository) {
        this.jobOperator = jobOperator;
        this.allJobs = allJobs;
        this.fileService = fileService;
        this.eventPublisher = eventPublisher;
        this.ioExecutor = ioExecutor;
        this.hostRepository = hostRepository;
        this.cmdbCiRepository = cmdbCiRepository;
    }

    // Each new scan is treated as the full current state of truth for that source -
    // re-ingesting replaces rather than accumulates, so a host/CI no longer present in
    // the new file doesn't linger as stale data, and CmdbCi's natural-key id (ciId)
    // doesn't collide with a prior upload's rows.
    private void wipeForReingest(Type type) {
        if (type == Type.QUALYS) {
            hostRepository.deleteAll(); // cascades to VulnInfo (Host.vulns is CascadeType.ALL)
        } else if (type == Type.CMDB) {
            cmdbCiRepository.deleteAll();
        }
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
    @Async(AsyncConfig.IO_EXECUTOR)
    public CompletableFuture<BatchStatus> launchJob(Type type){
        return CompletableFuture.supplyAsync(()-> {
                logger.info("calling from another thread");
        jobLock.lock();
        try {
            if(!fileService.getStatus(type)){
                fileService.process(type);
                wipeForReingest(type);
                JobExecution jobExecution = jobOperator.start(getJob(type),initJobParam(type));
                BatchStatus status = jobExecution.getStatus();
                if (status == BatchStatus.COMPLETED) {
                    eventPublisher.publishEvent(new IngestionCompletedEvent(type));
                }
                return status;
            } else {
                throw new FileProcessedException();
            }
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                 InvalidJobParametersException | JobRestartException e) {
            throw new RuntimeException(e);
        } finally {
            jobLock.unlock();
        } }, ioExecutor);

    }
}
