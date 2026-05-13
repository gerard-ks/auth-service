package dev.ks.authlayerarchitecture.exception.auth;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountNotVerifiedException extends BusinessException {
    public AccountNotVerifiedException() {
        super(
                ErrorCodeConstants.ACCOUNT_NOT_VERIFIED,
                "Account not verified",
                "Email verification is required before login"
        );
    }
}
