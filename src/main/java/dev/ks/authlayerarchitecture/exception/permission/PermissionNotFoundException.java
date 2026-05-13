package dev.ks.authlayerarchitecture.exception.permission;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class PermissionNotFoundException extends BusinessException {
    public PermissionNotFoundException() {
        super(
                ErrorCodeConstants.PERMISSION_NOT_FOUND,
                "Permission not found",
                "No permission exists with the provided identifier"
        );
    }
}
