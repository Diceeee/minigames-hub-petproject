package com.dice.auth.token.refresh.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RefreshTokenExceptionErrorCode {

    GENERIC(1),
    USER_IDS_MISMATCH(100),
    BROWSER_MISMATCH(101),
    OS_MISMATCH(102),
    TOKEN_NOT_FOUND(103),
    SESSION_REVOKED(104);

    private final int code;
}
