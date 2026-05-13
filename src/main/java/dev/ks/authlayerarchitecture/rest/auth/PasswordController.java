package dev.ks.authlayerarchitecture.rest.auth;

import dev.ks.authlayerarchitecture.dto.request.auth.ChangePasswordRequest;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.security.principal.CurrentAccount;
import dev.ks.authlayerarchitecture.service.password.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class PasswordController {

    private final PasswordResetService passwordResetService;

    public PasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @CurrentAccount AccountPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        passwordResetService.changePassword(
                principal.accountId(),
                request.currentPassword(),
                request.newPassword()
        );
        return ResponseEntity.ok().build();
    }
}
