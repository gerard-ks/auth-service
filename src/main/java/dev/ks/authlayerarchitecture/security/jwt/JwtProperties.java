package dev.ks.authlayerarchitecture.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth-service.jwt")
public record JwtProperties(
        String secretKey,
        String issuer,
        long   ttlMinutes
) {}
