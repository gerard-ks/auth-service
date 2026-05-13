package dev.ks.authlayerarchitecture.exception.token;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidResetTokenException extends BusinessException {
    public InvalidResetTokenException() {
        super(
                ErrorCodeConstants.INVALID_RESET_TOKEN,
                "Invalid reset token",
                "The provided password reset token is invalid or does not exist"
        );
    }
}
