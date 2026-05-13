package dev.ks.authlayerarchitecture.cache;

import java.util.List;

public interface PermissionCacheStore {
    List<String> getPermissions(String roleName);

    void evict(String roleName);

    void evictAll();
}
