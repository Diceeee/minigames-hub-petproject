package com.dice.auth;

import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RsaKeyProvider {

    private final AuthConfigurationProperties authProperties;
    private final JWKSource<SecurityContext> jwkSource;

    public RSAKey getRsaKey() throws KeySourceException {
        JWKSelector selector = new JWKSelector(
                new JWKMatcher.Builder()
                        .keyID(authProperties.getRsaKeyId())
                        .build()
        );

        return (RSAKey) jwkSource.get(selector, null).getFirst();
    }
}
