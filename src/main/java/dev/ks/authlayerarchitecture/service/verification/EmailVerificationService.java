package dev.ks.authlayerarchitecture.service.verification;

public interface EmailVerificationService {
    void initiateVerification(java.util.UUID accountId, String email);

    void verifyByToken(String token);

    void verifyByOtp(String email, String otpCode);

    void resendVerification(String email);
}
