package com.dice.auth.core;

import com.dice.auth.AuthConstants;
import com.dice.auth.core.access.Roles;
import com.dice.auth.core.properties.AuthConfigurationProperties;
import com.dice.auth.email.EmailOrUsernameAndPasswordAuthenticationFilter;
import com.dice.auth.email.EmailPasswordAuthenticationProvider;
import com.dice.auth.token.AccessTokenCookieBearerTokenResolver;
import com.dice.auth.token.TokensGeneratingAuthenticationSuccessHandler;
import com.dice.auth.token.TokensLogoutHandler;
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
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    public JWKSource<SecurityContext> jwkSource(AuthConfigurationProperties authProperties) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(authProperties.getRsaKeyId())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return ((jwkSelector, context) -> jwkSelector.select(jwkSet));
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity, JwtDecoder jwtDecoder,
                                                   AccessTokenCookieBearerTokenResolver accessTokenCookieBearerTokenResolver,
                                                   TokensGeneratingAuthenticationSuccessHandler tokensGeneratingAuthenticationSuccessHandler,
                                                   EmailOrUsernameAndPasswordAuthenticationFilter emailOrUsernameAndPasswordAuthenticationFilter,
                                                   EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   UserDetailsService userDetailsService,
                                                   RefreshAccessTokenRedirectionFilter refreshAccessTokenRedirectionFilter,
                                                   TokensLogoutHandler tokensLogoutHandler) throws Exception {
        return httpSecurity.authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico",
                                                "/email/verification/**",
                                                AuthConstants.Uris.HOME)
                                        .permitAll()
                                        .requestMatchers(AuthConstants.Uris.REGISTER + "/**")
                                        .not().hasAnyRole(Roles.USER.getRoleWithoutPrefix(), Roles.ADMIN.getRoleWithoutPrefix())
                                        .anyRequest().hasAnyRole(Roles.USER.getRoleWithoutPrefix(), Roles.ADMIN.getRoleWithoutPrefix()))
                .authenticationProvider(emailPasswordAuthenticationProvider)
                .userDetailsService(userDetailsService)
                .formLogin(configurer -> configurer
                        .loginPage(AuthConstants.Uris.LOGIN).permitAll()
                        .successHandler(tokensGeneratingAuthenticationSuccessHandler))
                .oauth2Login(configurer -> configurer
                        .loginPage(AuthConstants.Uris.LOGIN).permitAll()
                        .successHandler(tokensGeneratingAuthenticationSuccessHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .bearerTokenResolver(accessTokenCookieBearerTokenResolver)
                )
                .sessionManagement(sessionConfigurer -> sessionConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(configurer -> configurer
                        .logoutUrl(AuthConstants.Uris.LOGOUT)
                        .addLogoutHandler(tokensLogoutHandler)
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterBefore(refreshAccessTokenRedirectionFilter, BearerTokenAuthenticationFilter.class)
                .addFilterAt(emailOrUsernameAndPasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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
    @Primary
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    public EmailOrUsernameAndPasswordAuthenticationFilter emailOrUsernameAndPasswordAuthenticationFilter(UserDetailsService userDetailsService,
                                                                                                         AuthConfigurationProperties authProperties,
                                                                                                         EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider,
                                                                                                         TokensGeneratingAuthenticationSuccessHandler tokensGeneratingAuthenticationSuccessHandler,
                                                                                                         MessageSource messageSource) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setMessageSource(messageSource);

        AuthenticationManager authenticationManager = new ProviderManager(List.of(daoAuthenticationProvider, emailPasswordAuthenticationProvider));
        EmailOrUsernameAndPasswordAuthenticationFilter emailOrUsernameAndPasswordAuthenticationFilter = new EmailOrUsernameAndPasswordAuthenticationFilter(authenticationManager,
                AuthConstants.Uris.LOGIN);
        emailOrUsernameAndPasswordAuthenticationFilter.setAuthenticationSuccessHandler(tokensGeneratingAuthenticationSuccessHandler);
        emailOrUsernameAndPasswordAuthenticationFilter.setMessageSource(messageSource);

        return emailOrUsernameAndPasswordAuthenticationFilter;
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
}
