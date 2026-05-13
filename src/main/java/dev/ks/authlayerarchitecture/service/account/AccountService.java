package dev.ks.authlayerarchitecture.service.account;

import dev.ks.authlayerarchitecture.dto.request.account.UpdateAccountRequest;
import dev.ks.authlayerarchitecture.dto.response.auth.AccountResponse;
import dev.ks.authlayerarchitecture.security.principal.AccountPrincipal;

public interface AccountService {
    AccountResponse getProfile(AccountPrincipal principal);

    AccountResponse updateProfile(
            AccountPrincipal principal,
            UpdateAccountRequest request
    );

    void anonymize(AccountPrincipal principal);
}
