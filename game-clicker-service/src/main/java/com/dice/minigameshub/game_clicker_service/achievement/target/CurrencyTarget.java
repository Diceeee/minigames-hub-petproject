package com.dice.minigameshub.game_clicker_service.achievement.target;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurrencyTarget extends AchievementTarget {
    private CurrencyTargetType currencyTargetType;
    private long amount;
}
