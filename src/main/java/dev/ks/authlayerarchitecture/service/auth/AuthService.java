package dev.ks.authlayerarchitecture.service.auth;

import dev.ks.authlayerarchitecture.dto.request.auth.LoginRequest;
import dev.ks.authlayerarchitecture.security.jwt.TokenPair;


public interface AuthService {
    TokenPair login(
            LoginRequest request,
            String strategy
    );
}
