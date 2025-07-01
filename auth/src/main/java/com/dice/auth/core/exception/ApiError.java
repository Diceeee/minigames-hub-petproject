package com.dice.auth.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiError {

    USER_NOT_FOUND(100);

    private final int errorCode;
}
