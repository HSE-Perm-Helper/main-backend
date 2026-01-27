-- liquibase formatted sql

-- changeset denismalinin:1748594100651-1
CREATE TABLE user_role (user_id UUID NOT NULL, role_id SMALLINT);

-- changeset denismalinin:1748594100651-2
ALTER TABLE user_role ADD CONSTRAINT fk_user_role_on_user_entity FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset denismalinin:1748594100651-3
INSERT INTO user_role (user_id, role_id)
SELECT id, 0 FROM users;

