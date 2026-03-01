-- liquibase formatted sql

-- changeset denismalinin:1738916397538-1
ALTER TABLE users
    ADD email VARCHAR(255);

-- changeset denismalinin:1738916397538-2
UPDATE users u
SET email = s.email
FROM settings s
WHERE s.id = u.settings_id AND s.email IS NOT NULL;

-- changeset denismalinin:1738916397538-3
ALTER TABLE settings
    DROP COLUMN email;

-- changeset denismalinin:1738916397538-4
ALTER TABLE users
    ADD CONSTRAINT uc_users_telegram UNIQUE (telegram_id);

