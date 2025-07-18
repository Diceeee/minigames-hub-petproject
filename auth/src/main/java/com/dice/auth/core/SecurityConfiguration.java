package com.dice.auth.core;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.access.Roles;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.email.EmailPasswordAuthenticationProvider;
import com.dice.auth.token.AccessTokenCookieBearerTokenResolver;
import com.dice.auth.token.OAuth2LoginTokensGeneratingAuthenticationSuccessHandler;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableConfigurationProperties(AuthConfigurationProperties.class)
public class SecurityConfiguration {

    @Bean
    public JWKSet jwkSet() throws IOException, ParseException {
        return JWKSet.load(new ClassPathResource("secrets/jwks.json").getInputStream());
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(JWKSet jwkSet) {
        return ((jwkSelector, context) -> jwkSelector.select(jwkSet));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthoritiesClaimDelimiter(";");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity, JwtDecoder jwtDecoder,
                                                   AccessTokenCookieBearerTokenResolver accessTokenCookieBearerTokenResolver,
                                                   OAuth2LoginTokensGeneratingAuthenticationSuccessHandler oAuth2LoginTokensGeneratingAuthenticationSuccessHandler,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers(AuthConstants.Uris.REGISTER + "/**")
                                .access(((authentication, context)
                                        -> new AuthorizationDecision(!authentication.get().isAuthenticated()
                                        || authentication.get() instanceof AnonymousAuthenticationToken
                                        || authentication.get().getAuthorities().isEmpty())))
                                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico",
                                        "/api/public/email/verification/**",
                                        "/.well-known/**",
                                        "/api/public/**", "/api/public/refresh")
                                .permitAll()
                                .anyRequest().hasAnyRole(Roles.USER.getRoleWithoutPrefix(), Roles.ADMIN.getRoleWithoutPrefix()))
                .oauth2Login(configurer -> configurer
                        .loginPage(AuthConstants.Uris.LOGIN).permitAll()
                        .authorizationEndpoint(authorization ->
                                authorization.baseUri("/api/public/oauth2/authorization")
                        )
                        .redirectionEndpoint(redirection ->
                                redirection.baseUri("/api/public/login/oauth2/code/*")
                        )
                        .successHandler(oAuth2LoginTokensGeneratingAuthenticationSuccessHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .bearerTokenResolver(accessTokenCookieBearerTokenResolver)
                )
                .sessionManagement(sessionConfigurer -> sessionConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(AbstractHttpConfigurer::disable) // handled by gateway
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider,
                                                       UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder,
                                                       MessageSource messageSource) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setMessageSource(messageSource);

        return new ProviderManager(List.of(daoAuthenticationProvider, emailPasswordAuthenticationProvider));
    }

    @Bean
    @Primary
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
