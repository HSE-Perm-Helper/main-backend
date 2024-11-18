-- liquibase formatted sql

-- changeset denismalinin:1731941872497-1
ALTER TABLE settings
    DROP COLUMN user_sub_group;

