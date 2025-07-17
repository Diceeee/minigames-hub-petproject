package com.dice.minigameshub.game_clicker_service.core.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserStatistics {
    Long totalClicks;
    Long totalCurrencyEarned;
    Long totalCurrencyWasted;
}
