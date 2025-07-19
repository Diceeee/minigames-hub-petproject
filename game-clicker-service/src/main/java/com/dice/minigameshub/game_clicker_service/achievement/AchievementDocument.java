package com.dice.minigameshub.game_clicker_service.achievement;

import com.dice.minigameshub.game_clicker_service.achievement.target.AchievementTarget;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Document(collection = "achievements")
public class AchievementDocument {

    @Id
    String id;

    AchievementTarget target;
}
