-- changeset denismalinin:1761945949000-1
CREATE SEQUENCE IF NOT EXISTS event_seq START WITH 600000 INCREMENT BY 50;

-- changeset denismalinin:1761945949000-2
CREATE TABLE event
(
    id        BIGINT NOT NULL,
    type      VARCHAR(255),
    source    UUID,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_event PRIMARY KEY (id)
);

ALTER TABLE event
    ADD CONSTRAINT fk_event_on_users
        FOREIGN KEY (source)
        REFERENCES users (id)
        ON DELETE SET NULL
        ON UPDATE NO ACTION;