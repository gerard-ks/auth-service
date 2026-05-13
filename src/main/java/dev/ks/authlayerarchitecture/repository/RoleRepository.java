package dev.ks.authlayerarchitecture.repository;

import dev.ks.authlayerarchitecture.entity.RoleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository
        extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);

    @NonNull Page<RoleEntity> findAll(@NonNull Pageable pageable);
}
