package com.dice.auth.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Date;

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
}
