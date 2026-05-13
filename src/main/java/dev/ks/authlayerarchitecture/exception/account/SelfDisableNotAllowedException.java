package dev.ks.authlayerarchitecture.exception.account;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class SelfDisableNotAllowedException extends BusinessException {
    public SelfDisableNotAllowedException() {
        super(
                ErrorCodeConstants.SELF_DISABLE_NOT_ALLOWED,
                "Self disable not allowed",
                "Administrators cannot disable their own account"
        );
    }
}