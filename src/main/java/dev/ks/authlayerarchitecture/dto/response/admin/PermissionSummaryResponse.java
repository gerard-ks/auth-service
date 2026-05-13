package dev.ks.authlayerarchitecture.dto.response.admin;

import java.util.UUID;

public record PermissionSummaryResponse(
        UUID id,
        String  name,
        String  description,
        boolean systemPerm
) {}
