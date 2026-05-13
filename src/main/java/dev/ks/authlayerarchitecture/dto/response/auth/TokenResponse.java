package dev.ks.authlayerarchitecture.dto.response.auth;

public record TokenResponse(
        String accessToken,
        Long   expiresIn,
        String refreshToken
) {}
