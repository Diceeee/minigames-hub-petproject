package com.dice.minigameshub.game_clicker_service.items.dto.purchase;

import com.dice.minigameshub.game_clicker_service.achievement.domain.AchievementState;
import com.dice.minigameshub.game_clicker_service.core.dto.UserStatistics;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PurchaseItemResult {

    String itemId;
    int currencyPayedForItemPurchase;
    int currencyIncomePerClickBefore;
    int currencyIncomePerClickAfter;
    int currencyIncomePerMinuteBefore;
    int currencyIncomePerMinuteAfter;
    List<AchievementState> achievementStates;
    UserStatistics userStatistics;
}
