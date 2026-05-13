package dev.ks.authlayerarchitecture.rest.admin;

import dev.ks.authlayerarchitecture.dto.request.admin.CreateRoleRequest;
import dev.ks.authlayerarchitecture.dto.request.admin.UpdateRoleRequest;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.RoleSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.security.principal.CurrentAccount;
import dev.ks.authlayerarchitecture.service.admin.AdminRoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    public AdminRoleController(AdminRoleService adminRoleService) {
        this.adminRoleService = adminRoleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('roles:manage')")
    public ResponseEntity<PageResponse<RoleSummaryResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return ResponseEntity.ok(
                adminRoleService.findAll(pageable)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('roles:manage')")
    public ResponseEntity<RoleResponse> create(
            @Valid @RequestBody CreateRoleRequest request,
            @CurrentAccount AccountPrincipal admin
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminRoleService.create(request, admin));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:manage')")
    public ResponseEntity<RoleResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(adminRoleService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:manage')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request,
            @CurrentAccount AccountPrincipal admin
    ) {
        return ResponseEntity.ok(
                adminRoleService.update(id, request, admin)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('roles:manage')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminRoleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions/{permId}")
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<Void> addPermission(
            @PathVariable UUID id,
            @PathVariable UUID permId
    ) {
        adminRoleService.addPermission(id, permId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/permissions/{permId}")
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<Void> removePermission(
            @PathVariable UUID id,
            @PathVariable UUID permId
    ) {
        adminRoleService.removePermission(id, permId);
        return ResponseEntity.noContent().build();
    }
}
