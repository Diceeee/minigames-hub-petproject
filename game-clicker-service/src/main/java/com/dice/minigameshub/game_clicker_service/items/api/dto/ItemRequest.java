package com.dice.minigameshub.game_clicker_service.items.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemRequest {
    String name;
    String description;

    Integer price;
    Integer currencyIncomeIncreaseInMinute;
    Integer currencyIncomeIncreasePerClick;
}
