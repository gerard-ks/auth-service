package dev.ks.authlayerarchitecture.cache;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetStore {
    void save(UUID accountId, UUID token);

    Optional<UUID> findTokenByAccountId(UUID accountId);

    Optional<UUID> findAccountIdByToken(UUID token);

    void delete(UUID accountId, UUID token);
}
