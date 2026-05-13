package dev.ks.authlayerarchitecture.security.principal;

import java.util.List;
import java.util.UUID;

public record AccountPrincipal(
        UUID accountId,
        String email,
        List<String> roles
) {}
