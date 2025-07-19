package com.dice.minigameshub.game_clicker_service.achievement.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProgressResponse {

    long current;
    long target;
    long delta;
    double ratio;
    boolean completed;
}
