package com.dice.auth;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@AllArgsConstructor
public class CookiesCreator {

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from(AuthConstants.Cookies.ACCESS_TOKEN, accessToken)
                .httpOnly(true)           // Защищает от JavaScript (XSS)
                .secure(false)             // Только по HTTPS
                .sameSite("Strict")       // Защита от CSRF
                .path("/")                // Ограничение области действия куки
                .maxAge(Duration.ofDays(1)) // Время жизни
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(AuthConstants.Cookies.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)           // Защищает от JavaScript (XSS)
                .secure(false)             // Только по HTTPS
                .sameSite("Strict")       // Защита от CSRF
                .path("/refresh")                // Ограничение области действия куки
                .maxAge(Duration.ofDays(200)) // Время жизни
                .build();
    }

    public ResponseCookie getDeletedAccessTokenCookie() {
        return ResponseCookie.from(AuthConstants.Cookies.ACCESS_TOKEN, "")
                .httpOnly(true)           // Защищает от JavaScript (XSS)
                .secure(false)             // Только по HTTPS
                .sameSite("Strict")       // Защита от CSRF
                .path("/")                // Ограничение области действия куки
                .maxAge(0) // Время жизни
                .build();
    }

    public ResponseCookie getDeletedRefreshTokenCookie() {
        return ResponseCookie.from(AuthConstants.Cookies.REFRESH_TOKEN, "")
                .httpOnly(true)           // Защищает от JavaScript (XSS)
                .secure(false)             // Только по HTTPS
                .sameSite("Strict")       // Защита от CSRF
                .path("/")                // Ограничение области действия куки
                .maxAge(0) // Время жизни
                .build();
    }
}
