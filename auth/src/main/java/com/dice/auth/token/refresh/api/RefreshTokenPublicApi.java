package com.dice.auth.token.refresh.api;

import com.dice.auth.AuthConstants;
import com.dice.auth.CookiesCreator;
import com.dice.auth.core.exception.ApiError;
import com.dice.auth.core.exception.ApiException;
import com.dice.auth.core.util.AuthUtils;
import com.dice.auth.token.TokensGenerator;
import com.dice.auth.token.refresh.RefreshTokenSessionService;
import com.dice.auth.token.refresh.RefreshValidator;
import com.dice.auth.token.refresh.api.dto.RefreshTokensResponse;
import com.dice.auth.token.refresh.dto.RefreshTokenSession;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/public")
public class RefreshTokenPublicApi {

    private final RefreshTokenSessionService refreshTokenSessionService;
    private final RefreshValidator refreshValidator;
    private final TokensGenerator tokensGenerator;
    private final CookiesCreator cookiesCreator;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokensResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response,
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            @CookieValue(value = "access_token", required = false) String accessToken,
            @RequestHeader(AuthConstants.Headers.USER_AGENT) String userAgent) throws IOException {

        if (refreshToken == null) {
            throw new ApiException("Can't refresh without refresh token", ApiError.REFRESH_TOKEN_MISSING);
        }

        try {
            if (!refreshValidator.validateRefreshAllowed(accessToken, refreshToken)) {
                throw new ApiException("Refresh currently not allowed", ApiError.REFRESH_IS_TOO_FREQUENT);
            }

            Pair<String, String> accessAndRefreshTokens = tokensGenerator.generateTokensForRefreshToken(refreshToken, userAgent, AuthUtils.getClientIpAddress(request));

            Cookie accessTokenCookie = cookiesCreator.createAccessTokenCookie(accessAndRefreshTokens.getLeft());
            Cookie refreshTokenCookie = cookiesCreator.createRefreshTokenCookie(accessAndRefreshTokens.getRight());

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok(new RefreshTokensResponse(accessAndRefreshTokens.getLeft(), accessAndRefreshTokens.getRight()));
        } catch (JOSEException e) {
            throw new ApiException("JOSE exception during refresh, message: " + e.getMessage(), ApiError.UNKNOWN);
        }
    }

    @GetMapping(path = "/sessions/active")
    public ResponseEntity<List<RefreshTokenSession>> getActiveSessions(@RequestHeader(AuthConstants.Headers.X_USER_ID) Long userId) {
        return ResponseEntity.ok(refreshTokenSessionService.getUserActiveSessions(userId));
    }

    @PostMapping(path = "/sessions/revoke/{sessionId}")
    public void revokeSession(@RequestHeader(AuthConstants.Headers.X_USER_ID) Long userId,
                              @PathVariable(name = "sessionId") String revokedSessionId) {

        refreshTokenSessionService.revokeSession(userId, revokedSessionId);
    }
}
