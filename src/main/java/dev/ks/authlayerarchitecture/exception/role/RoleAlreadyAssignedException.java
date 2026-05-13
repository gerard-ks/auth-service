package dev.ks.authlayerarchitecture.exception.role;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class RoleAlreadyAssignedException extends BusinessException {
    public RoleAlreadyAssignedException() {
        super(
                ErrorCodeConstants.ROLE_ALREADY_ASSIGNED,
                "Role already assigned",
                "This role is already assigned to the account"
        );
    }
}
