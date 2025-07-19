package com.dice.minigameshub.game_clicker_service.achievement.target;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemTarget extends AchievementTarget {
    private ItemTargetType itemTargetType;
    private String itemId;
}
