package com.dice.minigameshub.game_clicker_service.achievement.api.dto;

import com.dice.minigameshub.game_clicker_service.achievement.target.AchievementTarget;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AchievementResponse {
    String id;
    AchievementTarget target;
}
