package com.dice.auth.core;

import com.dice.auth.user.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException() {
        return "/login?error=user_not_found";
    }
}
