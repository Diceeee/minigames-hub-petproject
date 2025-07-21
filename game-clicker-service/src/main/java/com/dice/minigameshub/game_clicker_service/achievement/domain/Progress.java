package com.dice.minigameshub.game_clicker_service.achievement.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Progress {

    long current;
    long target;
    long delta;

    public Progress(long current, long target, long delta) {
        this.current = Math.min(current, target);
        this.target = target;
        this.delta = delta;
    }

    public double getRatio() {
        return (double) current / target;
    }

    public boolean isCompleted() {
        return current >= target;
    }
}
