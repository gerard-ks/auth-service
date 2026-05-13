package dev.ks.authlayerarchitecture.exception.permission;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class PermissionNotAssignedException extends BusinessException {

    public PermissionNotAssignedException() {
        super(
                ErrorCodeConstants.PERMISSION_NOT_ASSIGNED,
                "Permission not assigned",
                "This permission is not assigned to the role"
        );
    }
}
