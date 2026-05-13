package dev.ks.authlayerarchitecture.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "role_permissions")
@IdClass(RolePermissionEntity.RolePermissionId.class)
public class RolePermissionEntity {

    @Id
    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private UUID permissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    private PermissionEntity permission;


    public static class RolePermissionId implements Serializable {
        private UUID roleId;
        private UUID permissionId;

        public RolePermissionId() {}

        public RolePermissionId(UUID roleId, UUID permissionId) {
            this.roleId       = roleId;
            this.permissionId = permissionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RolePermissionId that)) return false;
            return java.util.Objects.equals(roleId, that.roleId)
                    && java.util.Objects.equals(permissionId, that.permissionId);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(roleId, permissionId);
        }
    }
}

