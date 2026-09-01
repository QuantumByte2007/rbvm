package ma.dxc.app.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String IO_EXECUTOR = "ioTaskExecutor";
    public static final String CALC_EXECUTOR = "calcTaskExecutor";

    // IO-bound work (file hashing/storage, batch job launch): threads mostly wait on disk/DB,
    // so we can afford more of them than CPU cores.
    @Bean(IO_EXECUTOR)
    public Executor ioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("io-exec-");
        executor.initialize();
        return executor;
    }

    // Risk calculation fan-out (ACS lookup, weighted QDS per severity, external/mitigation
    // factors computed in parallel per asset): bounded to roughly available cores, but the
    // queue needs headroom for 2 futures per host (acs + weightedQds) submitted essentially
    // at once by recomputeAll() - at fleet sizes of a few hundred hosts, a queue capacity
    // matching the old pool size (200) rejects the overflow outright.
    @Bean(CALC_EXECUTOR)
    public Executor calcTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(Math.max(2, cores));
        executor.setMaxPoolSize(Math.max(8, cores * 4));
        executor.setQueueCapacity(5000);
        executor.setThreadNamePrefix("calc-exec-");
        executor.initialize();
        return executor;
    }
}