package com.dice.auth.core.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@Validated
@ConfigurationProperties("auth")
public class AuthConfigurationProperties {

    Integer refreshWindowInMinutes;
    Integer refreshTokenExpirationInDays;
    Integer accessTokenExpirationInMinutes;
    Integer verificationEmailRateLimitInMinutesPerUser;

    String rsaKeyId;
    String issuerName;
    
    // IP address handling configuration
    boolean trustProxyHeaders;
    boolean preferExternalIp;

    String frontendUrl;

    @ConstructorBinding
    public AuthConfigurationProperties(Integer refreshWindowInMinutes, Integer refreshTokenExpirationInDays, Integer accessTokenExpirationInMinutes,
                                       Integer verificationEmailRateLimitInMinutesPerUser, String rsaKeyId, String issuerName,
                                       Boolean trustProxyHeaders, Boolean preferExternalIp, String frontendUrl) {
        this.refreshWindowInMinutes = refreshWindowInMinutes == null ? 5 : refreshWindowInMinutes;
        this.refreshTokenExpirationInDays = refreshTokenExpirationInDays == null ? 60 : refreshTokenExpirationInDays;
        this.accessTokenExpirationInMinutes = accessTokenExpirationInMinutes == null ? 15 : accessTokenExpirationInMinutes;
        this.verificationEmailRateLimitInMinutesPerUser = verificationEmailRateLimitInMinutesPerUser == null ? 5 : verificationEmailRateLimitInMinutesPerUser;

        this.rsaKeyId = rsaKeyId == null ? "authKeyId" : rsaKeyId;
        this.issuerName = issuerName == null ? "http://localhost:5000" : issuerName;
        
        this.trustProxyHeaders = trustProxyHeaders == null || trustProxyHeaders;
        this.preferExternalIp = preferExternalIp != null && preferExternalIp;
        this.frontendUrl = frontendUrl == null ? "http://localhost:3000" : frontendUrl;
    }
}
