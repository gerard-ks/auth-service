package dev.ks.authlayerarchitecture.cache;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationStore {
    void save(
            UUID accountId,
            String email,
            UUID linkToken,
            String otpCode
    );

    Optional<VerificationData> findByAccountId(UUID accountId);

    Optional<VerificationData> findByLinkToken(UUID linkToken);

    void delete(UUID accountId, UUID linkToken);

    boolean canResend(UUID accountId);

    void markResent(UUID accountId);

    void incrementOtpAttempt(UUID accountId);

    int getOtpAttempts(UUID accountId);

    boolean isOtpBlocked(UUID accountId);
}
