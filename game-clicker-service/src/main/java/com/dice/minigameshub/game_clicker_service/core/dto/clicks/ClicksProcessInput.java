package com.dice.minigameshub.game_clicker_service.core.dto.clicks;

import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClicksProcessInput {
    Integer clicks;
    UserDetails userDetails;
}
