package dev.ks.authlayerarchitecture.exception.account;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccountNotActiveException extends BusinessException {
    public AccountNotActiveException() {
        super(
                ErrorCodeConstants.ACCOUNT_NOT_ACTIVE,
                "Account not active",
                "This account cannot be disabled because it is not active"
        );
    }
}
