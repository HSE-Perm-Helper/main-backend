-- liquibase formatted sql

-- changeset denismalinin:1738779392185-1
ALTER TABLE settings
    ADD email VARCHAR(255);

