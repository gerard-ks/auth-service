package dev.ks.authlayerarchitecture.dto.request.auth;

import dev.ks.authlayerarchitecture.validation.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "New password is required")
        @ValidPassword
        String newPassword
) {}
