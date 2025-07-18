package com.dice.auth.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ServiceError serviceError;

    public ServiceException(String message, ServiceError serviceError) {
        super(message);
        this.serviceError = serviceError;
    }
}
