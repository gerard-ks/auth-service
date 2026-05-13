package dev.ks.authlayerarchitecture.dto.request.auth;

import dev.ks.authlayerarchitecture.validation.annotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password is required")
        @ValidPassword
        String password,

        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 100, message = "Last name must not exceed 100 characters")
        String lastName
) {}
