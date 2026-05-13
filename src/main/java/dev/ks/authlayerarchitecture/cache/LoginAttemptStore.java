package dev.ks.authlayerarchitecture.cache;

public interface LoginAttemptStore {
    void increment(String email);

    int getAttempts(String email);

    boolean isBlocked(String email);

    void reset(String email);
}
