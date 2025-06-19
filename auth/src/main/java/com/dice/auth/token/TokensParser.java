package com.dice.auth.token;

import com.dice.auth.RsaKeyProvider;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TokensParser {

    private final Clock jjwtClock;
    private final RsaKeyProvider rsaKeyProvider;
    private final AuthConfigurationProperties authProperties;

    public Jws<Claims> parseToken(String jwtToken) throws JOSEException {
        RSAKey key = rsaKeyProvider.getRsaKey();
        JwtParser jwtParser = Jwts.parser()
                .requireIssuer(authProperties.getIssuerName())
                .verifyWith(key.toPublicKey())
                .clockSkewSeconds(60)
                .clock(jjwtClock)
                .build();

        return jwtParser.parseSignedClaims(jwtToken);
    }
}
