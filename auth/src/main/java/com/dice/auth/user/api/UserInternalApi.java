package com.dice.auth.user.api;

import com.dice.auth.common.exception.ServiceError;
import com.dice.auth.common.exception.ServiceException;
import com.dice.auth.user.UserMapper;
import com.dice.auth.user.UserService;
import com.dice.auth.user.api.dto.UserResponse;
import com.dice.auth.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/internal/user")
public class UserInternalApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("byId/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userMapper.mapToResponse(userService.getUserById(userId)));
        } catch (UserNotFoundException e) {
            throw new ServiceException(String.format("User not found by id %d", userId), ServiceError.USER_NOT_FOUND);
        }
    }

    @GetMapping("byEmail/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userMapper.mapToResponse(userService.getUserByEmail(email)));
        } catch (UserNotFoundException e) {
            throw new ServiceException(String.format("User not found by email %s", email), ServiceError.USER_NOT_FOUND);
        }
    }

    @GetMapping("byUsername/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userMapper.mapToResponse(userService.getUserByUsername(username)));
        } catch (UserNotFoundException e) {
            throw new ServiceException(String.format("User not found by username %s", username), ServiceError.USER_NOT_FOUND);
        }
    }
}
