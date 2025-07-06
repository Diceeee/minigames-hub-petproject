package com.dice.gateway.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private Services services;

    @Data
    public static class Services {
        private ServiceConfig auth;
        private ServiceConfig frontend;
    }

    @Data
    public static class ServiceConfig {
        private String uri;
    }
}
