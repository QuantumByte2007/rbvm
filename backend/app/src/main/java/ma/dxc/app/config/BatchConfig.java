package ma.dxc.app.config;

import jakarta.persistence.EntityManagerFactory;
import ma.dxc.app.FileProperties;
import ma.dxc.app.dto.CmdbDto;
import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.entity.CmdbCi;
import ma.dxc.app.entity.Host;
import ma.dxc.app.service.CmdbProcessor;
import ma.dxc.app.service.OsTypeProcessor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class BatchConfig {
    private final Path rootPath;

    public BatchConfig(FileProperties fileProperties) {
        this.rootPath = Paths.get(fileProperties.getBasePath());
    }

    @Bean
    @StepScope
    StaxEventItemReader<Hostdto> multiEntityReader(Jaxb2Marshaller jaxb2Marshaller) {

        StaxEventItemReader<Hostdto> staxEventReader = new StaxEventItemReader<>(jaxb2Marshaller);
        staxEventReader.setFragmentRootElementNames(new String[]{"HOST"});


        return staxEventReader;
    }
    @Bean
    @StepScope
    StaxEventItemReader<CmdbDto> cmdbEnityReader(Jaxb2Marshaller jaxb2Marshaller){
        StaxEventItemReader<CmdbDto> staxEventItemReader = new StaxEventItemReader<>(jaxb2Marshaller);
        staxEventItemReader.setFragmentRootElementNames(new String[]{"row"});
    return staxEventItemReader;}



    @Bean
    @StepScope
    public MultiResourceItemReader<Hostdto> multiReader(StaxEventItemReader<Hostdto> staxEventItemReader,
                                                        @Value("#{jobParameters['input']}") String pattern)
            throws IOException {

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        MultiResourceItemReader<Hostdto> itemReader = new MultiResourceItemReader<>(staxEventItemReader);
        itemReader.setResources(resources);
        return itemReader;
    }
    @Bean
    @StepScope
    public MultiResourceItemReader<CmdbDto> cmdbReader(StaxEventItemReader<CmdbDto> staxEventItemReader,
                                                        @Value("#{jobParameters['input']}") String pattern)
            throws IOException {

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        MultiResourceItemReader<CmdbDto> itemReader = new MultiResourceItemReader<>(staxEventItemReader);
        itemReader.setResources(resources);
        return itemReader;
    }

    @Bean
    public Job qualysJob(JobRepository jobRepository,@Qualifier("qualysStep") Step step) {
        return new JobBuilder("qualysingestion", jobRepository)
                .start(step)
                .build();

    }
    @Bean
    public Job cmdbJob(JobRepository jobRepository,@Qualifier("cmdbStep") Step step){
        return new JobBuilder("cmdbingestion", jobRepository)
                .start(step)
                .build();
    }


    @Bean
    public Step qualysStep(JobRepository jobRepository,
                        MultiResourceItemReader<Hostdto> multiResourceItemReader,
                        PlatformTransactionManager transactionManager,
                        JpaItemWriter<Host> hostWriter,
                        OsTypeProcessor osTypeProcessor) {
        return new StepBuilder(jobRepository)
                .<Hostdto, Host>chunk(100)
                .transactionManager(transactionManager)
                .reader(multiResourceItemReader)
                .processor(osTypeProcessor)
                .writer(hostWriter)
                .build();
    }
    @Bean
    public Step cmdbStep(JobRepository jobRepository, @Qualifier("cmdbReader")
                        MultiResourceItemReader<CmdbDto> multiResourceItemReader,
                         PlatformTransactionManager transactionManager,
                         JpaItemWriter<CmdbCi> cmdbCiJpaItemWriter,
                         CmdbProcessor cmdbProcessor) {
        return new StepBuilder(jobRepository)
                .<CmdbDto, CmdbCi>chunk(100)
                .transactionManager(transactionManager)
                .reader(multiResourceItemReader)
                .processor(cmdbProcessor)
                .writer(cmdbCiJpaItemWriter)
                .build();
    }

    @Bean
    public JpaItemWriter<Host> hostWriter(EntityManagerFactory emf) {
        return new JpaItemWriter<>(emf);
    }
    @Bean
    public JpaItemWriter<CmdbCi> cmdbCiJpaItemWriter(EntityManagerFactory emf){
        return new JpaItemWriter<>(emf);
    }
}
