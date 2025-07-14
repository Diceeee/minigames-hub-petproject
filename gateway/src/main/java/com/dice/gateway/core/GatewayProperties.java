package com.dice.gateway.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private Services services;

    @Data
    public static class Services {
        private ServiceConfig auth;
        private ServiceConfig gameClicker;
        private ServiceConfig frontend;
    }

    @Data
    public static class ServiceConfig {
        private String uri;
        private String serviceName;
    }
}
