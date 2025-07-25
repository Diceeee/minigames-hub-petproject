package com.dice.minigameshub.game_clicker_service.core.dto.clicks;

import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ClicksProcessResult {
    Long currencyBeforeClicks;
    Long currencyAfterClicks;
    UserStatistics userStatistics;
    List<AchievementState> achievementStates;
}
