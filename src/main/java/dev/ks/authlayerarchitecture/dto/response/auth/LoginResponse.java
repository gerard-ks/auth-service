package dev.ks.authlayerarchitecture.dto.response.auth;

public record LoginResponse(
        String accessToken,
        Long   expiresIn,
        String refreshToken
) {}
