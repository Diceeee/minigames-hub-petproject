package com.dice.minigameshub.game_clicker_service.items.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PurchaseItemResponse {
    int currencyPayedForItemPurchase;
    int currencyIncomePerClickBefore;
    int currencyIncomePerClickAfter;
    int currencyIncomePerMinuteBefore;
    int currencyIncomePerMinuteAfter;
}
