package com.dice.auth.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private static final String ID_MSG_TEMPLATE = "User %d was not found";
    private static final String EMAIL_MSG_TEMPLATE = "User was not found by email '%s'";
    private static final String USERNAME_MSG_TEMPLATE = "User was not found by username '%s'";

    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException forId(Long userId) {
        return new UserNotFoundException(String.format(ID_MSG_TEMPLATE, userId));
    }

    public static UserNotFoundException forEmail(String email) {
        return new UserNotFoundException(String.format(EMAIL_MSG_TEMPLATE, email));
    }

    public static UserNotFoundException forUsername(String username) {
        return new UserNotFoundException(String.format(USERNAME_MSG_TEMPLATE, username));
    }
}
