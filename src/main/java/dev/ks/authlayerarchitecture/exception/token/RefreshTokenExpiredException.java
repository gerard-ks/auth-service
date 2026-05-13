package dev.ks.authlayerarchitecture.exception.token;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class RefreshTokenExpiredException extends BusinessException {
    public RefreshTokenExpiredException() {
        super(
                ErrorCodeConstants.REFRESH_TOKEN_EXPIRED,
                "Refresh token expired",
                "The refresh token has expired. Please log in again"
        );
    }
}
