package dev.ks.authlayerarchitecture.constant;

import java.util.UUID;

public final class RedisKeysConstants {
    private RedisKeysConstants() {
        throw new UnsupportedOperationException("RedisKeys is Utility class");
    }

    public static String refreshSession(UUID tokenId) {
        return "refresh_session:" + tokenId;
    }

    public static String accountSessions(UUID accountId) {
        return "account_sessions:" + accountId;
    }

    public static String loginAttempt(String email) {
        return "login_attempt:" + email.toLowerCase();
    }


    public static String emailVerify(UUID accountId) {
        return "email_verify:" + accountId;
    }

    public static String emailVerifyLink(UUID linkToken) {
        return "email_verify:link:" + linkToken;
    }

    public static String resetToken(UUID token) {
        return "reset_token:" + token;
    }

    public static String resetAccount(UUID accountId) {
        return "reset_account:" + accountId;
    }


    public static String emailVerifyResend(UUID accountId) {
        return "email_verify_resend:" + accountId;
    }

    public static String otpAttempt(UUID accountId) {
        return "otp_attempt:" + accountId;
    }
}
