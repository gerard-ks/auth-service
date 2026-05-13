package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.cache.LoginAttemptStore;
import dev.ks.authlayerarchitecture.exception.auth.AccountLockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BruteForceServiceImpl implements BruteForceService {

    private final LoginAttemptStore loginAttemptStore;

    public BruteForceServiceImpl(LoginAttemptStore loginAttemptStore) {
        this.loginAttemptStore = loginAttemptStore;
    }

    @Override
    public void checkNotBlocked(String email) {
        if (loginAttemptStore.isBlocked(email)) {
            log.warn("Account locked due to brute force [email={}]",
                    email);
            throw new AccountLockedException();
        }
    }

    @Override
    public void recordFailedAttempt(String email) {
        loginAttemptStore.increment(email);

        log.warn(
                "Failed login attempt [email={}] [attempts={}]",
                email,
                loginAttemptStore.getAttempts(email)
        );
    }

    @Override
    public void reset(String email) {
        loginAttemptStore.reset(email);
    }
}
