package com.dice.auth.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Clock;
import java.util.Date;

@EnableAsync
@Configuration
public class CoreConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public io.jsonwebtoken.Clock jjwtClock(Clock clock) {
        return () -> Date.from(clock.instant());
    }

    @Bean
    public TaskExecutor taskExecutor() {
        int cpus = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpus);
        executor.setMaxPoolSize(cpus * 3);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
