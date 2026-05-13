package dev.ks.authlayerarchitecture.repository;

import dev.ks.authlayerarchitecture.entity.PermissionEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository
        extends JpaRepository<PermissionEntity, UUID> {

    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);

    @NonNull Page<PermissionEntity> findAll(@NonNull Pageable pageable);
}
