package com.dice.minigameshub.game_clicker_service.core.dto.start;

import com.dice.minigameshub.game_clicker_service.common.UserDetails;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StartGameInput {
    UserDetails userDetails;
}
