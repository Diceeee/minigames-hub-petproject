package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Error {

    UNKNOWN(-1),

    // --- GENERAL ---
    NOT_ENOUGH_CURRENCY(1),
    ACTIVE_SESSION_MISMATCH(2),

    // --- ITEMS ---
    ITEM_NOT_FOUND(100),
    ITEM_ALREADY_PURCHASED(101),

    // --- USERS ---
    USER_SAVE_NOT_FOUND(200),

    // --- MISS-CONFIGURATION ---
    GAME_NOT_CONFIGURED(300),

    // --- ACHIEVEMENTS ---
    ACHIEVEMENT_NOT_FOUND(400),

    // --- CHEATING PREVENTION ---
    CLICKS_LIMIT_PER_MINUTE_EXCEED(500);

    private final int errorCode;
}
