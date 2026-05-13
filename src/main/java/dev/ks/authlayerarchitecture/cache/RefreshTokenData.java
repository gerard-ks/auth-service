package dev.ks.authlayerarchitecture.cache;

import java.util.UUID;

public record RefreshTokenData(
        UUID accountId,
        String  email,
        String  strategy,
        String  issuedAt,
        String  expiresAt,
        boolean revoked
) {}
