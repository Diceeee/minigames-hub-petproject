package com.dice.auth.token.refresh;

import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.token.TokensParser;
import com.nimbusds.jose.JOSEException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
@AllArgsConstructor
public class RefreshValidator {

    private final Clock clock;
    private final TokensParser tokensParser;
    private final AuthConfigurationProperties authProperties;

    public boolean validateRefreshAllowed(String accessToken, String refreshToken) {
        if (refreshToken == null) {
            return false;
        }

        return validateRefreshAllowed(accessToken);
    }

    public boolean validateRefreshAllowed(String accessToken) {
        if (accessToken == null) {
            return true;
        }

        try {
            Jws<Claims> jws = tokensParser.parseToken(accessToken);

            Date expiration = jws.getPayload().getExpiration();
            Instant refreshWindowInstant = expiration.toInstant().minus(authProperties.getRefreshWindowInMinutes(), ChronoUnit.MINUTES);

            return clock.instant().isAfter(refreshWindowInstant);
        } catch (JOSEException e) {
            log.warn("Unusual refresh allowed validation caused exception for access token:{}", accessToken, e);
            return true;
        }
    }
}
