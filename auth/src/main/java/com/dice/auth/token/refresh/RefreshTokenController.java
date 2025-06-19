package com.dice.auth.token.refresh;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.token.TokensGenerator;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@Controller
@AllArgsConstructor
public class RefreshTokenController {

    private final RefreshValidator refreshValidator;
    private final CookiesCreator cookiesCreator;
    private final TokensGenerator tokensGenerator;

    @GetMapping("/refresh")
    public String refresh(
            HttpServletRequest request,
            HttpServletResponse response,
            @CookieValue("refresh_token") String refreshToken,
            @CookieValue(value = "access_token", required = false) String accessToken,
            @RequestHeader(AuthConstants.Headers.USER_AGENT) String userAgent,
            @RequestParam("redirectTo") String redirectTo) throws IOException {

        try {
            if (!refreshValidator.validateRefreshAllowed(accessToken, refreshToken)) {
                return "redirect:" + AuthConstants.Uris.LOGIN;
            }

            Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateForRefresh(refreshToken, userAgent, AuthUtils.getClientIpAddress(request));
            ResponseCookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
            ResponseCookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return "redirect:" + redirectTo;
        } catch (JOSEException e) {
            ResponseCookie accessTokenCookie = cookiesCreator.getDeletedAccessTokenCookie();
            ResponseCookie refreshTokenCookie = cookiesCreator.getDeletedRefreshTokenCookie();

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return "redirect:" + AuthConstants.Uris.LOGIN;
        }
    }
}
