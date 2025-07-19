package com.dice.minigameshub.game_clicker_service.items.dto.purchase;

import com.dice.minigameshub.game_clicker_service.achievement.dto.AchievementState;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PurchaseItemResult {

    int currencyPayedForItemPurchase;
    int currencyIncomePerClickBefore;
    int currencyIncomePerClickAfter;
    int currencyIncomePerMinuteBefore;
    int currencyIncomePerMinuteAfter;
    List<AchievementState> achievementStates;
}
