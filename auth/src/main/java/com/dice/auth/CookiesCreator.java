package com.dice.auth;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
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
        cookie.setMaxAge((int) Duration.ofDays(1).getSeconds());
        return cookie;
    }

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(AuthConstants.Cookies.REFRESH_TOKEN, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true for HTTPS
        cookie.setPath("/refresh");
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
        cookie.setPath("/refresh");
        cookie.setMaxAge(0);
        return cookie;
    }

    // Legacy methods for backward compatibility (if needed)
    public ResponseCookie createAccessTokenResponseCookie(String accessToken) {
        return ResponseCookie.from(AuthConstants.Cookies.ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();
    }

    public ResponseCookie createRefreshTokenResponseCookie(String refreshToken) {
        return ResponseCookie.from(AuthConstants.Cookies.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/refresh")
                .maxAge(Duration.ofDays(200))
                .build();
    }

    public ResponseCookie getDeletedAccessTokenResponseCookie() {
        return ResponseCookie.from(AuthConstants.Cookies.ACCESS_TOKEN, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie getDeletedRefreshTokenResponseCookie() {
        return ResponseCookie.from(AuthConstants.Cookies.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/refresh")
                .maxAge(0)
                .build();
    }
}
