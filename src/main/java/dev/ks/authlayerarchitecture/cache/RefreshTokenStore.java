package dev.ks.authlayerarchitecture.cache;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RefreshTokenStore {
    void save(
            UUID tokenId,
            UUID accountId,
            String email,
            String strategy
    );

    Optional<RefreshTokenData> findById(UUID tokenId);

    void revoke(UUID tokenId);

    void revokeAll(UUID accountId);

    Set<UUID> findActiveSessionIds(UUID accountId);
}
