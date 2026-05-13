package dev.ks.authlayerarchitecture.security.jwt;

import dev.ks.authlayerarchitecture.cache.PermissionCacheStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class JwtToAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final PermissionCacheStore permissionCacheStore;

    public JwtToAuthenticationConverter(PermissionCacheStore permissionCacheStore) {
        this.permissionCacheStore = permissionCacheStore;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (roles != null) {
            roles.forEach(role -> {

                // ROLE_X pour @PreAuthorize("hasRole('ADMIN')")
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                // Permissions depuis Caffeine
                List<String> permissions =
                        permissionCacheStore.getPermissions(role);

                permissions.forEach(permission ->
                        authorities.add(
                                new SimpleGrantedAuthority(permission)
                        )
                );
            });
        }

        log.debug(
                "Authorities resolved : {}",
                authorities
        );

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
