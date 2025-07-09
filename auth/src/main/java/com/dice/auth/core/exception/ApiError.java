package com.dice.auth.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiError {

    UNKNOWN(-1),

    // --- User ---
    USER_NOT_FOUND(100),

    // --- Session ---
    SESSION_NOT_FOUND(200),
    SESSION_ALREADY_REVOKED(201),
    SESSION_DOES_NOT_BELONG_TO_USER(202),

    // --- Refresh ---
    REFRESH_IS_TOO_FREQUENT(300),
    REFRESH_TOKEN_MISSING(301),

    // --- Registration ---
    REGISTRATION_FAILED_DUPLICATE_USERNAME(400),
    REGISTRATION_FAILED_ALREADY_REGISTERED(401),

    // --- Email Verification ---
    EMAIL_VERIFICATION_TOKEN_NOT_FOUND(500),
    EMAIL_VERIFICATION_ALREADY_VERIFIED(501),

    // --- Authentication ---
    INVALID_CREDENTIALS(600),
    AUTHENTICATION_FAILED(601),
    TOKEN_GENERATION_FAILED(602);

    private final int errorCode;
}
