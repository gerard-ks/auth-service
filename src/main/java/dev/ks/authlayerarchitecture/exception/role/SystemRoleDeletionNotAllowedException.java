package dev.ks.authlayerarchitecture.exception.role;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class SystemRoleDeletionNotAllowedException extends BusinessException {
    public SystemRoleDeletionNotAllowedException() {
        super(
                ErrorCodeConstants.SYSTEM_ROLE_DELETION_NOT_ALLOWED,
                "System role deletion not allowed",
                "System roles cannot be deleted"
        );
    }
}
