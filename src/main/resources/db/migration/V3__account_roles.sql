CREATE TABLE IF NOT EXISTS account_roles (
    account_id  UUID        NOT NULL,
    role_id     UUID        NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL,
    assigned_by UUID        NULL,
    source      VARCHAR(50) NOT NULL,

    CONSTRAINT pk_account_roles
       PRIMARY KEY (account_id, role_id),

    CONSTRAINT fk_account_roles_account
       FOREIGN KEY (account_id)
           REFERENCES accounts (id)
           ON DELETE CASCADE,

    CONSTRAINT fk_account_roles_role
       FOREIGN KEY (role_id)
           REFERENCES roles (id)
           ON DELETE RESTRICT,

    CONSTRAINT fk_account_roles_assigned_by
       FOREIGN KEY (assigned_by)
           REFERENCES accounts (id)
           ON DELETE SET NULL,

    CONSTRAINT ck_account_roles_source
       CHECK (source IN ('REGISTRATION', 'ADMIN_MANUAL'))
);