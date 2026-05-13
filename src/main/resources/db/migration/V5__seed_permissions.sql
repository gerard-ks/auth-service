INSERT INTO permissions (
    id,
    name,
    description,
    system_perm,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
  gen_random_uuid(),
  'accounts:read',
  'Read account information',
  true,
  NOW(), NULL, NOW(), NULL
),
(
  gen_random_uuid(),
  'accounts:disable',
  'Disable an account',
  true,
  NOW(), NULL, NOW(), NULL
),
(
  gen_random_uuid(),
  'accounts:enable',
  'Enable an account',
  true,
  NOW(), NULL, NOW(), NULL
),
(
  gen_random_uuid(),
  'roles:manage',
  'Create, update and delete roles',
  true,
  NOW(), NULL, NOW(), NULL
),
(
  gen_random_uuid(),
  'roles:assign',
  'Assign and revoke roles on accounts',
  true,
  NOW(), NULL, NOW(), NULL
),
(
  gen_random_uuid(),
  'permissions:manage',
  'Create, update and delete permissions',
  true,
  NOW(), NULL, NOW(), NULL
);