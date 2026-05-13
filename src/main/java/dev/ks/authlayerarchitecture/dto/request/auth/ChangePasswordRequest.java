package dev.ks.authlayerarchitecture.dto.request.auth;

import dev.ks.authlayerarchitecture.validation.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @ValidPassword
        String newPassword
) {}
