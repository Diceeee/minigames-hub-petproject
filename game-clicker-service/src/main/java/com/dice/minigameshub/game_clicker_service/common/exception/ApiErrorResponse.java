package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.Value;

@Value
public class ApiErrorResponse {

    String message;
    int errorCode;
}
