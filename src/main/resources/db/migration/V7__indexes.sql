
-- UNIQUE index sur email en lowercase
CREATE UNIQUE INDEX uq_accounts_email_lower
    ON accounts (LOWER(email));

-- INDEX sur anonymized_at
-- filtrer les comptes non anonymisés
CREATE INDEX idx_accounts_anonymized_at
    ON accounts (anonymized_at)
    WHERE anonymized_at IS NOT NULL;

-- INDEX sur permission_id dans role_permissions
-- jointures permissions → rôles
CREATE INDEX idx_role_permissions_permission_id
    ON role_permissions (permission_id);

-- INDEX sur role_id dans account_roles
-- vérifier si un rôle est encore assigné (R18)
CREATE INDEX idx_account_roles_role_id
    ON account_roles (role_id);

-- INDEX sur account_id dans account_roles
-- chargement des rôles d'un compte
CREATE INDEX idx_account_roles_account_id
    ON account_roles (account_id);