CREATE TABLE IF NOT EXISTS accounts (
    id                  UUID         NOT NULL,
    email               VARCHAR(255) NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    email_verified      BOOLEAN      NOT NULL DEFAULT false,
    enabled             BOOLEAN      NOT NULL DEFAULT true,
    anonymized_at       TIMESTAMPTZ  NULL,
    password_changed_at TIMESTAMPTZ  NULL,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_accounts PRIMARY KEY (id)
);