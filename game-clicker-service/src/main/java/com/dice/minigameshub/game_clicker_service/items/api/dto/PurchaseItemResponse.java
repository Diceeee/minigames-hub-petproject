package com.dice.minigameshub.game_clicker_service.items.api.dto;

import com.dice.minigameshub.game_clicker_service.achievement.api.dto.AchievementStateResponse;
import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PurchaseItemResponse {
    String itemId;
    int currencyPayedForItemPurchase;
    int currencyIncomePerClickBefore;
    int currencyIncomePerClickAfter;
    int currencyIncomePerMinuteBefore;
    int currencyIncomePerMinuteAfter;
    List<AchievementStateResponse> achievementStates;
    UserStatistics userStatistics;
}
