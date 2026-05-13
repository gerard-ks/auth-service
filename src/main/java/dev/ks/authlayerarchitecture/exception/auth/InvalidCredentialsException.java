package dev.ks.authlayerarchitecture.exception.auth;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(
                ErrorCodeConstants.INVALID_CREDENTIALS,
                "Invalid credentials",
                "The provided email or password is incorrect"
        );
    }
}
