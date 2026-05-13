package dev.ks.authlayerarchitecture.exception.token;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidAccessTokenException extends BusinessException {
    public InvalidAccessTokenException() {
        super(
                ErrorCodeConstants.INVALID_ACCESS_TOKEN,
                "Invalid access token",
                "The provided access token is invalid"
        );
    }
}
