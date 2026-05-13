package dev.ks.authlayerarchitecture.mapper;

import dev.ks.authlayerarchitecture.dto.response.admin.RoleResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleSummaryResponse;
import dev.ks.authlayerarchitecture.entity.RoleEntity;

import java.util.List;

public final class RoleMapper {

     private RoleMapper() {
         throw new UnsupportedOperationException("RoleMapper is Utility class");
     }

    public static RoleSummaryResponse toSummary(RoleEntity entity) {
        return new RoleSummaryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isSystemRole()
        );
    }

    public static RoleResponse toDetail(
            RoleEntity entity,
            List<String> permissions
    ) {
        return new RoleResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isSystemRole(),
                permissions,
                entity.getAudit().getCreatedAt(),
                entity.getAudit().getUpdatedAt()
        );
    }
}
