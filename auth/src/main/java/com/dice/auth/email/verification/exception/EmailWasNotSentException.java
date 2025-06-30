package com.dice.auth.email.verification.exception;

public class EmailWasNotSentException extends RuntimeException {

    public EmailWasNotSentException(String message) {
        super(message);
    }
}
