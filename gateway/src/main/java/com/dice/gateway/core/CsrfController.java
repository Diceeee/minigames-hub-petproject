package com.dice.gateway.core;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public Mono<CsrfToken> csrf(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());
        return Objects.requireNonNullElseGet(csrfTokenMono, Mono::empty);
    }
}
