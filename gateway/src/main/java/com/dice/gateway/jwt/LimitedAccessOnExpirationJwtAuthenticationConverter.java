package com.dice.gateway.jwt;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

@AllArgsConstructor
public class LimitedAccessOnExpirationJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Clock clock;
    private final JwtAuthenticationConverter delegate;

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        AbstractAuthenticationToken token = delegate.convert(source);
        if (token instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Instant now = clock.instant();
            Instant tokenExpiresAt = source.getExpiresAt();

            if (Objects.requireNonNull(tokenExpiresAt).isAfter(now)) {
                return jwtAuthenticationToken;
            }
            return new JwtAuthenticationToken(source, Collections.emptyList());
        } else {
            throw new IllegalStateException("Unexpected authentication token type: " + token.getClass().getName());
        }
    }
}
