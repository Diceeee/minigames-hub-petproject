package com.dice.minigameshub.game_clicker_service.achievement.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AchievementState {

    String achievementId;
    boolean completed;
    Progress progress;

    public boolean hasProgress() {
        return progress != null;
    }
}
