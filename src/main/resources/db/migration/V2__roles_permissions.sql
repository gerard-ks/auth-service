CREATE TABLE IF NOT EXISTS roles (
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    system_role BOOLEAN      NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ  NOT NULL,
    created_by  UUID         NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    updated_by  UUID         NULL,

    CONSTRAINT pk_roles      PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS permissions (
    id          UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL,
    system_perm BOOLEAN      NOT NULL DEFAULT false,
    created_at  TIMESTAMPTZ  NOT NULL,
    created_by  UUID         NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    updated_by  UUID         NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (id),
    CONSTRAINT uq_permissions_name UNIQUE (name),
    CONSTRAINT ck_permissions_format CHECK (name LIKE '%:%')
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       UUID NOT NULL,
    permission_id UUID NOT NULL,

    CONSTRAINT pk_role_permissions
      PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
      FOREIGN KEY (role_id)
          REFERENCES roles (id)
          ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
      FOREIGN KEY (permission_id)
          REFERENCES permissions (id)
          ON DELETE CASCADE
);