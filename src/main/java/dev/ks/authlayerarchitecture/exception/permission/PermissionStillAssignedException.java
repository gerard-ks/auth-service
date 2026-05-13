package dev.ks.authlayerarchitecture.exception.permission;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class PermissionStillAssignedException extends BusinessException {
    public PermissionStillAssignedException() {
        super(
                ErrorCodeConstants.PERMISSION_STILL_ASSIGNED,
                "Permission still assigned",
                "Cannot delete permission because it is still assigned to roles"
        );
    }
}
