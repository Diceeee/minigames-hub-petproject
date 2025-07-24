package com.dice.minigameshub.game_clicker_service.save.document;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ClicksEventDocument {

    Integer clicks;
    Instant timestamp;
}
