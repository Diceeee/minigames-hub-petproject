package com.dice.minigameshub.game_clicker_service.core.api.dto.clicks;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClicksRequest {
    @Positive
    Integer clicks;
}
