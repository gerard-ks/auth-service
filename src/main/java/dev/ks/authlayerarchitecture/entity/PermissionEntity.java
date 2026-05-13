package dev.ks.authlayerarchitecture.entity;

import dev.ks.authlayerarchitecture.entity.embedded.AuditMetaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class PermissionEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "system_perm", nullable = false)
    private boolean systemPerm;

    @Embedded
    private AuditMetaEntity audit;
}
