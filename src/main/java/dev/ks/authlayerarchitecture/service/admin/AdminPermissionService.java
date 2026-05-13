package dev.ks.authlayerarchitecture.service.admin;

import dev.ks.authlayerarchitecture.dto.request.admin.CreatePermissionRequest;
import dev.ks.authlayerarchitecture.dto.request.admin.UpdatePermissionRequest;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminPermissionService {

    PageResponse<PermissionSummaryResponse> findAll(Pageable pageable);

    PermissionResponse findById(UUID permissionId);

    PermissionResponse create(
            CreatePermissionRequest request,
            AccountPrincipal admin
    );

    PermissionResponse update(
            UUID permissionId,
            UpdatePermissionRequest request,
            AccountPrincipal admin
    );

    void delete(UUID permissionId);
}
