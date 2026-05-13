package dev.ks.authlayerarchitecture.exception.auth;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountLockedException extends BusinessException {
    public AccountLockedException() {
        super(
                ErrorCodeConstants.ACCOUNT_LOCKED,
                "Account locked",
                "Too many failed login attempts. Please try again later"
        );
    }
}
