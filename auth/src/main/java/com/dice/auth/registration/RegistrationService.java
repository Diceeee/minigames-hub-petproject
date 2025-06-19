package com.dice.auth.registration;

import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResult register(RegistrationDto registration) {
        try {
            User user = userService.getUserByEmail(registration.getEmail());
            if (user.isRegistered()) {
                return RegistrationResult.builder()
                        .isSuccessful(false)
                        .errorId("already_registered")
                        .build();
            }

            User registeredUser = userService.save(user.toBuilder()
                    .username(registration.getUsername())
                    .nickname(registration.getNickname())
                    .password(passwordEncoder.encode(registration.getPassword()))
                    .build());
            registration.setPassword(null);

            return RegistrationResult.builder()
                    .isSuccessful(true)
                    .registeredUser(registeredUser)
                    .build();
        } catch (UserNotFoundException e) {
            User user = User.builder()
                    .username(registration.getUsername())
                    .nickname(registration.getNickname())
                    .email(registration.getEmail())
                    // todo: change to false when email verification feature is implemented
                    .emailVerified(true)
                    .password(passwordEncoder.encode(registration.getPassword()))
                    .build();

            User registeredUser = userService.save(user);
            return RegistrationResult.builder()
                    .isSuccessful(true)
                    .registeredUser(registeredUser)
                    .build();
        }
    }
}
