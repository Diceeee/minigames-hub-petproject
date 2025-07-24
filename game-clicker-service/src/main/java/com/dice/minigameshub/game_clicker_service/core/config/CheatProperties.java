package com.dice.minigameshub.game_clicker_service.core.config;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("game.cheat")
public class CheatProperties {

    @Positive
    Integer allowedClicksTimeWindowInSeconds = 10;
    @Positive
    Integer allowedClicksPerTimeWindow = allowedClicksTimeWindowInSeconds * 10;
}
