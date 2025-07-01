package com.dice.auth.core.exception;

import lombok.Value;

@Value
public class ApiErrorResponse {

    String message;
    int errorCode;
}
