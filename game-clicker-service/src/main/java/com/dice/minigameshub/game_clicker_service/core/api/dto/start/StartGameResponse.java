package com.dice.minigameshub.game_clicker_service.core.api.dto.start;

import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementStateResponse;
import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StartGameResponse {
    long currency;
    long currencyIncomePerClick;
    long currencyIncomePerMinute;

    boolean firstStart;

    UserStatistics userStatistics;
    List<String> purchasedItemsIds;
    List<AchievementStateResponse> achievementStates;
}
