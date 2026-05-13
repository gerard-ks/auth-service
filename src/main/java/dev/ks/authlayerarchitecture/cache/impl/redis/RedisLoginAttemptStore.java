package dev.ks.authlayerarchitecture.cache.impl.redis;

import dev.ks.authlayerarchitecture.cache.LoginAttemptStore;
import dev.ks.authlayerarchitecture.constant.RedisKeysConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RedisLoginAttemptStore implements LoginAttemptStore {

    private final StringRedisTemplate redisTemplate;

    private final int maxAttempts;

    private final long blockMinutes;

    public RedisLoginAttemptStore(StringRedisTemplate redisTemplate,
                                  @Value("${auth-service.brute-force.max-attempts}") int maxAttempts,
                                  @Value("${auth-service.brute-force.block-minutes}") long blockMinutes) {
        this.redisTemplate = redisTemplate;
        this.maxAttempts = maxAttempts;
        this.blockMinutes = blockMinutes;
    }


    @Override
    public void increment(String email) {
        String key = RedisKeysConstants.loginAttempt(email);

        Long attempts = redisTemplate.opsForValue().increment(key);

        // Poser le TTL uniquement à la première tentative
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(blockMinutes));
        }
    }

    @Override
    public int getAttempts(String email) {
        String value = redisTemplate.opsForValue()
                .get(RedisKeysConstants.loginAttempt(email));

        if (value == null) return 0;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            log.warn(
                    "Invalid attempt count in Redis for email hash"
            );
            return 0;
        }
    }

    @Override
    public boolean isBlocked(String email) {
        return getAttempts(email) >= maxAttempts;
    }

    @Override
    public void reset(String email) {
        redisTemplate.delete(RedisKeysConstants.loginAttempt(email));
    }
}
