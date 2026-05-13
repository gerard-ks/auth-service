package dev.ks.authlayerarchitecture.exception.account;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountAlreadyDisabledException extends BusinessException {
    public AccountAlreadyDisabledException() {
        super(
                ErrorCodeConstants.ACCOUNT_ALREADY_DISABLED,
                "Account already disabled",
                "This account is already disabled"
        );
    }
}