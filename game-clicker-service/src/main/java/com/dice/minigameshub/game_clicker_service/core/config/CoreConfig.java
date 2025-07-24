package com.dice.minigameshub.game_clicker_service.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(CheatProperties.class)
public class CoreConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
