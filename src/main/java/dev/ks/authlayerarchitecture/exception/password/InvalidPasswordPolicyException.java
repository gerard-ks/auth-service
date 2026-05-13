package dev.ks.authlayerarchitecture.exception.password;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class InvalidPasswordPolicyException extends BusinessException {
    public InvalidPasswordPolicyException() {
        super(
                ErrorCodeConstants.INVALID_PASSWORD_POLICY,
                "Invalid password policy",
                "Password does not meet security requirements"
        );
    }
}
