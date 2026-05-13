package dev.ks.authlayerarchitecture.service.auth;

public interface BruteForceService {

    void checkNotBlocked(String email);

    void recordFailedAttempt(String email);

    void reset(String email);
}

