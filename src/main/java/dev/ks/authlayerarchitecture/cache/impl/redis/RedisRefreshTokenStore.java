package dev.ks.authlayerarchitecture.cache.impl.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ks.authlayerarchitecture.cache.RefreshTokenData;
import dev.ks.authlayerarchitecture.cache.RefreshTokenStore;
import dev.ks.authlayerarchitecture.constant.RedisKeysConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final long ttlDays;

    public RedisRefreshTokenStore(StringRedisTemplate redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Value("${auth-service.refresh-token.ttl-days}") long ttlDays) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttlDays = ttlDays;
    }

    @Override
    public void save(UUID tokenId, UUID accountId, String email, String strategy) {
        Instant now       = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(ttlDays));

        Map<String, Object> data = Map.of(
                "accountId", accountId.toString(),
                "email",     email,
                "strategy",  strategy,
                "issuedAt",  now.toString(),
                "expiresAt", expiresAt.toString(),
                "revoked",   false
        );

        try {
            String json = objectMapper.writeValueAsString(data);

            // Sauvegarder le token
            redisTemplate.opsForValue().set(
                    RedisKeysConstants.refreshSession(tokenId),
                    json,
                    Duration.ofDays(ttlDays)
            );

            // Ajouter au SET des sessions du compte
            redisTemplate.opsForSet().add(
                    RedisKeysConstants.accountSessions(accountId),
                    tokenId.toString()
            );

            redisTemplate.expire(
                    RedisKeysConstants.accountSessions(accountId),
                    Duration.ofDays(ttlDays)
            );

        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to serialize refresh token [accountId={}]",
                    accountId
            );
            throw new IllegalStateException(
                    "Failed to serialize refresh token", ex
            );
        }
    }

    @Override
    public Optional<RefreshTokenData> findById(UUID tokenId) {
        String json = redisTemplate.opsForValue()
                .get(RedisKeysConstants.refreshSession(tokenId));

        if (json == null) return Optional.empty();

        try {
            Map<?, ?> map = objectMapper.readValue(json, Map.class);

            return Optional.of(new RefreshTokenData(
                    UUID.fromString((String) map.get("accountId")),
                    (String) map.get("email"),
                    (String) map.get("strategy"),
                    (String) map.get("issuedAt"),
                    (String) map.get("expiresAt"),
                    (Boolean) map.get("revoked")
            ));

        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to deserialize refresh token [tokenId={}]",
                    tokenId
            );
            return Optional.empty();
        }
    }

    @Override
    public void revoke(UUID tokenId) {
        String json = redisTemplate.opsForValue()
                .get(RedisKeysConstants.refreshSession(tokenId));

        if (json == null) return;

        try {
            Map<String, Object> map = objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory()
                            .constructMapType(Map.class, String.class, Object.class)
            );

            UUID accountId = UUID.fromString(
                    (String) map.get("accountId")
            );

            // Marquer comme révoqué
            map.put("revoked", true);

            redisTemplate.opsForValue().set(
                    RedisKeysConstants.refreshSession(tokenId),
                    objectMapper.writeValueAsString(map),
                    Duration.ofDays(ttlDays)
            );

            // Retirer du SET des sessions
            redisTemplate.opsForSet().remove(
                    RedisKeysConstants.accountSessions(accountId),
                    tokenId.toString()
            );

        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to revoke refresh token [tokenId={}]",
                    tokenId
            );
        }
    }

    @Override
    public void revokeAll(UUID accountId) {
        Set<String> tokenIds = redisTemplate.opsForSet()
                .members(RedisKeysConstants.accountSessions(accountId));

        if (tokenIds == null || tokenIds.isEmpty()) return;

        tokenIds.forEach(tokenIdStr -> {
            try {
                revoke(UUID.fromString(tokenIdStr));
            } catch (IllegalArgumentException ex) {
                log.warn(
                        "Invalid tokenId in sessions set [accountId={}] : {}",
                        accountId,
                        tokenIdStr
                );
            }
        });

        // Supprimer le SET des sessions
        redisTemplate.delete(RedisKeysConstants.accountSessions(accountId));
    }

    @Override
    public Set<UUID> findActiveSessionIds(UUID accountId) {
        Set<String> members = redisTemplate.opsForSet()
                .members(RedisKeysConstants.accountSessions(accountId));

        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }

        return members.stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }
}
