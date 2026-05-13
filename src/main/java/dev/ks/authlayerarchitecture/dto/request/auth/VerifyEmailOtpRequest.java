package dev.ks.authlayerarchitecture.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailOtpRequest(
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "OTP code is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "OTP must be exactly 6 digits"
        )
        String otpCode
) {}
