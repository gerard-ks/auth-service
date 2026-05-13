package dev.ks.authlayerarchitecture.mapper;

import dev.ks.authlayerarchitecture.dto.response.admin.PermissionResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionSummaryResponse;
import dev.ks.authlayerarchitecture.entity.PermissionEntity;

public final class PermissionMapper {

    private PermissionMapper() {
        throw new UnsupportedOperationException("PermissionMapper is Utility class");
    }

    public static PermissionSummaryResponse toSummary(
            PermissionEntity entity
    ) {
        return new PermissionSummaryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isSystemPerm()
        );
    }

    public static PermissionResponse toDetail(PermissionEntity entity) {
        return new PermissionResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isSystemPerm(),
                entity.getAudit().getCreatedAt(),
                entity.getAudit().getUpdatedAt()
        );
    }
}
