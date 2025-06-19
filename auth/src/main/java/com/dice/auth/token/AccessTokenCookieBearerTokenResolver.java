package com.dice.auth.token;

import com.dice.auth.AuthConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AccessTokenCookieBearerTokenResolver implements BearerTokenResolver {

    private final TokensParser tokensParser;

    @Override
    public String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(AuthConstants.Cookies.ACCESS_TOKEN)) {
                    String token = cookie.getValue();
                    if (isAccessTokenValid(token)) {
                        return token;
                    } else {
                        log.warn("Token signature is invalid: {}", token);
                    }
                }
            }
        }

        return null;
    }

    private boolean isAccessTokenValid(String token) {
        try {
            tokensParser.parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
