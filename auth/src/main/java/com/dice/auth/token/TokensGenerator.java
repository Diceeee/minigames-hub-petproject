package com.dice.auth.token;

import com.dice.auth.AuthConstants;
import com.dice.auth.RsaKeyProvider;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.core.util.UserAgentParser;
import com.dice.auth.token.refresh.RefreshTokenSessionService;
import com.dice.auth.token.refresh.dto.RefreshTokenSession;
import com.dice.auth.token.refresh.exception.GenericRefreshTokenException;
import com.dice.auth.token.refresh.exception.RefreshTokenExceptionErrorCode;
import com.dice.auth.user.UserService;
import com.dice.auth.user.dto.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TokensGenerator {

    private final Clock jjwtClock;
    private final java.time.Clock clock;
    private final RsaKeyProvider rsaKeyProvider;
    private final AuthConfigurationProperties authProperties;
    private final TokensParser tokensParser;
    private final UserService userService;
    private final RefreshTokenSessionService refreshTokenSessionService;

    /**
     * Generates new access and refresh tokens pairs for refreshes,
     * uses additional logical validation of refresh tokens and sessions before generating new tokens.
     *
     * @param providedRefreshToken Provided refresh token
     * @param userAgent    User's agent from UA header
     * @param ipAddress    Request IP address
     * @return Pair of access token (left) and refresh token (right)
     * @throws JOSEException If refresh token parsing is failed
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Pair<String, String> generateForRefresh(String providedRefreshToken, String userAgent, String ipAddress) throws JOSEException {
        Jws<Claims> parsedRefreshToken = tokensParser.parseToken(providedRefreshToken);
        RefreshTokenSession refreshTokenSession = refreshTokenSessionService.getByTokenId(parsedRefreshToken.getPayload().getId());
        Long userId = Long.valueOf(parsedRefreshToken.getPayload().getSubject());
        UserAgentParser.UserAgentParseResult userAgentParseResult = UserAgentParser.parse(userAgent);
        validateRefreshToken(parsedRefreshToken, refreshTokenSession, userAgentParseResult);

        User user = userService.getUserById(userId);
        String refreshTokenId = UUID.randomUUID().toString();
        Date refreshTokenIssuedAt = jjwtClock.now();
        Date refreshTokenExpiration = Date.from(clock.instant().plus(authProperties.getRefreshTokenExpirationInDays(), ChronoUnit.DAYS));
        RSAKey key = rsaKeyProvider.getRsaKey();

        String accessToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(authProperties.getIssuerName())
                .issuedAt(jjwtClock.now())
                .expiration(Date.from(clock.instant().plus(authProperties.getAccessTokenExpirationInMinutes(), ChronoUnit.MINUTES)))
                .signWith(key.toPrivateKey())
                .subject(user.getId().toString())
                .claim(AuthConstants.Claims.AUTHORITIES, user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(";")))
                .compact();

        String refreshToken = Jwts.builder()
                .id(refreshTokenId)
                .issuer(authProperties.getIssuerName())
                .issuedAt(refreshTokenIssuedAt)
                .expiration(refreshTokenExpiration)
                .signWith(key.toPrivateKey())
                .subject(user.getId().toString())
                .compact();

        refreshTokenSessionService.save(refreshTokenSession.toBuilder()
                        .tokenId(refreshTokenId)
                        .createdAt(refreshTokenIssuedAt.toInstant())
                        .expiresAt(refreshTokenExpiration.toInstant())
                        .ipAddress(ipAddress)
                        .build());

        return Pair.of(accessToken, refreshToken);
    }

    public Pair<String, String> generateForLogin(User user, String userAgent, String ipAddress) throws JOSEException {
        String refreshTokenId = UUID.randomUUID().toString();
        Date refreshTokenIssuedAt = jjwtClock.now();
        Date refreshTokenExpiration = Date.from(clock.instant().plus(authProperties.getRefreshTokenExpirationInDays(), ChronoUnit.DAYS));
        RSAKey key = rsaKeyProvider.getRsaKey();

        String accessToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(authProperties.getIssuerName())
                .issuedAt(jjwtClock.now())
                .expiration(Date.from(clock.instant().plus(authProperties.getAccessTokenExpirationInMinutes(), ChronoUnit.MINUTES)))
                .signWith(key.toPrivateKey())
                .subject(user.getId().toString())
                .claim(AuthConstants.Claims.AUTHORITIES, user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(";")))
                .compact();

        String refreshToken = Jwts.builder()
                .id(refreshTokenId)
                .issuer(authProperties.getIssuerName())
                .issuedAt(refreshTokenIssuedAt)
                .expiration(refreshTokenExpiration)
                .signWith(key.toPrivateKey())
                .subject(user.getId().toString())
                .compact();

        UserAgentParser.UserAgentParseResult userAgentParseResult = UserAgentParser.parse(userAgent);

        refreshTokenSessionService.save(RefreshTokenSession.builder()
                .id(UUID.randomUUID().toString())
                .tokenId(refreshTokenId)
                .userId(user.getId())
                .createdAt(refreshTokenIssuedAt.toInstant())
                .expiresAt(refreshTokenExpiration.toInstant())
                .ipAddress(ipAddress)
                .osSystem(userAgentParseResult.os())
                .browser(userAgentParseResult.browser())
                .build());

        return Pair.of(accessToken, refreshToken);
    }

    private void validateRefreshToken(Jws<Claims> parsedProvidedRefreshToken, RefreshTokenSession refreshTokenSession,
                                      UserAgentParser.UserAgentParseResult userAgentParseResult) {
        Long providedUserId = Long.valueOf(parsedProvidedRefreshToken.getPayload().getSubject());

        if (!providedUserId.equals(refreshTokenSession.getUserId())) {
            String message = String.format("Provided refresh token's user id '%d' does not equal to persisted session user id '%d'",
                    providedUserId, refreshTokenSession.getUserId());
            log.info(message);
            throw new GenericRefreshTokenException(message, RefreshTokenExceptionErrorCode.USER_IDS_MISMATCH);
        }

        if (!userAgentParseResult.os().equals(refreshTokenSession.getOsSystem())) {
            String message = String.format("Provided refresh token's OS '%s' does not equal to persisted OS '%s'",
                    userAgentParseResult.os(), refreshTokenSession.getOsSystem());
            log.info(message);
            throw new GenericRefreshTokenException(message, RefreshTokenExceptionErrorCode.OS_MISMATCH);
        }

        if (!userAgentParseResult.browser().equals(refreshTokenSession.getBrowser())) {
            String message = String.format("Provided refresh token's browser '%s' does not equal to persisted browser '%s'",
                    userAgentParseResult.browser(), refreshTokenSession.getBrowser());
            log.info(message);
            throw new GenericRefreshTokenException(message, RefreshTokenExceptionErrorCode.BROWSER_MISMATCH);
        }

        if (refreshTokenSession.isRevoked()) {
            String message = String.format("Provided refresh token for user '%d' was revoked",
                    providedUserId);
            log.info(message);
            throw new GenericRefreshTokenException(message, RefreshTokenExceptionErrorCode.SESSION_REVOKED);
        }
    }
}
