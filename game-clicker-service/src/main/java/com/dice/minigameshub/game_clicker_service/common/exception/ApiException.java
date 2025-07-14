package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiError apiError;

    public ApiException(String message, ApiError apiError) {
        super(message);
        this.apiError = apiError;
    }
}
