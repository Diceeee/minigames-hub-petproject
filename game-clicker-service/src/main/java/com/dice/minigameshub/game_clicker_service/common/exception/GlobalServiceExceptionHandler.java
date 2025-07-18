package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalServiceExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleApiException(ServiceException serviceException) {
        log.info("Api exception occurred", serviceException);
        return ResponseEntity.badRequest().body(new ApiErrorResponse(serviceException.getMessage(), serviceException.getError().getErrorCode()));
    }
}
