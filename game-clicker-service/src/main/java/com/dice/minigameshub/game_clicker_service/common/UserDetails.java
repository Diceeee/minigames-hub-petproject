package com.dice.minigameshub.game_clicker_service.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDetails {

    Long userId;
    String sessionId;

    public static UserDetails of(Long userId, String sessionId) {
        return new UserDetails(userId, sessionId);
    }
}
