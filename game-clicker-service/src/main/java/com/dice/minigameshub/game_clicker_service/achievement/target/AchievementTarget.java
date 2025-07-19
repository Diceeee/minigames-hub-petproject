package com.dice.minigameshub.game_clicker_service.achievement.target;

import com.dice.minigameshub.game_clicker_service.achievement.AchievementType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CurrencyTarget.class, name = AchievementType.CURRENCY),
        @JsonSubTypes.Type(value = ItemTarget.class, name = AchievementType.ITEM)
})
public abstract class AchievementTarget {
    private String name;
    private String description;
}
