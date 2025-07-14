package com.dice.minigameshub.game_clicker_service.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException apiException) {
        log.info("Api exception occurred", apiException);
        return ResponseEntity.badRequest().body(new ApiErrorResponse(apiException.getMessage(), apiException.getApiError().getErrorCode()));
    }
}
