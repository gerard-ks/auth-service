package dev.ks.authlayerarchitecture.constant;

public final class ErrorCodeConstants {
    private ErrorCodeConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    //  AUTH

    public static final String INVALID_CREDENTIALS       = "INVALID_CREDENTIALS";
    public static final String ACCOUNT_NOT_VERIFIED      = "ACCOUNT_NOT_VERIFIED";
    public static final String ACCOUNT_DISABLED          = "ACCOUNT_DISABLED";
    public static final String ACCOUNT_LOCKED            = "ACCOUNT_LOCKED";
    public static final String ACCOUNT_NOT_ACTIVE        = "ACCOUNT_NOT_ACTIVE";
    public static final String INVALID_TOKEN_STRATEGY    = "INVALID_TOKEN_STRATEGY";

    //  TOKENS

    public static final String INVALID_REFRESH_TOKEN     = "INVALID_REFRESH_TOKEN";
    public static final String REFRESH_TOKEN_EXPIRED     = "REFRESH_TOKEN_EXPIRED";
    public static final String INVALID_ACCESS_TOKEN      = "INVALID_ACCESS_TOKEN";
    public static final String ACCESS_TOKEN_EXPIRED      = "ACCESS_TOKEN_EXPIRED";

    //  VÉRIFICATION EMAIL

    public static final String INVALID_VERIFICATION_TOKEN = "INVALID_VERIFICATION_TOKEN";
    public static final String VERIFICATION_TOKEN_EXPIRED = "VERIFICATION_TOKEN_EXPIRED";
    public static final String INVALID_OTP               = "INVALID_OTP";
    public static final String OTP_EXPIRED               = "OTP_EXPIRED";
    public static final String OTP_BLOCKED               = "OTP_BLOCKED";
    public static final String RESEND_TOO_SOON           = "RESEND_TOO_SOON";

    //  RESET PASSWORD

    public static final String INVALID_RESET_TOKEN       = "INVALID_RESET_TOKEN";
    public static final String RESET_TOKEN_EXPIRED       = "RESET_TOKEN_EXPIRED";

    //  PASSWORD

    public static final String INVALID_PASSWORD_POLICY   = "INVALID_PASSWORD_POLICY";
    public static final String SAME_PASSWORD             = "SAME_PASSWORD";
    public static final String INVALID_CURRENT_PASSWORD  = "INVALID_CURRENT_PASSWORD";

    //  COMPTES

    public static final String ACCOUNT_NOT_FOUND         = "ACCOUNT_NOT_FOUND";
    public static final String EMAIL_ALREADY_EXISTS      = "EMAIL_ALREADY_EXISTS";
    public static final String SELF_DISABLE_NOT_ALLOWED  = "SELF_DISABLE_NOT_ALLOWED";
    public static final String ACCOUNT_ALREADY_DISABLED  = "ACCOUNT_ALREADY_DISABLED";
    public static final String ACCOUNT_ALREADY_ENABLED   = "ACCOUNT_ALREADY_ENABLED";

    //  RÔLES

    public static final String ROLE_NOT_FOUND                    = "ROLE_NOT_FOUND";
    public static final String ROLE_ALREADY_EXISTS               = "ROLE_ALREADY_EXISTS";
    public static final String ROLE_ALREADY_ASSIGNED             = "ROLE_ALREADY_ASSIGNED";
    public static final String ROLE_NOT_ASSIGNED                 = "ROLE_NOT_ASSIGNED";
    public static final String ROLE_STILL_ASSIGNED               = "ROLE_STILL_ASSIGNED";
    public static final String SYSTEM_ROLE_DELETION_NOT_ALLOWED  = "SYSTEM_ROLE_DELETION_NOT_ALLOWED";

    //  PERMISSIONS

    public static final String PERMISSION_NOT_FOUND              = "PERMISSION_NOT_FOUND";
    public static final String PERMISSION_ALREADY_EXISTS         = "PERMISSION_ALREADY_EXISTS";
    public static final String PERMISSION_ALREADY_ASSIGNED       = "PERMISSION_ALREADY_ASSIGNED";
    public static final String PERMISSION_STILL_ASSIGNED         = "PERMISSION_STILL_ASSIGNED";
    public static final String SYSTEM_PERM_DELETION_NOT_ALLOWED  = "SYSTEM_PERM_DELETION_NOT_ALLOWED";
    public static final String PERMISSION_NOT_ASSIGNED = "PERMISSION_NOT_ASSIGNED";

    //  VALIDATION & SYSTÈME

    public static final String VALIDATION_ERROR          = "VALIDATION_ERROR";
    public static final String INTERNAL_SERVER_ERROR     = "INTERNAL_SERVER_ERROR";

    // Sécurité Spring
    public static final String ACCESS_DENIED          = "ACCESS_DENIED";
    public static final String UNAUTHENTICATED        = "UNAUTHENTICATED";
}
