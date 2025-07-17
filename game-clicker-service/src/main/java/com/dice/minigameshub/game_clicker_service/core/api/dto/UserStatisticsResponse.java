package com.dice.minigameshub.game_clicker_service.core.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserStatisticsResponse {
    Long totalClicks;
    Long totalCurrencyEarned;
    Long totalCurrencyWasted;

}
