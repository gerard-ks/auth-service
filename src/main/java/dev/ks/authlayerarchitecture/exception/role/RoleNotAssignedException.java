package dev.ks.authlayerarchitecture.exception.role;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class RoleNotAssignedException extends BusinessException {
    public RoleNotAssignedException() {
        super(
                ErrorCodeConstants.ROLE_NOT_ASSIGNED,
                "Role not assigned",
                "This role is not assigned to the account"
        );
    }
}
