package dev.ks.authlayerarchitecture.security.web;

import dev.ks.authlayerarchitecture.dto.response.auth.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class BearerTokenStrategy implements TokenResponseStrategy {


    @Override
    public LoginResponse buildResponse(
            HttpServletResponse response,
            String accessToken,
            String refreshToken,
            long expiresIn
    ) {
        // Tout dans le body
        return new LoginResponse(accessToken, expiresIn, refreshToken);
    }

    @Override
    public String strategyName() {
        return "BEARER";
    }
}
