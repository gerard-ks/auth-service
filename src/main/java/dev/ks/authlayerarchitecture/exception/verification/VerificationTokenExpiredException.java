package dev.ks.authlayerarchitecture.exception.verification;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class VerificationTokenExpiredException extends BusinessException {
    public VerificationTokenExpiredException() {
        super(
                ErrorCodeConstants.VERIFICATION_TOKEN_EXPIRED,
                "Verification token expired",
                "The verification token has expired. Request a new one"
        );
    }
}
