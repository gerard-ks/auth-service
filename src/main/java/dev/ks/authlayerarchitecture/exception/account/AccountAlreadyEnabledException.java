package dev.ks.authlayerarchitecture.exception.account;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountAlreadyEnabledException extends BusinessException {
    public AccountAlreadyEnabledException() {
        super(
                ErrorCodeConstants.ACCOUNT_ALREADY_ENABLED,
                "Account already enabled",
                "This account is already enabled"
        );
    }
}
