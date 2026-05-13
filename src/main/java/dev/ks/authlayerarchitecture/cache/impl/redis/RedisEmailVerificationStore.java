package dev.ks.authlayerarchitecture.cache.impl.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ks.authlayerarchitecture.cache.EmailVerificationStore;
import dev.ks.authlayerarchitecture.cache.VerificationData;
import dev.ks.authlayerarchitecture.constant.RedisKeysConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RedisEmailVerificationStore implements EmailVerificationStore {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final long ttlMinutes;
    private final long resendDelayMinutes;
    private final int otpMaxAttempts;
    private final long otpBlockMinutes;

    public RedisEmailVerificationStore(StringRedisTemplate redisTemplate,
                                       ObjectMapper objectMapper,
                                       @Value("${auth-service.verification.ttl-minutes}") long ttlMinutes,
                                       @Value("${auth-service.verification.resend-delay-minutes}") long resendDelayMinutes,
                                       @Value("${auth-service.otp.max-attempts}")  int otpMaxAttempts,
                                       @Value("${auth-service.otp.block-minutes}") long otpBlockMinutes) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttlMinutes = ttlMinutes;
        this.resendDelayMinutes = resendDelayMinutes;
        this.otpMaxAttempts = otpMaxAttempts;
        this.otpBlockMinutes = otpBlockMinutes;
    }

    @Override
    public void save(UUID accountId, String email, UUID linkToken, String otpCode) {
        Instant now       = Instant.now();
        Instant expiresAt = now.plus(Duration.ofMinutes(ttlMinutes));

        Map<String, String> data = Map.of(
                "accountId", accountId.toString(),
                "email",     email,
                "linkToken", linkToken.toString(),
                "otpCode",   otpCode,
                "createdAt", now.toString(),
                "expiresAt", expiresAt.toString()
        );

        try {
            String json = objectMapper.writeValueAsString(data);
            Duration ttl = Duration.ofMinutes(ttlMinutes);

            // email_verify:{accountId} → JSON
            redisTemplate.opsForValue().set(
                    RedisKeysConstants.emailVerify(accountId),
                    json,
                    ttl
            );

            // email_verify:link:{linkToken} → accountId
            redisTemplate.opsForValue().set(
                    RedisKeysConstants.emailVerifyLink(linkToken),
                    accountId.toString(),
                    ttl
            );

        } catch (JsonProcessingException ex) {
            log.error(
                    "Failed to serialize email verification [accountId={}]",
                    accountId
            );
            throw new IllegalStateException(
                    "Failed to serialize email verification", ex
            );
        }
    }

    @Override
    public Optional<VerificationData> findByAccountId(UUID accountId) {
        String json = redisTemplate.opsForValue()
                .get(RedisKeysConstants.emailVerify(accountId));

        return deserialize(json);
    }

    @Override
    public Optional<VerificationData> findByLinkToken(UUID linkToken) {
        String accountIdStr = redisTemplate.opsForValue()
                .get(RedisKeysConstants.emailVerifyLink(linkToken));

        if (accountIdStr == null) return Optional.empty();

        try {
            return findByAccountId(UUID.fromString(accountIdStr));
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid accountId in emailVerifyLink [linkToken={}]", linkToken);
            return Optional.empty();
        }
    }

    @Override
    public void delete(UUID accountId, UUID linkToken) {
        redisTemplate.delete(RedisKeysConstants.emailVerify(accountId));
        redisTemplate.delete(RedisKeysConstants.emailVerifyLink(linkToken));
    }

    @Override
    public boolean canResend(UUID accountId) {
        return !redisTemplate.hasKey(RedisKeysConstants.emailVerifyResend(accountId));
    }

    @Override
    public void markResent(UUID accountId) {
        redisTemplate.opsForValue().set(
                RedisKeysConstants.emailVerifyResend(accountId),
                "1",
                Duration.ofMinutes(resendDelayMinutes)
        );
    }

    @Override
    public void incrementOtpAttempt(UUID accountId) {
        String key    = RedisKeysConstants.otpAttempt(accountId);
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(
                    key,
                    Duration.ofMinutes(otpBlockMinutes)
            );
        }
    }

    @Override
    public int getOtpAttempts(UUID accountId) {
        String value = redisTemplate.opsForValue()
                .get(RedisKeysConstants.otpAttempt(accountId));

        if (value == null) return 0;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            log.warn("Invalid OTP attempt count [accountId={}]", accountId);
            return 0;
        }
    }

    @Override
    public boolean isOtpBlocked(UUID accountId) {
        return getOtpAttempts(accountId) >= otpMaxAttempts;
    }

    private Optional<VerificationData> deserialize(String json) {
        if (json == null) return Optional.empty();

        try {
            Map<?, ?> map = objectMapper.readValue(json, Map.class);

            return Optional.of(new VerificationData(
                    UUID.fromString((String) map.get("accountId")),
                    (String) map.get("email"),
                    UUID.fromString((String) map.get("linkToken")),
                    (String) map.get("otpCode"),
                    (String) map.get("createdAt"),
                    (String) map.get("expiresAt")
            ));

        } catch (JsonProcessingException ex) {
            log.error("Failed to deserialize email verification data");
            return Optional.empty();
        }
    }
}
