package com.dice.auth.token;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.token.refresh.RefreshTokenSessionService;
import com.dice.auth.token.refresh.exception.SessionAssociatedWithRefreshTokenNotFound;
import com.nimbusds.jose.JOSEException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TokensLogoutHandler implements LogoutHandler {

    private final RefreshTokenSessionService sessionService;
    private final CookiesCreator cookiesCreator;
    private final TokensParser tokensParser;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        AuthUtils.getCookieValue(request, AuthConstants.Cookies.REFRESH_TOKEN)
                .ifPresent(this::endRefreshTokenSession);

        ResponseCookie accessTokenCookie = cookiesCreator.getDeletedAccessTokenCookie();
        ResponseCookie refreshTokenCookie = cookiesCreator.getDeletedRefreshTokenCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private void endRefreshTokenSession(String refreshToken) {
        try {
            Jws<Claims> refreshTokenJws = tokensParser.parseToken(refreshToken);
            sessionService.deleteTokenByTokenId(refreshTokenJws.getPayload().getId());
        } catch (JOSEException | SessionAssociatedWithRefreshTokenNotFound e) {
            log.info("Couldn't end session for refresh token: {}", refreshToken);
        }
    }
}
