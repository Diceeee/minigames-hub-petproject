package com.dice.auth.login;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiException;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.email.EmailPasswordAuthenticationToken;
import com.dice.auth.token.TokensGenerator;
import com.dice.auth.token.TokensLogoutHandler;
import com.dice.auth.user.dto.User;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/public")
public class LoginPublicApi {

    private final AuthenticationManager authenticationManager;
    private final TokensLogoutHandler tokensLogoutHandler;
    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            Authentication authenticationToken;
            if (EmailValidator.getInstance().isValid(loginRequest.getPrincipal())) {
                authenticationToken = EmailPasswordAuthenticationToken.unauthenticated(
                    loginRequest.getPrincipal(), 
                    loginRequest.getPassword()
                );
            } else {
                authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequest.getPrincipal(), 
                    loginRequest.getPassword()
                );
            }

            Authentication authenticated = authenticationManager.authenticate(authenticationToken);
            if (!(authenticated.getPrincipal() instanceof User user)) {
                throw new ApiException("Authentication principal is not a User object", ApiError.AUTHENTICATION_FAILED);
            }

            Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateTokensForUser(
                user,
                request.getHeader(AuthConstants.Headers.USER_AGENT),
                AuthUtils.getClientIpAddress(request)
            );

            Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
            Cookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new LoginResponse(
                accessAndRefreshTokens.getLeft(),
                accessAndRefreshTokens.getRight()
            ));

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for principal: {}", loginRequest.getPrincipal());
            throw new ApiException("Invalid credentials", ApiError.INVALID_CREDENTIALS);
        } catch (AuthenticationException e) {
            log.error("Authentication error during login", e);
            throw new ApiException("Authentication failed", ApiError.AUTHENTICATION_FAILED);
        } catch (JOSEException e) {
            log.error("Error generating tokens during login", e);
            throw new ApiException("Token generation failed", ApiError.TOKEN_GENERATION_FAILED);
        }
    }

    /**
     * Performs logout. Refresh path part is important to get refresh token cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, HttpServletRequest request) {
        tokensLogoutHandler.logout(request, response);
        return ResponseEntity.noContent().build();
    }
} 