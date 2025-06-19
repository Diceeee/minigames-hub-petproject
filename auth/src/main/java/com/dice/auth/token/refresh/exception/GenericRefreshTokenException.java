package com.dice.auth.token.refresh.exception;

import lombok.Getter;

@Getter
public class GenericRefreshTokenException extends RuntimeException {

    private final RefreshTokenExceptionErrorCode errorCode;

    public GenericRefreshTokenException(String message, RefreshTokenExceptionErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
