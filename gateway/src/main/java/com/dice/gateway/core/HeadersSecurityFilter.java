package com.dice.gateway.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * Removes headers that are used for internal purposes from requests if they are present, such as User Id header.
 */
@Slf4j
@Component
public class HeadersSecurityFilter implements GlobalFilter, Ordered {

    private static final List<String> FORBIDDEN_HEADERS = List.of(Headers.SESSION_ID, Headers.USER_ID);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        FORBIDDEN_HEADERS.stream()
                .filter(forbiddenHeader -> exchange.getRequest().getHeaders().containsKey(forbiddenHeader))
                .map(forbiddenHeader -> String.join("=", forbiddenHeader, Objects.requireNonNull(exchange.getRequest().getHeaders().get(forbiddenHeader)).toString()))
                .reduce((s1, s2) -> String.join(", ", s1, s2))
                .ifPresent(logMessage -> log.warn("Possible try of forbidden headers manual injection detected for request: {}", logMessage));

        ServerWebExchange exchangeWithRemovedUserHeaders = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .headers(headers -> headers
                                .remove(Headers.USER_ID)
                                .remove(Headers.SESSION_ID))
                        .build())
                .build();

        return chain.filter(exchangeWithRemovedUserHeaders);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
