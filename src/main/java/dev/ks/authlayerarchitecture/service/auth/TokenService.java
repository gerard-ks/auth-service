package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.security.jwt.TokenPair;

import java.util.List;
import java.util.UUID;

public interface TokenService {

    TokenPair generatePair(
            UUID accountId,
            String email,
            List<String> roles,
            String strategy
    );

    TokenPair refresh(
            String refreshToken
    );

    void revoke(String refreshTokenStr);

    void revokeAll(UUID accountId);
}
