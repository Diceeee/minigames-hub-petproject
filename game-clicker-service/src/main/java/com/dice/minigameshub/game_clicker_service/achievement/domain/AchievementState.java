package com.dice.minigameshub.game_clicker_service.achievement.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AchievementState {

    String achievementId;
    boolean completed;
    Progress progress;
}
