package com.dice.auth.wellknown;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class WellKnownRestController {

    private final JWKSet jwkSet;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return jwkSet.toJSONObject();
    }

    @GetMapping("/.well-known/openid-configuration")
    public Map<String, Object> getConfig() {
        return Map.of(
                "jwks_uri", "http://localhost:5000/.well-known/jwks.json",
                "issuer", "http://localhost:5000"
        );
    }
}
