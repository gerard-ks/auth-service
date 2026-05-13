package dev.ks.authlayerarchitecture.repository;

import dev.ks.authlayerarchitecture.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermissionEntity,
                RolePermissionEntity.RolePermissionId> {


    List<RolePermissionEntity> findByRoleId(UUID roleId);

    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    Optional<RolePermissionEntity> findByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId
    );

    boolean existsByPermissionId(UUID permissionId);

    @Query("""
        SELECT p.name
        FROM RolePermissionEntity rp
        JOIN PermissionEntity p ON p.id = rp.permissionId
        JOIN RoleEntity r ON r.id = rp.roleId
        WHERE r.name = :roleName
        """)
    List<String> findPermissionNamesByRoleName(
            @Param("roleName") String roleName
    );
}
