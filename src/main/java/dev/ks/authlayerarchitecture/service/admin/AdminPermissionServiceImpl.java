package dev.ks.authlayerarchitecture.service.admin;

import dev.ks.authlayerarchitecture.cache.PermissionCacheStore;
import dev.ks.authlayerarchitecture.dto.request.admin.CreatePermissionRequest;
import dev.ks.authlayerarchitecture.dto.request.admin.UpdatePermissionRequest;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.entity.PermissionEntity;
import dev.ks.authlayerarchitecture.entity.embedded.AuditMetaEntity;
import dev.ks.authlayerarchitecture.exception.permission.*;
import dev.ks.authlayerarchitecture.mapper.PageResponseMapper;
import dev.ks.authlayerarchitecture.mapper.PermissionMapper;
import dev.ks.authlayerarchitecture.repository.PermissionRepository;
import dev.ks.authlayerarchitecture.repository.RolePermissionRepository;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionCacheStore permissionCacheStore;

    public AdminPermissionServiceImpl(PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, PermissionCacheStore permissionCacheStore) {
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionCacheStore = permissionCacheStore;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PermissionSummaryResponse> findAll(
            Pageable pageable
    ) {
        return PageResponseMapper.from(
                permissionRepository.findAll(pageable)
                        .map(PermissionMapper::toSummary)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse findById(UUID permissionId) {
        return PermissionMapper.toDetail(findPermission(permissionId));
    }

    @Override
    @Transactional
    public PermissionResponse create(
            CreatePermissionRequest request,
            AccountPrincipal admin
    ) {
        if (permissionRepository.existsByName(request.name())) {
            throw new PermissionAlreadyExistsException();
        }

        AuditMetaEntity audit = new AuditMetaEntity();
        audit.setCreatedAt(Instant.now());
        audit.setCreatedBy(admin.accountId());
        audit.setUpdatedAt(Instant.now());
        audit.setUpdatedBy(admin.accountId());

        PermissionEntity permission = new PermissionEntity();
        permission.setId(UUID.randomUUID());
        permission.setName(request.name().toLowerCase());
        permission.setDescription(request.description());
        permission.setSystemPerm(false);
        permission.setAudit(audit);

        permissionRepository.save(permission);

        log.info(
                "Permission created [name={}] by [adminId={}]",
                permission.getName(), admin.accountId()
        );

        return PermissionMapper.toDetail(permission);
    }

    @Override
    @Transactional
    public PermissionResponse update(
            UUID permissionId,
            UpdatePermissionRequest request,
            AccountPrincipal admin
    ) {
        PermissionEntity permission = findPermission(permissionId);

        if (permissionRepository.existsByName(request.name())
                && !permission.getName().equals(request.name())) {
            throw new PermissionAlreadyExistsException();
        }

        permission.setName(request.name().toLowerCase());
        permission.setDescription(request.description());
        permission.getAudit().setUpdatedAt(Instant.now());
        permission.getAudit().setUpdatedBy(admin.accountId());

        permissionRepository.save(permission);

        // Invalider tout le cache (toutes les permissions changées)
        permissionCacheStore.evictAll();

        log.info("Permission updated [permissionId={}]", permissionId);

        return PermissionMapper.toDetail(permission);
    }

    @Override
    @Transactional
    public void delete(UUID permissionId) {
        PermissionEntity permission = findPermission(permissionId);

        // système non supprimable
        if (permission.isSystemPerm()) {
            throw new SystemPermDeletionNotAllowedException();
        }

        // Vérifier si encore assignée
        if (rolePermissionRepository.existsByPermissionId(permissionId)) {
            throw new PermissionStillAssignedException();
        }

        permissionRepository.delete(permission);

        // Invalider tout le cache
        permissionCacheStore.evictAll();

        log.info("Permission deleted [permissionId={}]", permissionId);
    }

    private PermissionEntity findPermission(UUID permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(PermissionNotFoundException::new);
    }
}
