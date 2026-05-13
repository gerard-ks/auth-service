package dev.ks.authlayerarchitecture.rest.admin;

import dev.ks.authlayerarchitecture.dto.response.admin.AccountDetailResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.AccountSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.pagination.PageResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.security.principal.CurrentAccount;
import dev.ks.authlayerarchitecture.service.admin.AdminAccountService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    public AdminAccountController(AdminAccountService adminAccountService) {
        this.adminAccountService = adminAccountService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<AccountSummaryResponse>> findAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                adminAccountService.findAll(email, enabled, verified, pageable)
        );
    }



    @GetMapping("/{id}")
    public ResponseEntity<AccountDetailResponse> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                adminAccountService.findById(id)
        );
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('accounts:disable')")
    public ResponseEntity<Void> disable(
            @PathVariable UUID id,
            @CurrentAccount AccountPrincipal admin
    ) {
        adminAccountService.disable(id, admin);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('accounts:enable')")
    public ResponseEntity<Void> enable(@PathVariable UUID id) {
        adminAccountService.enable(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasAuthority('roles:assign')")
    public ResponseEntity<Void> assignRole(
            @PathVariable UUID id,
            @PathVariable UUID roleId,
            @CurrentAccount AccountPrincipal admin
    ) {
        adminAccountService.assignRole(id, roleId, admin);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @PreAuthorize("hasAuthority('roles:assign')")
    public ResponseEntity<Void> revokeRole(
            @PathVariable UUID id,
            @PathVariable UUID roleId
    ) {
        adminAccountService.revokeRole(id, roleId);
        return ResponseEntity.ok().build();
    }
}

