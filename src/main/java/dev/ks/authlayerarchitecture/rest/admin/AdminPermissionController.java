package dev.ks.authlayerarchitecture.rest.admin;

import dev.ks.authlayerarchitecture.dto.request.admin.CreatePermissionRequest;
import dev.ks.authlayerarchitecture.dto.request.admin.UpdatePermissionRequest;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.PermissionSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.security.principal.CurrentAccount;
import dev.ks.authlayerarchitecture.service.admin.AdminPermissionService;
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
@RequestMapping("/api/admin/permissions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    public AdminPermissionController(AdminPermissionService adminPermissionService) {
        this.adminPermissionService = adminPermissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<PageResponse<PermissionSummaryResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return ResponseEntity.ok(
                adminPermissionService.findAll(pageable)
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<PermissionResponse> create(
            @Valid @RequestBody CreatePermissionRequest request,
            @CurrentAccount AccountPrincipal admin
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminPermissionService.create(request, admin));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<PermissionResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                adminPermissionService.findById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<PermissionResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePermissionRequest request,
            @CurrentAccount AccountPrincipal admin
    ) {
        return ResponseEntity.ok(
                adminPermissionService.update(id, request, admin)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permissions:manage')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
