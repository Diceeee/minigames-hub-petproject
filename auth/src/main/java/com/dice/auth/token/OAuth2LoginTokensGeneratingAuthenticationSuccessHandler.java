package com.dice.auth.token;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;

/**
 * Handles successful authentications in application using OAuth2 and assigns to users access and refresh tokens.
 */
@Component
@AllArgsConstructor
public class OAuth2LoginTokensGeneratingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String GOOGLE_OAUTH2_ID = "google";

    private final AuthConfigurationProperties authProperties;
    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;
    private final UserService userService;
    private final Clock clock;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (OAuth2AuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            handleAuthenticationToken(request, response, oAuth2AuthenticationToken);
        }
    }

    private void handleAuthenticationToken(HttpServletRequest request, HttpServletResponse response, AbstractAuthenticationToken authenticationToken) throws JOSEException, IOException {
        User authenticatedUser = getUserByToken(authenticationToken);
        Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateTokensForUser(authenticatedUser,
                request.getHeader(AuthConstants.Headers.USER_AGENT),
                AuthUtils.getClientIpAddress(request));

        Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
        Cookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.sendRedirect(authProperties.getFrontendUrl());
    }

    private User getUserByToken(AbstractAuthenticationToken authenticationToken) {
        if (authenticationToken instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authenticationToken.getPrincipal();
            String userEmail = oidcUser.getEmail();
            try {
                return userService.getUserByEmail(userEmail);
            } catch (UserNotFoundException e) {
                return preRegisterUser(oAuth2AuthenticationToken);
            }
        }

        throw new IllegalStateException("Can't handle unsupported authentication token: " + authenticationToken);
    }

    /**
     * Pre-registers user that logged from external provider with his OIDC data.
     */
    private User preRegisterUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        DefaultOidcUser oidcUser = (DefaultOidcUser) oAuth2AuthenticationToken.getPrincipal();
        User.UserBuilder userBuilder = User.builder()
                .email(oidcUser.getEmail())
                .createdAt(clock.instant())
                .emailVerified(true);

        if (!userService.userExistWithUsername(oidcUser.getGivenName())) {
            userBuilder.username(oidcUser.getGivenName());
        }
        if (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals(GOOGLE_OAUTH2_ID)) {
            userBuilder.googleId(oidcUser.getSubject());
        } else {
            throw new UnsupportedOperationException("OAuth2 provider not supported!");
        }

        return userService.save(userBuilder.build());
    }
}
