package com.dice.minigameshub.game_clicker_service.core.dto.clicks;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClicksProcessResult {
    Long currencyBeforeClicks;
    Long currencyAfterClicks;
}
