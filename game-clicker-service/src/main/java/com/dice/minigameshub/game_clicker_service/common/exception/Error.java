package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Error {

    UNKNOWN(-1),

    // --- GENERAL ---
    NOT_ENOUGH_CURRENCY(1),

    /**
     * User tries to perform action from session that differs from last session that was used to start game.
     * Only one active session is allowed per game.
     * If start game performed from other session, previous session will become inactive.
     */
    ACTIVE_SESSION_MISMATCH(2),

    // --- ITEMS ---
    ITEM_NOT_FOUND(100),
    ITEM_ALREADY_PURCHASED(101),

    // --- USERS ---
    USER_SAVE_NOT_FOUND(200),

    // --- MISS-CONFIGURATION ---
    GAME_NOT_CONFIGURED(300),

    // --- ACHIEVEMENTS ---
    ACHIEVEMENT_NOT_FOUND(400);

    private final int errorCode;
}
