package com.dice.auth.user;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiException;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "user")
public class UserRestApi {

    private final UserService userService;

    @GetMapping("me")
    public ResponseEntity<User> getMe(@RequestHeader(AuthConstants.Headers.X_USER_ID) Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (UserNotFoundException e) {
            throw new ApiException(String.format("User not found by id %d", userId), ApiError.USER_NOT_FOUND);
        }
    }

    @GetMapping("byId/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (UserNotFoundException e) {
            throw new ApiException(String.format("User not found by id %d", userId), ApiError.USER_NOT_FOUND);
        }
    }

    @GetMapping("byEmail/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } catch (UserNotFoundException e) {
            throw new ApiException(String.format("User not found by email %s", email), ApiError.USER_NOT_FOUND);
        }
    }

    @GetMapping("byUsername/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.getUserByUsername(username));
        } catch (UserNotFoundException e) {
            throw new ApiException(String.format("User not found by username %s", username), ApiError.USER_NOT_FOUND);
        }
    }
}
