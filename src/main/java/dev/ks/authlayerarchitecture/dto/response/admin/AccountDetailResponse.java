package dev.ks.authlayerarchitecture.dto.response.admin;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AccountDetailResponse(
        UUID id,
        String       email,
        String       firstName,
        String       lastName,
        boolean      emailVerified,
        boolean      enabled,
        List<String> roles,
        Instant      createdAt,
        Instant      updatedAt,
        Instant      passwordChangedAt,
        Instant anonymizedAt
) {}
