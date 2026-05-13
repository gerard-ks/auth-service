package dev.ks.authlayerarchitecture.exception.token;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class AccessTokenExpiredException extends BusinessException {
    public AccessTokenExpiredException() {
        super(
                ErrorCodeConstants.ACCESS_TOKEN_EXPIRED,
                "Access token expired",
                "The access token has expired. Please refresh it"
        );
    }
}
