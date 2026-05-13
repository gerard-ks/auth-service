package dev.ks.authlayerarchitecture.repository;

import dev.ks.authlayerarchitecture.entity.AccountEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository
        extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    @Query(value = """
        SELECT a FROM AccountEntity a
        WHERE (:email = '' OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:enabled IS NULL OR a.enabled = :enabled)
          AND (:verified IS NULL OR a.emailVerified = :verified)
          AND a.anonymizedAt IS NULL
        """,
            countQuery = """
        SELECT COUNT(a) FROM AccountEntity a
        WHERE (:email = '' OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:enabled IS NULL OR a.enabled = :enabled)
          AND (:verified IS NULL OR a.emailVerified = :verified)
          AND a.anonymizedAt IS NULL
        """)
    Page<AccountEntity> findAllWithFilters(
            @Param("email")   String email,
            @Param("enabled") Boolean enabled,
            @Param("verified")Boolean verified,
           Pageable pageable
    );
}
