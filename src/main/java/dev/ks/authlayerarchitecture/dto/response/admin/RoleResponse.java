package dev.ks.authlayerarchitecture.dto.response.admin;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        boolean systemRole,
        List<String> permissions,
        Instant createdAt,
        Instant updatedAt
) {}
