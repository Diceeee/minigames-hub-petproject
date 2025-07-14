package com.dice.minigameshub.game_clicker_service.save.document;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class UserStatisticsDocument {

    Long totalClicks;
    Long totalCurrencyEarned;
    Long totalCurrencyWasted;
}
