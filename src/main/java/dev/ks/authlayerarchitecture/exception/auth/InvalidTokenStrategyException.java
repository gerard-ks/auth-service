package dev.ks.authlayerarchitecture.exception.auth;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidTokenStrategyException extends BusinessException {
    public InvalidTokenStrategyException() {
        super(
                ErrorCodeConstants.INVALID_TOKEN_STRATEGY,
                "Invalid token strategy",
                "Required header 'X-Token-Strategy' is missing or invalid. " +
                        "Allowed values: COOKIE, BEARER"
        );
    }
}
