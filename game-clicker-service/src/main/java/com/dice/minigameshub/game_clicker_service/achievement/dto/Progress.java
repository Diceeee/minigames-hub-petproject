package com.dice.minigameshub.game_clicker_service.achievement.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Progress {

    long current;
    long target;
    long delta;

    public double getRatio() {
        return (double) current / target;
    }

    public boolean isCompleted() {
        return current >= target;
    }
}
