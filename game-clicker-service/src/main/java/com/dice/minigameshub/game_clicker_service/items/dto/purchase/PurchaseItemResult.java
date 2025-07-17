package com.dice.minigameshub.game_clicker_service.items.dto.purchase;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PurchaseItemResult {

    int currencyPayedForItemPurchase;
    int currencyIncomePerClickBefore;
    int currencyIncomePerClickAfter;
    int currencyIncomePerMinuteBefore;
    int currencyIncomePerMinuteAfter;
}
