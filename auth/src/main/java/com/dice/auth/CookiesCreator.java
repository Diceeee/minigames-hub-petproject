package com.dice.auth;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@AllArgsConstructor
public class CookiesCreator {

    public Cookie createAccessTokenCookie(String accessToken) {
        Cookie cookie = new Cookie(AuthConstants.Cookies.ACCESS_TOKEN, accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) Duration.ofDays(200).getSeconds());
        return cookie;
    }

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(AuthConstants.Cookies.REFRESH_TOKEN, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true for HTTPS
        cookie.setPath("/public/auth");
        cookie.setMaxAge((int) Duration.ofDays(200).getSeconds());
        return cookie;
    }

    public Cookie getDeletedAccessTokenCookie() {
        Cookie cookie = new Cookie(AuthConstants.Cookies.ACCESS_TOKEN, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    public Cookie getDeletedRefreshTokenCookie() {
        Cookie cookie = new Cookie(AuthConstants.Cookies.REFRESH_TOKEN, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true for HTTPS
        cookie.setPath("/public/auth");
        cookie.setMaxAge(0);
        return cookie;
    }
}
