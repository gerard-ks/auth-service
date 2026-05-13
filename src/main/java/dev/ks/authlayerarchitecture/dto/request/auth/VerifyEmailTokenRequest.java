package dev.ks.authlayerarchitecture.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailTokenRequest(
        @NotBlank(message = "Token is required")
        String token
) {}
