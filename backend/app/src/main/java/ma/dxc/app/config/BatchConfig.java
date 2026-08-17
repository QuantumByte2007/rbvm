package ma.dxc.app.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import ma.dxc.app.FileProperties;
import ma.dxc.app.dto.Hostdto;
import ma.dxc.app.dto.Qiddto;
import ma.dxc.app.dto.VulnInfoDto;
import ma.dxc.app.entity.Host;
import ma.dxc.app.entity.VulnInfo;
import ma.dxc.app.service.OsTypeProcessor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.ChunkOrientedStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.item.ChunkOrientedStep;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.MultiResourceItemReader;
import org.springframework.batch.infrastructure.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.infrastructure.item.support.CompositeItemWriter;
import org.springframework.batch.infrastructure.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.infrastructure.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.classify.Classifier;
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
import java.util.List;


@Configuration
public class BatchConfig {
    private final Path rootPath;

    public BatchConfig(FileProperties fileProperties) {
        this.rootPath = Paths.get(fileProperties.getBasePath());
    }

    @Bean
    @StepScope
    StaxEventItemReader<Object> multiEntityReader(Jaxb2Marshaller jaxb2Marshaller) {

        StaxEventItemReader<Object> staxEventReader = new StaxEventItemReader<>(jaxb2Marshaller);
        staxEventReader.setFragmentRootElementNames(new String[]{"HOST"});


        return staxEventReader;
    }



    @Bean
    @StepScope
    public MultiResourceItemReader<Object> multiReader(StaxEventItemReader<Object> staxEventItemReader,
                                                       @Value("#{jobParameters['input']}") String pattern)
            throws IOException {

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        MultiResourceItemReader<Object> itemReader = new MultiResourceItemReader<>(staxEventItemReader);
        itemReader.setResources(resources);
        return itemReader;
    }

    @Bean
    public Job getJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("xmlingestion", jobRepository)
                .start(step)
                .build();

    }


    @Bean
    public Step getStep(JobRepository jobRepository,
                        MultiResourceItemReader<Object> multiResourceItemReader,
                        PlatformTransactionManager transactionManager,
                        ClassifierCompositeItemWriter<Object> classifier,
                        OsTypeProcessor osTypeProcessor) {
        return new StepBuilder(jobRepository)
                .chunk(100)
                .transactionManager(transactionManager)
                .reader(multiResourceItemReader)
                .processor(osTypeProcessor)
                .writer(classifier)
                .build();





    }

    @Bean
    public ClassifierCompositeItemWriter<Object> classifierWriter(
            JpaItemWriter<Host> hostWriter,
            JpaItemWriter<VulnInfo> vulnWriter,
            JpaItemWriter<Qiddto> qidWriter) {

        Classifier<Object, ItemWriter<? super Object>> classifier = item -> {
            if (item instanceof Hostdto)   return (ItemWriter) hostWriter;
            if (item instanceof VulnInfo) return (ItemWriter) vulnWriter;
            return (ItemWriter) qidWriter;
        };

        ClassifierCompositeItemWriter<Object> writer = new ClassifierCompositeItemWriter<>();
        writer.setClassifier(classifier);
        return writer;
    }

    @Bean
    public JpaItemWriter<Host> hostWriter(EntityManagerFactory emf) {
        JpaItemWriter<Host> w = new JpaItemWriter<>(emf);
        return w;
    }
    @Bean
    public JpaItemWriter<VulnInfo> vulnWriter(EntityManagerFactory emf) {
        JpaItemWriter<VulnInfo> w = new JpaItemWriter<>(emf);
        return w;
    }
    @Bean
    public JpaItemWriter<Qiddto> qidtWriter(EntityManagerFactory emf) {
        JpaItemWriter<Qiddto> w = new JpaItemWriter<>(emf);
        return w;
    }


}





