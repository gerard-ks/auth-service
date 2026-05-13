package dev.ks.authlayerarchitecture.exception.verification;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class OtpExpiredException extends BusinessException {
    public OtpExpiredException() {
        super(
                ErrorCodeConstants.OTP_EXPIRED,
                "OTP expired",
                "The OTP code has expired. Request a new one"
        );
    }
}
