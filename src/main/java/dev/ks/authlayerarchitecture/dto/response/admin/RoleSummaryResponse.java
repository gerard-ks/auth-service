package dev.ks.authlayerarchitecture.dto.response.admin;

import java.util.UUID;

public record RoleSummaryResponse(
        UUID id,
        String  name,
        String  description,
        boolean systemRole
) {}
