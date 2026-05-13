package dev.ks.authlayerarchitecture.mapper;

import dev.ks.authlayerarchitecture.dto.response.admin.AccountDetailResponse;
import dev.ks.authlayerarchitecture.dto.response.admin.AccountSummaryResponse;
import dev.ks.authlayerarchitecture.dto.response.auth.AccountResponse;
import dev.ks.authlayerarchitecture.entity.AccountEntity;

import java.util.List;

public final class AccountMapper {

    private AccountMapper() {
        throw new UnsupportedOperationException("AccountMapper is Utility class");
    }

    public static AccountResponse toAccountResponse(
            AccountEntity entity,
            List<String> roles
    ) {
        return new AccountResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isEmailVerified(),
                entity.isEnabled(),
                roles,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static AccountSummaryResponse toSummary(AccountEntity entity) {
        return new AccountSummaryResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isEmailVerified(),
                entity.isEnabled(),
                entity.getCreatedAt()
        );
    }

    public static AccountDetailResponse toDetail(
            AccountEntity entity,
            List<String> roles
    ) {
        return new AccountDetailResponse(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isEmailVerified(),
                entity.isEnabled(),
                roles,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPasswordChangedAt(),
                entity.getAnonymizedAt()
        );
    }
}
