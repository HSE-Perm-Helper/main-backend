-- liquibase formatted sql

-- changeset denismalinin:1761946949000-1
CREATE TABLE token
(
    user_id     uuid                        NOT NULL,
    token       VARCHAR(64)                 NOT NULL,
    last_fetch  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_token PRIMARY KEY (user_id)
);