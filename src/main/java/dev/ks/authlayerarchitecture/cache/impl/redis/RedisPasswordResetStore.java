package dev.ks.authlayerarchitecture.cache.impl.redis;

import dev.ks.authlayerarchitecture.cache.PasswordResetStore;
import dev.ks.authlayerarchitecture.constant.RedisKeysConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RedisPasswordResetStore implements PasswordResetStore {

    private final StringRedisTemplate redisTemplate;

    private final long ttlMinutes;

    public RedisPasswordResetStore(StringRedisTemplate redisTemplate,
                                   @Value("${auth-service.verification.ttl-minutes}") long ttlMinutes) {
        this.redisTemplate = redisTemplate;
        this.ttlMinutes = ttlMinutes;
    }

    @Override
    public void save(UUID accountId, UUID token) {
        Duration ttl = Duration.ofMinutes(ttlMinutes);

        // Supprimer l'ancien token si existant
        String oldTokenStr = redisTemplate.opsForValue()
                .get(RedisKeysConstants.resetAccount(accountId));

        if (oldTokenStr != null) {
            try {
                UUID oldToken = UUID.fromString(oldTokenStr);
                redisTemplate.delete(RedisKeysConstants.resetToken(oldToken));
            } catch (IllegalArgumentException ex) {
                log.warn(
                        "Invalid old reset token [accountId={}]",
                        accountId
                );
            }
        }

        // Sauvegarder le nouveau token
        redisTemplate.opsForValue().set(
                RedisKeysConstants.resetToken(token),
                accountId.toString(),
                ttl
        );

        redisTemplate.opsForValue().set(
                RedisKeysConstants.resetAccount(accountId),
                token.toString(),
                ttl
        );
    }

    @Override
    public Optional<UUID> findTokenByAccountId(UUID accountId) {
        String tokenStr = redisTemplate.opsForValue()
                .get(RedisKeysConstants.resetAccount(accountId));

        if (tokenStr == null) return Optional.empty();

        try {
            return Optional.of(UUID.fromString(tokenStr));
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid token UUID [accountId={}]", accountId);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UUID> findAccountIdByToken(UUID token) {
        String accountIdStr = redisTemplate.opsForValue()
                .get(RedisKeysConstants.resetToken(token));

        if (accountIdStr == null) return Optional.empty();

        try {
            return Optional.of(UUID.fromString(accountIdStr));
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid accountId UUID [token={}]", token);
            return Optional.empty();
        }
    }

    @Override
    public void delete(UUID accountId, UUID token) {
        redisTemplate.delete(RedisKeysConstants.resetAccount(accountId));
        redisTemplate.delete(RedisKeysConstants.resetToken(token));
    }
}
