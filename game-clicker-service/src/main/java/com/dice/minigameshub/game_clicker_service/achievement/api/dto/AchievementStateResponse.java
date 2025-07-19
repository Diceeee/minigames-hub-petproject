package com.dice.minigameshub.game_clicker_service.achievement.api.dto;

import com.dice.minigameshub.game_clicker_service.achievement.dto.Progress;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AchievementStateResponse {

    String achievementId;
    boolean completed;
    Progress progress;
}
