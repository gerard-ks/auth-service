package dev.ks.authlayerarchitecture.security.principal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component("currentAccountProvider")
public class CurrentAccountProvider {

    public AccountPrincipal resolve(Jwt jwt) {
        UUID accountId = UUID.fromString(jwt.getSubject());
        String email   = jwt.getClaimAsString("email");
        List<String> roles = jwt.getClaimAsStringList("roles");

        return new AccountPrincipal(
                accountId,
                email,
                roles != null ? roles : List.of()
        );
    }
}
