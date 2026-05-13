package dev.ks.authlayerarchitecture.security.jwt;

public record TokenPair(
        String accessToken,
        String refreshToken,
        long   expiresIn
) {}
