package dev.ks.authlayerarchitecture.security.web;

import dev.ks.authlayerarchitecture.dto.response.auth.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenResponseStrategy {
    LoginResponse buildResponse(
            HttpServletResponse response,
            String accessToken,
            String refreshToken,
            long   expiresIn
    );

    String strategyName();
}
