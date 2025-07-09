package com.dice.auth.registration;

import com.dice.auth.core.access.Roles;
import com.dice.auth.email.verification.EmailVerificationService;
import com.dice.auth.token.TokensGenerator;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final Clock clock;
    private final UserService userService;
    private final TokensGenerator tokensGenerator;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public RegistrationResult register(RegistrationDto registration, String refreshToken, String userAgent) throws JOSEException {
        try {
            User user = userService.getUserByEmail(registration.getEmail());
            if (user.isRegistered()) {
                return RegistrationResult.builder()
                        .isSuccessful(false)
                        .error(RegistrationResult.Error.ALREADY_REGISTERED)
                        .build();
            }

            if (user.getUsername() != null && !user.getUsername().equals(registration.getUsername())
                    && userService.userExistWithUsername(registration.getUsername())) {

                return RegistrationResult.builder()
                        .isSuccessful(false)
                        .error(RegistrationResult.Error.USERNAME_DUPLICATE)
                        .build();
            }

            if (user.inEmailVerificationProcess()) {
                emailVerificationService.createOrRecreateEmailVerificationTokenForUser(user.getId());
                return RegistrationResult.builder()
                        .isSuccessful(true)
                        .registeredUser(user)
                        .build();
            }

            User registeredUser = userService.save(user.toBuilder()
                    .username(registration.getUsername())
                    .password(passwordEncoder.encode(registration.getPassword()))
                    .authority(Roles.USER.getRoleWithPrefix())
                    .build());
            registration.setPassword(null);

            return RegistrationResult.builder()
                    .isSuccessful(true)
                    .updatedAccessToken(tokensGenerator.generateAccessTokenForUser(registeredUser, refreshToken, userAgent))
                    .registeredUser(registeredUser)
                    .build();
        } catch (UserNotFoundException e) {
            if (userService.userExistWithUsername(registration.getUsername())) {
                return RegistrationResult.builder()
                        .isSuccessful(false)
                        .error(RegistrationResult.Error.USERNAME_DUPLICATE)
                        .build();
            }

            User user = User.builder()
                    .username(registration.getUsername())
                    .email(registration.getEmail())
                    .createdAt(clock.instant())
                    .emailVerified(false)
                    .password(passwordEncoder.encode(registration.getPassword()))
                    .build();

            User registeredUser = userService.save(user);
            emailVerificationService.createOrRecreateEmailVerificationTokenForUser(registeredUser.getId());

            return RegistrationResult.builder()
                    .isSuccessful(true)
                    .registeredUser(registeredUser)
                    .build();
        }
    }
}
