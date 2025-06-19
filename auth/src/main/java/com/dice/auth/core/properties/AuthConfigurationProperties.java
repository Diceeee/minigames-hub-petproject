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

    String rsaKeyId;
    String issuerName;
    
    // IP address handling configuration
    boolean trustProxyHeaders;
    boolean preferExternalIp;

    @ConstructorBinding
    public AuthConfigurationProperties(Integer refreshWindowInMinutes, Integer refreshTokenExpirationInDays, Integer accessTokenExpirationInMinutes,
                                       String rsaKeyId, String issuerName, Boolean trustProxyHeaders, Boolean preferExternalIp) {
        this.refreshWindowInMinutes = refreshWindowInMinutes == null ? 5 : refreshWindowInMinutes;
        this.refreshTokenExpirationInDays = refreshTokenExpirationInDays == null ? 60 : refreshTokenExpirationInDays;
        this.accessTokenExpirationInMinutes = accessTokenExpirationInMinutes == null ? 30 : accessTokenExpirationInMinutes;

        this.rsaKeyId = rsaKeyId == null ? "authKeyId" : rsaKeyId;
        this.issuerName = issuerName == null ? "minigames-hub" : issuerName;
        
        this.trustProxyHeaders = trustProxyHeaders == null || trustProxyHeaders;
        this.preferExternalIp = preferExternalIp != null && preferExternalIp;
    }
}
