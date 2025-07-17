package com.dice.minigameshub.game_clicker_service.items.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateOrCreateItemInput {
    String itemId;

    String name;
    String description;

    Integer price;
    Integer currencyIncomeIncreaseInMinute;
    Integer currencyIncomeIncreasePerClick;
}
