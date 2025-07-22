package com.dice.gateway.core;

import com.dice.gateway.core.access.Roles;
import com.dice.gateway.jwt.AccessTokenCookieBearerTokenConverter;
import com.dice.gateway.jwt.LimitedAccessOnExpirationJwtAuthenticationConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfiguration {

    @Bean
    @Order(Integer.MIN_VALUE)
    public CorsWebFilter corsWebFilter(GatewayProperties gatewayProperties) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin(gatewayProperties.getServices().getFrontend().getUri());
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(GatewayProperties gatewayProperties) {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(gatewayProperties.getServices().getAuth().getUri() + "/.well-known/jwks.json")
                .build();

        String issuer = gatewayProperties.getServices().getAuth().getUri();
        OAuth2TokenValidator<Jwt> issuerValidator = new JwtIssuerValidator(issuer);
        decoder.setJwtValidator(issuerValidator);

        return decoder;
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter reactiveJwtAuthenticationConverterAdapter(Clock clock) {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthoritiesClaimDelimiter(";");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        LimitedAccessOnExpirationJwtAuthenticationConverter limitedAccessOnExpirationJwtAuthenticationConverter = new LimitedAccessOnExpirationJwtAuthenticationConverter(clock, jwtAuthenticationConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(limitedAccessOnExpirationJwtAuthenticationConverter);
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, ReactiveJwtDecoder reactiveJwtDecoder,
                                                      ReactiveJwtAuthenticationConverterAdapter reactiveJwtAuthenticationConverterAdapter,
                                                      AccessTokenCookieBearerTokenConverter accessTokenCookieBearerTokenConverter) {
        http
                .csrf(csrf -> {
                            var delegate = new ServerCsrfTokenRequestAttributeHandler();
                            csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(delegate::handle);
                        }
                )
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/public/auth/login/**", "/csrf", "/public/auth/register/**", "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/public/auth/oauth2/authorization/**", "/public/auth/email/verification/**", "/public/auth/user/me", "/public/auth/refresh")
                        .permitAll()
                        .pathMatchers("/public/**")
                        .hasAnyRole(Roles.USER.getRoleWithoutPrefix(), Roles.ADMIN.getRoleWithoutPrefix())
                        .pathMatchers("/admin/**")
                        .hasRole(Roles.ADMIN.getRoleWithoutPrefix())
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtDecoder(reactiveJwtDecoder)
                                .jwtAuthenticationConverter(reactiveJwtAuthenticationConverterAdapter))
                        .bearerTokenConverter(accessTokenCookieBearerTokenConverter)
                );
        return http.build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
