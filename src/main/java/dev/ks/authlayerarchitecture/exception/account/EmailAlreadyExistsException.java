package dev.ks.authlayerarchitecture.exception.account;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException() {
        super(
                ErrorCodeConstants.EMAIL_ALREADY_EXISTS,
                "Email already exists",
                "An account with this email address already exists"
        );
    }
}
