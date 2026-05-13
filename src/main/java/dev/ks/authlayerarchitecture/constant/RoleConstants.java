package dev.ks.authlayerarchitecture.constant;

public final class RoleConstants {
     private RoleConstants() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
     }

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    public static final String SOURCE_REGISTRATION  = "REGISTRATION";
    public static final String SOURCE_ADMIN_MANUAL  = "ADMIN_MANUAL";

    public static final String PERM_ACCOUNTS_READ     = "accounts:read";
    public static final String PERM_ACCOUNTS_DISABLE  = "accounts:disable";
    public static final String PERM_ACCOUNTS_ENABLE   = "accounts:enable";
    public static final String PERM_ROLES_MANAGE      = "roles:manage";
    public static final String PERM_ROLES_ASSIGN      = "roles:assign";
    public static final String PERM_PERMISSIONS_MANAGE= "permissions:manage";
}
