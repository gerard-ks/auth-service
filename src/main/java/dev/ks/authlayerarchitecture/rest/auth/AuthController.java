package dev.ks.authlayerarchitecture.rest.auth;

import dev.ks.authlayerarchitecture.dto.request.auth.*;
import dev.ks.authlayerarchitecture.dto.response.auth.LoginResponse;
import dev.ks.authlayerarchitecture.dto.response.auth.TokenResponse;
import dev.ks.authlayerarchitecture.exception.token.InvalidRefreshTokenException;
import dev.ks.authlayerarchitecture.security.jwt.TokenPair;
import dev.ks.authlayerarchitecture.security.web.TokenResponseStrategy;
import dev.ks.authlayerarchitecture.security.web.TokenStrategyFactory;
import dev.ks.authlayerarchitecture.service.auth.AuthService;
import dev.ks.authlayerarchitecture.service.auth.RegisterService;
import dev.ks.authlayerarchitecture.service.auth.TokenService;
import dev.ks.authlayerarchitecture.service.password.PasswordResetService;
import dev.ks.authlayerarchitecture.service.verification.EmailVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterService registerService;
    private final AuthService authService;
    private final TokenService tokenService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final TokenStrategyFactory tokenStrategyFactory;


    public AuthController(RegisterService registerService, AuthService authService, TokenService tokenService, EmailVerificationService emailVerificationService, PasswordResetService passwordResetService, TokenStrategyFactory tokenStrategyFactory) {
        this.registerService = registerService;
        this.authService = authService;
        this.tokenService = tokenService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.tokenStrategyFactory = tokenStrategyFactory;
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        registerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify-email/token")
    public ResponseEntity<Void> verifyByToken(
            @Valid @RequestBody VerifyEmailTokenRequest request
    ) {
        emailVerificationService.verifyByToken(request.token());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email/otp")
    public ResponseEntity<Void> verifyByOtp(
            @Valid @RequestBody VerifyEmailOtpRequest request
    ) {
        emailVerificationService.verifyByOtp(
                request.email(),
                request.otpCode()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        emailVerificationService.resendVerification(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Token-Strategy", required = false) String strategyName,
            HttpServletResponse response
    ) {

        TokenResponseStrategy strategy =
                tokenStrategyFactory.resolve(strategyName);

        TokenPair pair = authService.login(request, strategy.strategyName());

        LoginResponse body = strategy.buildResponse(
                response,
                pair.accessToken(),
                pair.refreshToken(),
                pair.expiresIn()
        );

        return ResponseEntity.ok(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestBody(required = false) RefreshRequest request,
            @RequestHeader(value = "X-Token-Strategy", required = false)
            String strategyName,
            @CookieValue(value = "refreshToken", required = false)
            String cookieToken,
            HttpServletResponse response
    ) {
        String refreshToken = resolveRefreshToken(
                request, cookieToken
        );

        TokenPair pair = tokenService.refresh(refreshToken);

        TokenResponseStrategy strategy =
                tokenStrategyFactory.resolve(strategyName);

        LoginResponse strategyResponse = strategy.buildResponse(
                response,
                pair.accessToken(),
                pair.refreshToken(),
                pair.expiresIn()
        );

        return ResponseEntity.ok(new TokenResponse(
                strategyResponse.accessToken(),
                strategyResponse.expiresIn(),
                strategyResponse.refreshToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody(required = false) RefreshRequest request,
            @CookieValue(value = "refreshToken", required = false) String cookieToken
    ) {
        String refreshToken = resolveRefreshToken(
                request, cookieToken
        );

        tokenService.revoke(refreshToken);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        // toujours 200
        passwordResetService.forgotPassword(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(
                request.token(),
                request.newPassword()
        );
        return ResponseEntity.ok().build();
    }

    private String resolveRefreshToken(
            RefreshRequest request,
            String cookieToken
    ) {
        // BEARER
        if (request != null
                && request.refreshToken() != null
                && !request.refreshToken().isBlank()) {
            return request.refreshToken();
        }

        // COOKIE
        if (cookieToken != null && !cookieToken.isBlank()) {
            return cookieToken;
        }

        throw new InvalidRefreshTokenException();
    }
}
