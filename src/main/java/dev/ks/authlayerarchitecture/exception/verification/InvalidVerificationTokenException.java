package dev.ks.authlayerarchitecture.exception.verification;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidVerificationTokenException extends BusinessException {
    public InvalidVerificationTokenException() {
        super(
                ErrorCodeConstants.INVALID_VERIFICATION_TOKEN,
                "Invalid verification token",
                "The provided verification token is invalid"
        );
    }
}
