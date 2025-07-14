package com.dice.minigameshub.game_clicker_service.items.purchase;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PurchaseItemResult {

    Integer newActiveIncomePerClick;
    Integer newPassiveIncomePerMinute;
}
