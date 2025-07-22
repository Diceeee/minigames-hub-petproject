package com.dice.auth.user.api;

import com.dice.auth.AuthConstants;
import com.dice.auth.common.exception.ServiceError;
import com.dice.auth.common.exception.ServiceException;
import com.dice.auth.user.UserMapper;
import com.dice.auth.user.UserService;
import com.dice.auth.user.api.dto.UserMeResponse;
import com.dice.auth.user.domain.User;
import com.dice.auth.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/public/user")
public class UserPublicApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe(@RequestHeader(AuthConstants.Headers.X_USER_ID) Long userId,
                                                @RequestHeader(AuthConstants.Headers.X_SESSION_ID) String sessionId) {
        try {
            User user = userService.getUserById(userId);
            UserMeResponse userMeResponse = UserMeResponse.builder()
                    .id(user.getId())
                    .sessionId(sessionId)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .registered(user.isRegistered())
                    .emailVerified(user.isEmailVerified())
                    .authorities(new HashSet<>(userMapper.mapAuthoritiesToListOfStrings(user.getAuthorities())))
                    .build();

            return ResponseEntity.ok(userMeResponse);
        } catch (UserNotFoundException e) {
            throw new ServiceException(String.format("User not found by id %d", userId), ServiceError.USER_NOT_FOUND);
        }
    }
}
