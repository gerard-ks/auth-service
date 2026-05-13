package dev.ks.authlayerarchitecture.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "account_roles")
@IdClass(AccountRoleEntity.AccountRoleId.class)
public class AccountRoleEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Id
    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleEntity role;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "source", nullable = false)
    private String source;

    public static class AccountRoleId implements Serializable {
        private UUID accountId;
        private UUID roleId;

        public AccountRoleId() {}

        public AccountRoleId(UUID accountId, UUID roleId) {
            this.accountId = accountId;
            this.roleId    = roleId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AccountRoleId that)) return false;
            return java.util.Objects.equals(accountId, that.accountId)
                    && java.util.Objects.equals(roleId, that.roleId);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(accountId, roleId);
        }
    }
}
