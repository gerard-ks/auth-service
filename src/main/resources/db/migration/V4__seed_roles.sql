INSERT INTO roles (
    id,
    name,
    description,
    system_role,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
  gen_random_uuid(),
  'USER',
  'Default role assigned to every registered account',
  true,
  NOW(),
  NULL,
  NOW(),
  NULL
),
(
  gen_random_uuid(),
  'ADMIN',
  'System administration role',
  true,
  NOW(),
  NULL,
  NOW(),
  NULL
);