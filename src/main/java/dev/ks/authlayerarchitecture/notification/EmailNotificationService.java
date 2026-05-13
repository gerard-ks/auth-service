package dev.ks.authlayerarchitecture.notification;

public interface EmailNotificationService {
    void sendVerificationEmail(
            String to,
            String linkUrl,
            String otpCode,
            int ttlMinutes
    );

    void sendVerificationResendEmail(
            String to,
            String linkUrl,
            String otpCode,
            int ttlMinutes
    );

    void sendPasswordResetEmail(
            String to,
            String resetUrl,
            int ttlMinutes
    );

    void sendPasswordResetConfirmEmail(
            String to,
            String datetime
    );

    void sendPasswordChangedEmail(
            String to,
            String datetime
    );

    void sendAccountDisabledEmail(
            String to,
            String supportUrl
    );
}
