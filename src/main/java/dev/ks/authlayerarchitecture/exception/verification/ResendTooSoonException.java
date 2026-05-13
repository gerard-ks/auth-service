package dev.ks.authlayerarchitecture.exception.verification;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class ResendTooSoonException extends BusinessException {
    public ResendTooSoonException() {
        super(
                ErrorCodeConstants.RESEND_TOO_SOON,
                "Resend too soon",
                "You must wait before requesting a new verification email"
        );
    }
}
