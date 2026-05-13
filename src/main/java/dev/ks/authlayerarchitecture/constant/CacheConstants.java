package dev.ks.authlayerarchitecture.constant;

public final class CacheConstants {
    private CacheConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String PERMISSIONS_CACHE = "permissions";
    public static final int PERMISSIONS_CACHE_TTL_MINUTES = 5;
}
