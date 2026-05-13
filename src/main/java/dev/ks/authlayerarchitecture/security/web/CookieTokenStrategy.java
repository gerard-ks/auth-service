package dev.ks.authlayerarchitecture.security.web;

import dev.ks.authlayerarchitecture.dto.response.auth.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class CookieTokenStrategy implements TokenResponseStrategy {

    private static final String COOKIE_NAME = "refreshToken";
    private static final String PATH = "/";

    @Value("${auth-service.refresh-token.ttl-days}")
    private int refreshTokenTtlDays;

    @Override
    public LoginResponse buildResponse(
            HttpServletResponse response,
            String accessToken,
            String refreshToken,
            long expiresIn
    ) {
        // Construction moderne via ResponseCookie Spring
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(PATH)
                .maxAge(Duration.ofDays(refreshTokenTtlDays))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new LoginResponse(
                accessToken,
                expiresIn,
                null // refreshToken caché dans le cookie
        );
    }

    @Override
    public String strategyName() {
        return "COOKIE";
    }
}
