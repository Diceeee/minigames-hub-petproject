package com.dice.minigameshub.game_clicker_service.items.purchase;

import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PurchaseItemInput {

    String itemId;
    UserDetails userDetails;
}
