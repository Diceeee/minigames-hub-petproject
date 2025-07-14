package com.dice.minigameshub.game_clicker_service.gameconfig;

import lombok.Value;

@Value
public class GameConfig {

    /**
     * Amount of currency gain per one click.
     */
    Integer basicCurrencyGainPerClick;

    /**
     * Limits time in hours for passive income to accumulate.
     */
    Integer maxHoursAllowedForPassiveIncome;
}
