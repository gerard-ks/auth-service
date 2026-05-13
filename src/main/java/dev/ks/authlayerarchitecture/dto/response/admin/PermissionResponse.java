package dev.ks.authlayerarchitecture.dto.response.admin;

import java.time.Instant;
import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String  name,
        String  description,
        boolean systemPerm,
        Instant createdAt,
        Instant updatedAt
) {}
