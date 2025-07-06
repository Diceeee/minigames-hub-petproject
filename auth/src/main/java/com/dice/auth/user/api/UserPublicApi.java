package com.dice.auth.user.api;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiException;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/public/user")
public class UserPublicApi {

    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<User> getMe(@RequestHeader(AuthConstants.Headers.X_USER_ID) Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (UserNotFoundException e) {
            throw new ApiException(String.format("User not found by id %d", userId), ApiError.USER_NOT_FOUND);
        }
    }
}
