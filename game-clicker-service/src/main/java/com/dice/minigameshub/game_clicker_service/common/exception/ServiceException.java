package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final Error error;

    public ServiceException(String message, Error error) {
        super(message);
        this.error = error;
    }
}
