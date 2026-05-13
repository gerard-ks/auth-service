package dev.ks.authlayerarchitecture.exception.password;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidCurrentPasswordException extends BusinessException {
    public InvalidCurrentPasswordException() {
        super(
                ErrorCodeConstants.INVALID_CURRENT_PASSWORD,
                "Invalid current password",
                "The current password provided is incorrect"
        );
    }
}
