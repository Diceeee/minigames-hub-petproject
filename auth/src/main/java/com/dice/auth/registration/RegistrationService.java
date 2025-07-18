package com.dice.auth.registration;

import com.dice.auth.core.access.Roles;
import com.dice.auth.email.verification.EmailVerificationService;
import com.dice.auth.token.TokensGenerator;
import com.dice.auth.token.TokensParser;
import com.dice.auth.token.refresh.RefreshTokenSessionService;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import com.nimbusds.jose.JOSEException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationService {

    private final Clock clock;
    private final UserService userService;
    private final TokensParser tokensParser;
    private final TokensGenerator tokensGenerator;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenSessionService refreshTokenSessionService;

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

            // If email verified, but user still not registered, it means it is OAuth2 flow.
            // But if refresh/access tokens are missing, it means user is not authenticated and must re-authenticate via OAuth2 again to finish registration.
            // It is security in case if somebody will try manually register as user that is currently in OAuth2 registration flow but not authenticated. Unlikely, but possible.
            if (refreshToken == null || refreshToken.isEmpty()) {
                return RegistrationResult.builder()
                        .isSuccessful(false)
                        .error(RegistrationResult.Error.OAUTH2_REGISTRATION_BROKEN)
                        .build();
            }

            User registeredUser = userService.save(user.toBuilder()
                    .username(registration.getUsername())
                    .password(passwordEncoder.encode(registration.getPassword()))
                    .authority(Roles.USER.getRoleWithPrefix())
                    .registered(true)
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

    @Transactional
    public void cancelRegistration(String refreshToken) throws JOSEException {
        Jws<Claims> parsedRefreshToken = tokensParser.parseToken(refreshToken);
        Long userId = Long.valueOf(parsedRefreshToken.getPayload().getSubject());

        User user = userService.getUserById(userId);
        if (!user.isRegistered()) {
            refreshTokenSessionService.deleteAllUserSessions(userId);
            userService.removeUserById(userId);
            log.info("Canceled registration for user {} with email {}", user.getId(), user.getEmail());
        }
    }
}
