package dev.ks.authlayerarchitecture.exception.permission;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class PermissionAlreadyExistsException extends BusinessException {
    public PermissionAlreadyExistsException() {
        super(
                ErrorCodeConstants.PERMISSION_ALREADY_EXISTS,
                "Permission already exists",
                "A permission with this name already exists"
        );
    }
}
