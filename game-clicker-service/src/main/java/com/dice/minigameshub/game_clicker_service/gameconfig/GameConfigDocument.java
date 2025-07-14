package com.dice.minigameshub.game_clicker_service.gameconfig;

import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document(collection = "game_config")
public class GameConfigDocument {

    public static final String GAME_CONFIG_DOCUMENT_ID = "game_config";

    @Id
    String id;

    @Positive
    Integer basicCurrencyGainPerClick;

    /**
     * Limits time in hours for passive income to accumulate.
     */
    @Positive
    Integer maxHoursAllowedForPassiveIncome;
}
