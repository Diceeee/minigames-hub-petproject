package com.dice.auth.registration;

import com.dice.auth.CookiesCreator;
import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "register")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CookiesCreator cookiesCreator;

    @PostMapping("cancel")
    public void cancel(Authentication authentication, HttpServletResponse response) {
        if (authentication != null) {
            Cookie accessTokenCookie = cookiesCreator.getDeletedAccessTokenCookie();
            Cookie refreshTokenCookie = cookiesCreator.getDeletedRefreshTokenCookie();

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<SuccessfulRegistrationResultResponse> processRegistration(@RequestBody @Valid RegistrationDto registration, HttpServletResponse response) {
        RegistrationResult registrationResult = registrationService.register(registration);
        if (registrationResult.isSuccessful()) {
            if (registrationResult.getUpdatedAccessToken() != null) {
                Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(registrationResult.getUpdatedAccessToken());
                response.addCookie(accessTokenCookie);
            }

            return ResponseEntity.ok(new SuccessfulRegistrationResultResponse(registrationResult.getRegisteredUser().isEmailVerified()));
        } else {
            switch (registrationResult.getError()) {
                case USERNAME_DUPLICATE ->
                        throw new ApiException("User with such username already exists", ApiError.REGISTRATION_FAILED_DUPLICATE_USERNAME);
                case ALREADY_REGISTERED ->
                        throw new ApiException("User already registered", ApiError.REGISTRATION_FAILED_ALREADY_REGISTERED);
                default ->
                        throw new ApiException("Unknown registration error: " + registrationResult.getError(), ApiError.UNKNOWN);
            }
        }
    }
}
