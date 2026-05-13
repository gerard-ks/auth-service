package dev.ks.authlayerarchitecture.exception.permission;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class PermissionAlreadyAssignedException extends BusinessException {
    public PermissionAlreadyAssignedException() {
        super(
                ErrorCodeConstants.PERMISSION_ALREADY_ASSIGNED,
                "Permission already assigned",
                "This permission is already assigned to the role"
        );
    }
}
