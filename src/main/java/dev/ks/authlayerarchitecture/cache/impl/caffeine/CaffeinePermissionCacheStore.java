package dev.ks.authlayerarchitecture.cache.impl.caffeine;

import dev.ks.authlayerarchitecture.cache.PermissionCacheStore;
import dev.ks.authlayerarchitecture.constant.CacheConstants;
import dev.ks.authlayerarchitecture.repository.RolePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CaffeinePermissionCacheStore implements PermissionCacheStore {

    private final RolePermissionRepository rolePermissionRepository;

    public CaffeinePermissionCacheStore(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    @Cacheable(
            value  = CacheConstants.PERMISSIONS_CACHE,
            key    = "#roleName",
            unless = "#result.isEmpty()"
    )
    public List<String> getPermissions(String roleName) {
        List<String> permissions = rolePermissionRepository
                .findPermissionNamesByRoleName(roleName);

        log.debug(
                "Cache miss — loading permissions for role [{}] → {}",
                roleName,
                permissions   // ← affiche les permissions trouvées
        );

        return permissions;
    }


    @Override
    @CacheEvict(
            value = CacheConstants.PERMISSIONS_CACHE,
            key   = "#roleName"
    )
    public void evict(String roleName) {
        log.debug("Cache evicted for role [{}]", roleName);
    }

    @Override
    @CacheEvict(
            value   = CacheConstants.PERMISSIONS_CACHE,
            allEntries = true
    )
    public void evictAll() {
        log.debug("Cache fully evicted — all permissions cleared");
    }
}
