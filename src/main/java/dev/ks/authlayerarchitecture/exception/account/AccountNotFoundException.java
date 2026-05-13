package dev.ks.authlayerarchitecture.exception.account;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(
                ErrorCodeConstants.ACCOUNT_NOT_FOUND,
                "Account not found",
                "No account exists with the provided identifier"
        );
    }
}
