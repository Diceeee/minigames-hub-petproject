package com.dice.auth.core;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.access.Roles;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.email.EmailPasswordAuthenticationProvider;
import com.dice.auth.token.AccessTokenCookieBearerTokenResolver;
import com.dice.auth.token.OAuth2LoginTokensGeneratingAuthenticationSuccessHandler;
import com.dice.auth.token.refresh.RefreshAccessTokenRedirectionFilter;
import com.dice.auth.token.refresh.RefreshValidator;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableConfigurationProperties(AuthConfigurationProperties.class)
public class SecurityConfiguration {

    @Bean
    public JWKSet jwkSet(AuthConfigurationProperties authProperties) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(authProperties.getRsaKeyId())
                .build();

        return new JWKSet(rsaKey);
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
                                                   OAuth2LoginTokensGeneratingAuthenticationSuccessHandler OAuth2LoginTokensGeneratingAuthenticationSuccessHandler,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return httpSecurity.authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico",
                                                "/api/public/email/verification/**",
                                                "/.well-known/**",
                                                "/api/public/**")
                                        .permitAll()
                                        .requestMatchers(AuthConstants.Uris.REGISTER + "/**")
                                        .not().hasAnyRole(Roles.USER.getRoleWithoutPrefix(), Roles.ADMIN.getRoleWithoutPrefix())
                                        .anyRequest().hasAnyRole(Roles.USER.getRoleWithoutPrefix(), Roles.ADMIN.getRoleWithoutPrefix()))
                .oauth2Login(configurer -> configurer
                        .loginPage(AuthConstants.Uris.LOGIN).permitAll()
                        .successHandler(OAuth2LoginTokensGeneratingAuthenticationSuccessHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .bearerTokenResolver(accessTokenCookieBearerTokenResolver)
                )
                .sessionManagement(sessionConfigurer -> sessionConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .build();
    }

    @Bean
    public RefreshAccessTokenRedirectionFilter accessTokenRefreshFilter(RefreshValidator refreshValidator) {
        return new RefreshAccessTokenRedirectionFilter(refreshValidator);
    }

    @Bean
    public FilterRegistrationBean<RefreshAccessTokenRedirectionFilter> accessTokenRefreshFilterRegistrationBean(RefreshAccessTokenRedirectionFilter refreshAccessTokenRedirectionFilter) {
        FilterRegistrationBean<RefreshAccessTokenRedirectionFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>(refreshAccessTokenRedirectionFilter);
        filterFilterRegistrationBean.setEnabled(false);
        return filterFilterRegistrationBean;
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
