package dev.ks.authlayerarchitecture.cache;

import java.util.UUID;

public record VerificationData(
        UUID   accountId,
        String email,
        UUID linkToken,
        String otpCode,
        String createdAt,
        String expiresAt
) {}
