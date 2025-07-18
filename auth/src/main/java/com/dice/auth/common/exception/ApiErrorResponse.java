package com.dice.auth.common.exception;

import lombok.Value;

@Value
public class ApiErrorResponse {

    String message;
    int errorCode;
}
