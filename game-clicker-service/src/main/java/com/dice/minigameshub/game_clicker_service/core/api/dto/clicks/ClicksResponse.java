package com.dice.minigameshub.game_clicker_service.core.api.dto.clicks;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClicksResponse {
    Long currencyBeforeClicks;
    Long currencyAfterClicks;
}
