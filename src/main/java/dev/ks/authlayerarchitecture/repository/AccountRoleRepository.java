package dev.ks.authlayerarchitecture.repository;

import dev.ks.authlayerarchitecture.entity.AccountRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRoleRepository
        extends JpaRepository<AccountRoleEntity,
                AccountRoleEntity.AccountRoleId> {

    List<AccountRoleEntity> findByAccountId(UUID accountId);

    boolean existsByAccountIdAndRoleId(UUID accountId, UUID roleId);

    Optional<AccountRoleEntity> findByAccountIdAndRoleId(
            UUID accountId,
            UUID roleId
    );

    boolean existsByRoleId(UUID roleId);

    @Query("""
        SELECT r.name
        FROM AccountRoleEntity ar
        JOIN RoleEntity r ON r.id = ar.roleId
        WHERE ar.accountId = :accountId
        """)
    List<String> findRoleNamesByAccountId(
            @Param("accountId") UUID accountId
    );
}
