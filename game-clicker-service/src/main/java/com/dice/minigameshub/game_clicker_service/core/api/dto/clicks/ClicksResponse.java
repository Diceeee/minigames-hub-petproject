package com.dice.minigameshub.game_clicker_service.core.api.dto.clicks;

import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementStateResponse;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ClicksResponse {
    Long currencyBeforeClicks;
    Long currencyAfterClicks;
    List<AchievementStateResponse> achievementStates;
}
