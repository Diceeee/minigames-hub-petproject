package com.dice.gateway.core;

import com.dice.gateway.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Removes headers that are used for internal purposes from requests if they are present, such as User Id header.
 */
@Slf4j
@Component
public class HeadersSecurityFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getRequest().getHeaders().containsKey(Headers.USER_ID)) {
            log.warn("Forbidden '{}' header with value '{}' found and filtered from request:\n{}",
                    Headers.USER_ID, exchange.getRequest().getHeaders().get(Headers.USER_ID), exchange.getRequest());

            ServerWebExchange exchangeWithRemovedUserIdHeader = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .headers(headers -> headers.remove(Headers.USER_ID))
                            .build())
                    .build();

            return chain.filter(exchangeWithRemovedUserIdHeader);
        } else {
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
