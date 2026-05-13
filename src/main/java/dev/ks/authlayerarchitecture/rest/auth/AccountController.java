package dev.ks.authlayerarchitecture.rest.auth;

import dev.ks.authlayerarchitecture.dto.request.account.UpdateAccountRequest;
import dev.ks.authlayerarchitecture.dto.response.auth.AccountResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;
import dev.ks.authlayerarchitecture.security.principal.CurrentAccount;
import dev.ks.authlayerarchitecture.service.account.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getProfile(
            @CurrentAccount AccountPrincipal principal
    ) {
        return ResponseEntity.ok(
                accountService.getProfile(principal)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<AccountResponse> updateProfile(
            @CurrentAccount AccountPrincipal principal,
            @Valid @RequestBody UpdateAccountRequest request
    ) {
        return ResponseEntity.ok(
                accountService.updateProfile(principal, request)
        );
    }

    @PatchMapping("/me/anonymize")
    public ResponseEntity<Void> anonymize(
            @CurrentAccount AccountPrincipal principal
    ) {
        accountService.anonymize(principal);
        return ResponseEntity.ok().build();
    }
}
