package dev.ks.authlayerarchitecture.exception.verification;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class OtpBlockedException extends BusinessException {
    public OtpBlockedException() {
        super(
                ErrorCodeConstants.OTP_BLOCKED,
                "OTP blocked",
                "Too many failed OTP attempts. Please try again later"
        );
    }
}
