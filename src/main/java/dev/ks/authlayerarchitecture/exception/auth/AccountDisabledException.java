package dev.ks.authlayerarchitecture.exception.auth;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountDisabledException extends BusinessException {
    public AccountDisabledException() {
        super(
                ErrorCodeConstants.ACCOUNT_DISABLED,
                "Account disabled",
                "This account has been disabled by an administrator"
        );
    }
}

