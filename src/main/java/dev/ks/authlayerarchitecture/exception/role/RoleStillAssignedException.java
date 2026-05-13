package dev.ks.authlayerarchitecture.exception.role;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class RoleStillAssignedException extends BusinessException {
    public RoleStillAssignedException() {
        super(
                ErrorCodeConstants.ROLE_STILL_ASSIGNED,
                "Role still assigned",
                "Cannot delete role because it is still assigned to accounts"
        );
    }
}
