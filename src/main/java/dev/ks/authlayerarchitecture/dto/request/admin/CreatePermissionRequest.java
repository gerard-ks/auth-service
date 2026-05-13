package dev.ks.authlayerarchitecture.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank(message = "Permission name is required")
        @Size(max = 100, message = "Permission name must not exceed 100 characters")
        @Pattern(
                regexp = "^[a-z]+:[a-z]+$",
                message = "Permission name must follow format resource:action (e.g. accounts:read)"
        )
        String name,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description
) {}
