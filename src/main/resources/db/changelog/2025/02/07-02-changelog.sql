-- liquibase formatted sql

-- changeset denismalinin:1738953780514-1
CREATE TABLE email_verification
(
    token        VARCHAR(10) NOT NULL,
    user_id      UUID,
    email        VARCHAR(255),
    created      TIMESTAMP WITHOUT TIME ZONE,
    attempts     INTEGER     NOT NULL,
    next_attempt TIMESTAMP WITHOUT TIME ZONE,
    secret       VARCHAR(24),
    CONSTRAINT pk_emailverification PRIMARY KEY (token)
);

-- changeset denismalinin:1738953780514-2
ALTER TABLE email_verification
    ADD CONSTRAINT uc_emailverification_secret UNIQUE (secret);

-- changeset denismalinin:1738953780514-3
ALTER TABLE email_verification
    ADD CONSTRAINT uc_emailverification_user UNIQUE (user_id);

-- changeset denismalinin:1738953780514-4
ALTER TABLE email_verification
    ADD CONSTRAINT FK_EMAILVERIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

