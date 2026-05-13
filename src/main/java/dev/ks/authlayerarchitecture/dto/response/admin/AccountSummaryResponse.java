package dev.ks.authlayerarchitecture.dto.response.admin;

import java.time.Instant;
import java.util.UUID;

public record AccountSummaryResponse(
        UUID id,
        String  email,
        String  firstName,
        String  lastName,
        boolean emailVerified,
        boolean enabled,
        Instant createdAt
) {}
