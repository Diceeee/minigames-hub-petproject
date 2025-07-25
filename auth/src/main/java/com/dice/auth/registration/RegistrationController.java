package com.dice.auth.registration;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.common.exception.ServiceError;
import com.dice.auth.common.exception.ServiceException;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "api/public/register")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final CookiesCreator cookiesCreator;

    @PostMapping("cancel")
    public void cancel(@CookieValue(value = AuthConstants.Cookies.REFRESH_TOKEN, required = false) String refreshToken, HttpServletResponse response) throws JOSEException {
        if (refreshToken != null) {
            registrationService.cancelRegistration(refreshToken);

            Cookie accessTokenCookie = cookiesCreator.getDeletedAccessTokenCookie();
            Cookie refreshTokenCookie = cookiesCreator.getDeletedRefreshTokenCookie();

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulRegistrationResultResponse> processRegistration(@RequestBody @Valid RegistrationDto registration,
                                                                                    @CookieValue(value = AuthConstants.Cookies.REFRESH_TOKEN, required = false) String refreshToken,
                                                                                    HttpServletResponse response,
                                                                                    HttpServletRequest request) throws JOSEException {

        RegistrationResult registrationResult = registrationService.register(registration, refreshToken,
                request.getHeader(AuthConstants.Headers.USER_AGENT));

        if (registrationResult.isSuccessful()) {
            if (registrationResult.getUpdatedAccessToken() != null) {
                Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(registrationResult.getUpdatedAccessToken());
                response.addCookie(accessTokenCookie);
            }

            return ResponseEntity.ok(new SuccessfulRegistrationResultResponse(registrationResult.getRegisteredUser().isEmailVerified()));
        } else {
            switch (registrationResult.getError()) {
                case USERNAME_DUPLICATE ->
                        throw new ServiceException("User with such username already exists", ServiceError.REGISTRATION_FAILED_DUPLICATE_USERNAME);
                case ALREADY_REGISTERED ->
                        throw new ServiceException("User already registered", ServiceError.REGISTRATION_FAILED_ALREADY_REGISTERED);
                case OAUTH2_REGISTRATION_BROKEN ->
                        throw new ServiceException("User has to re-login via OAuth2 to continue registration", ServiceError.REGISTRATION_FAILED_ALREADY_REGISTERED);
                default ->
                        throw new ServiceException("Unknown registration error: " + registrationResult.getError(), ServiceError.UNKNOWN);
            }
        }
    }
}
