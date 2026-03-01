-- liquibase formatted sql

-- changeset denismalinin:1761945529000-1
CREATE INDEX idx_users_email ON users(email);

-- changeset denismalinin:1761945529000-2
ALTER TABLE user_role ADD COLUMN role VARCHAR(50);

-- changeset denismalinin:1761945529000-3
UPDATE user_role
SET role = CASE role_id::int
    WHEN 0 THEN 'USER'
    WHEN 1 THEN 'ADMIN'
    WHEN 2 THEN 'SERVICE_ADMIN'
    ELSE 'USER'
END;

-- changeset denismalinin:1761945529000-4
ALTER TABLE user_role ALTER COLUMN role SET NOT NULL;

-- changeset denismalinin:1761945529000-5
CREATE INDEX idx_user_role_role ON user_role(role);

-- changeset denismalinin:1761945529000-6
ALTER TABLE user_role DROP COLUMN role_id;