package dev.ks.authlayerarchitecture.exception.permission;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class SystemPermDeletionNotAllowedException extends BusinessException {
    public SystemPermDeletionNotAllowedException() {
        super(
                ErrorCodeConstants.SYSTEM_PERM_DELETION_NOT_ALLOWED,
                "System permission deletion not allowed",
                "System permissions cannot be deleted"
        );
    }
}
