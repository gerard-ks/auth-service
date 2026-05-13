package dev.ks.authlayerarchitecture.exception.verification;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidOtpException extends BusinessException {
    public InvalidOtpException() {
        super(
                ErrorCodeConstants.INVALID_OTP,
                "Invalid OTP",
                "The provided OTP code is incorrect"
        );
    }
}
