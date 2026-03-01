-- liquibase formatted sql

-- changeset denismalinin:1730671351362-1
ALTER TABLE users
    ADD created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users
    RENAME COLUMN user_id TO settings_id;

