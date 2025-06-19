package com.dice.auth.token;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.email.EmailPasswordAuthenticationToken;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.dice.auth.user.exception.UserNotFoundException;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles successful authentications in application and assigns to users access and refresh tokens.
 */
@Component
@AllArgsConstructor
public class TokensGeneratingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String GOOGLE_OAUTH2_ID = "google";

    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;
    private final UserService userService;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
            handleAuthenticationToken(request, response, usernamePasswordAuthenticationToken);
        }
        if (EmailPasswordAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            EmailPasswordAuthenticationToken emailPasswordAuthenticationToken = (EmailPasswordAuthenticationToken) authentication;
            handleAuthenticationToken(request, response, emailPasswordAuthenticationToken);
        }
        if (OAuth2AuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            handleAuthenticationToken(request, response, oAuth2AuthenticationToken);
        }
    }

    private void handleAuthenticationToken(HttpServletRequest request, HttpServletResponse response, AbstractAuthenticationToken authenticationToken) throws JOSEException, IOException {
        User authenticatedUser = getUserByToken(authenticationToken);
        Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateForLogin(authenticatedUser,
                request.getHeader(AuthConstants.Headers.USER_AGENT),
                AuthUtils.getClientIpAddress(request));

        ResponseCookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
        ResponseCookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        if (!authenticatedUser.isRegistered()) {
            List<String> params = new ArrayList<>();
            if (authenticatedUser.getUsername() != null) {
                params.add(String.format("%s=%s&", "username", URLEncoder.encode(authenticatedUser.getUsername(), StandardCharsets.UTF_8)));
            }
            if (authenticatedUser.getNickname() != null) {
                params.add(String.format("%s=%s&", "nickname", URLEncoder.encode(authenticatedUser.getNickname(), StandardCharsets.UTF_8)));
            }
            if (authenticatedUser.getEmail() != null) {
                params.add(String.format("%s=%s", "email", URLEncoder.encode(authenticatedUser.getEmail(), StandardCharsets.UTF_8)));
            }

            String encodedParams = String.join("&", params);
            String continueRegistrationRedirectionUri = String.format("%s/continue?%s",
                    AuthConstants.Uris.REGISTER,
                    encodedParams);

            response.sendRedirect(continueRegistrationRedirectionUri);
        } else {
            response.sendRedirect("/");
        }
    }

    private User getUserByToken(AbstractAuthenticationToken authenticationToken) {
        if (authenticationToken.getPrincipal() instanceof User) {
            return (User) authenticationToken.getPrincipal();
        }
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
        if (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals(GOOGLE_OAUTH2_ID)) {
            User user = User.builder()
                    .googleId(oidcUser.getSubject())
                    .nickname(oidcUser.getGivenName())
                    .email(oidcUser.getEmail())
                    .emailVerified(oidcUser.getEmailVerified())
                    .build();

            return userService.save(user);
        } else {
            throw new UnsupportedOperationException("OAuth2 provider not supported!");
        }
    }
}
