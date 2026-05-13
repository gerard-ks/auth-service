package dev.ks.authlayerarchitecture.exception.token;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super(
                ErrorCodeConstants.INVALID_REFRESH_TOKEN,
                "Invalid refresh token",
                "The provided refresh token is invalid or revoked"
        );
    }
}
