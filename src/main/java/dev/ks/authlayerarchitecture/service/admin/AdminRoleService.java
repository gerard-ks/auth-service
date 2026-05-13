package dev.ks.authlayerarchitecture.service.admin;

import dev.ks.authlayerarchitecture.dto.request.admin.CreateRoleRequest;
import dev.ks.authlayerarchitecture.dto.request.admin.UpdateRoleRequest;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminRoleService {

    PageResponse<RoleSummaryResponse> findAll(Pageable pageable);

    RoleResponse findById(UUID roleId);

    RoleResponse create(
            CreateRoleRequest request,
            AccountPrincipal admin
    );

    RoleResponse update(
            UUID roleId,
            UpdateRoleRequest request,
            AccountPrincipal admin
    );

    void delete(UUID roleId);

    void addPermission(UUID roleId, UUID permissionId);

    void removePermission(UUID roleId, UUID permissionId);
}
