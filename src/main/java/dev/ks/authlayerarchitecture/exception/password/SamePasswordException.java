package dev.ks.authlayerarchitecture.exception.password;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class SamePasswordException extends BusinessException {
    public SamePasswordException() {
        super(
                ErrorCodeConstants.SAME_PASSWORD,
                "Same password",
                "New password cannot be the same as the current one"
        );
    }
}
