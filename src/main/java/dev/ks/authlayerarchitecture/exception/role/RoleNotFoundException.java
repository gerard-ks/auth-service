package dev.ks.authlayerarchitecture.exception.role;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class RoleNotFoundException extends BusinessException {
    public RoleNotFoundException() {
        super(
                ErrorCodeConstants.ROLE_NOT_FOUND,
                "Role not found",
                "No role exists with the provided identifier"
        );
    }
}
