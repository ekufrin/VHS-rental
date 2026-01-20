package com.ekufrin.vhsrental.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.secret")
public record JWTConfigurationProperties(String key, long expiration) {
}