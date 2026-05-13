package dev.ks.authlayerarchitecture.service.admin;

import dev.ks.authlayerarchitecture.dto.response.admin.AccountDetailResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.AccountSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminAccountService {
    PageResponse<AccountSummaryResponse> findAll(
            String email,
            Boolean enabled,
            Boolean verified,
            Pageable pageable
    );

    AccountDetailResponse findById(UUID accountId);

    void disable(UUID accountId, AccountPrincipal admin);

    void enable(UUID accountId);

    void assignRole(UUID accountId, UUID roleId, AccountPrincipal admin);

    void revokeRole(UUID accountId, UUID roleId);
}
