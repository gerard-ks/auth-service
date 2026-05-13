package dev.ks.authlayerarchitecture.exception.role;

import dev.ks.authlayerarchitecture.constant.ErrorCodeConstants;
import dev.ks.authlayerarchitecture.exception.BusinessException;

public class RoleAlreadyExistsException extends BusinessException {
    public RoleAlreadyExistsException() {
        super(
                ErrorCodeConstants.ROLE_ALREADY_EXISTS,
                "Role already exists",
                "A role with this name already exists"
        );
    }
}
