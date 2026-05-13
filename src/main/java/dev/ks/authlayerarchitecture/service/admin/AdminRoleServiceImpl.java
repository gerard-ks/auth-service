package dev.ks.authlayerarchitecture.service.admin;

import dev.ks.authlayerarchitecture.cache.PermissionCacheStore;
import dev.ks.authlayerarchitecture.dto.request.admin.CreateRoleRequest;
import dev.ks.authlayerarchitecture.dto.request.admin.UpdateRoleRequest;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.entity.RoleEntity;
import dev.ks.authlayerarchitecture.entity.RolePermissionEntity;
import dev.ks.authlayerarchitecture.entity.embedded.AuditMetaEntity;
import dev.ks.authlayerarchitecture.exception.permission.*;
import dev.ks.authlayerarchitecture.exception.role.*;
import dev.ks.authlayerarchitecture.mapper.PageResponseMapper;
import dev.ks.authlayerarchitecture.mapper.RoleMapper;
import dev.ks.authlayerarchitecture.repository.AccountRoleRepository;
import dev.ks.authlayerarchitecture.repository.PermissionRepository;
import dev.ks.authlayerarchitecture.repository.RolePermissionRepository;
import dev.ks.authlayerarchitecture.repository.RoleRepository;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionCacheStore permissionCacheStore;

    public AdminRoleServiceImpl(RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, AccountRoleRepository accountRoleRepository, PermissionRepository permissionRepository, PermissionCacheStore permissionCacheStore) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.permissionRepository = permissionRepository;
        this.permissionCacheStore = permissionCacheStore;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleSummaryResponse> findAll(Pageable pageable) {
        return PageResponseMapper.from(
                roleRepository.findAll(pageable)
                        .map(RoleMapper::toSummary)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse findById(UUID roleId) {
        RoleEntity role = findRole(roleId);
        List<String> permissions = rolePermissionRepository
                .findPermissionNamesByRoleName(role.getName());
        return RoleMapper.toDetail(role, permissions);
    }

    @Override
    @Transactional
    public RoleResponse create(
            CreateRoleRequest request,
            AccountPrincipal admin
    ) {
        if (roleRepository.existsByName(request.name())) {
            throw new RoleAlreadyExistsException();
        }

        AuditMetaEntity audit = new AuditMetaEntity();
        audit.setCreatedAt(Instant.now());
        audit.setCreatedBy(admin.accountId());
        audit.setUpdatedAt(Instant.now());
        audit.setUpdatedBy(admin.accountId());

        RoleEntity role = new RoleEntity();
        role.setId(UUID.randomUUID());
        role.setName(request.name().toUpperCase());
        role.setDescription(request.description());
        role.setSystemRole(false);
        role.setAudit(audit);

        roleRepository.save(role);

        log.info(
                "Role created [name={}] by [adminId={}]",
                role.getName(), admin.accountId()
        );

        return RoleMapper.toDetail(role, List.of());
    }

    @Override
    @Transactional
    public RoleResponse update(
            UUID roleId,
            UpdateRoleRequest request,
            AccountPrincipal admin
    ) {
        RoleEntity role = findRole(roleId);

        if (roleRepository.existsByName(request.name())
                && !role.getName().equals(request.name())) {
            throw new RoleAlreadyExistsException();
        }

        String oldName = role.getName();
        role.setName(request.name().toUpperCase());
        role.setDescription(request.description());
        role.getAudit().setUpdatedAt(Instant.now());
        role.getAudit().setUpdatedBy(admin.accountId());

        roleRepository.save(role);

        // Invalider le cache
        permissionCacheStore.evict(oldName);

        log.info("Role updated [roleId={}]", roleId);

        List<String> permissions = rolePermissionRepository
                .findPermissionNamesByRoleName(role.getName());

        return RoleMapper.toDetail(role, permissions);
    }

    @Override
    @Transactional
    public void delete(UUID roleId) {
        RoleEntity role = findRole(roleId);

        // système non supprimable
        if (role.isSystemRole()) {
            throw new SystemRoleDeletionNotAllowedException();
        }

        // refusée si assigné
        if (accountRoleRepository.existsByRoleId(roleId)) {
            throw new RoleStillAssignedException();
        }

        roleRepository.delete(role);

        // Invalider le cache
        permissionCacheStore.evict(role.getName());

        log.info("Role deleted [roleId={}]", roleId);
    }

    @Override
    @Transactional
    public void addPermission(UUID roleId, UUID permissionId) {
        RoleEntity role = findRole(roleId);

        permissionRepository.findById(permissionId)
                .orElseThrow(PermissionNotFoundException::new);

        if (rolePermissionRepository.existsByRoleIdAndPermissionId(
                roleId, permissionId
        )) {
            throw new PermissionAlreadyAssignedException();
        }

        RolePermissionEntity rp = new RolePermissionEntity();
        rp.setRoleId(roleId);
        rp.setPermissionId(permissionId);
        rolePermissionRepository.save(rp);

        // Invalider le cache
        permissionCacheStore.evict(role.getName());

        log.info(
                "Permission [{}] added to role [{}]",
                permissionId, roleId
        );
    }

    @Override
    @Transactional
    public void removePermission(UUID roleId, UUID permissionId) {
        RoleEntity role = findRole(roleId);

        RolePermissionEntity rp = rolePermissionRepository
                .findByRoleIdAndPermissionId(roleId, permissionId)
                .orElseThrow(PermissionNotAssignedException::new);

        rolePermissionRepository.delete(rp);

        // Invalider le cache
        permissionCacheStore.evict(role.getName());

        log.info(
                "Permission [{}] removed from role [{}]",
                permissionId, roleId
        );
    }

    private RoleEntity findRole(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(RoleNotFoundException::new);
    }
}
