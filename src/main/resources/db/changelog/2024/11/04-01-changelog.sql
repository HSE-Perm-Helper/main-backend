-- liquibase formatted sql

-- changeset denismalinin:1730668446098-1
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM hide_lesson
CREATE SEQUENCE IF NOT EXISTS hide_lesson_seq START WITH 1 INCREMENT BY 50;

-- changeset denismalinin:1730668446098-2
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM settings
CREATE SEQUENCE IF NOT EXISTS settings_seq START WITH 1 INCREMENT BY 50;

-- changeset denismalinin:1730668446098-3
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM hide_lesson
CREATE TABLE hide_lesson
(
    id          BIGINT       NOT NULL,
    lesson      VARCHAR(255) NOT NULL,
    lesson_type VARCHAR(64) NOT NULL,
    sub_group   INTEGER,
    CONSTRAINT pk_hide_lesson PRIMARY KEY (id)
);

-- changeset denismalinin:1730668446098-4
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM settings
CREATE TABLE settings
(
    id                                       BIGINT NOT NULL,
    user_group                               VARCHAR(32),
    user_sub_group                           INTEGER,
    is_enabled_new_schedule_notification     BOOLEAN DEFAULT TRUE,
    is_enabled_changed_schedule_notification BOOLEAN DEFAULT TRUE,
    is_enabled_coming_lessons_notifications  BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_settings PRIMARY KEY (id)
);

-- changeset denismalinin:1730668446098-5
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM settings_hidden_lessons
CREATE TABLE settings_hidden_lessons
(
    settings_entity_id BIGINT NOT NULL,
    hidden_lessons_id  BIGINT NOT NULL,
    CONSTRAINT pk_settings_hiddenlessons PRIMARY KEY (settings_entity_id, hidden_lessons_id)
);

-- changeset denismalinin:1730668446098-6
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users
CREATE TABLE users
(
    id          UUID NOT NULL,
    telegram_id BIGINT,
    user_id     BIGINT,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset denismalinin:1730668446098-7
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM settings_hidden_lessons
ALTER TABLE settings_hidden_lessons
    ADD CONSTRAINT uc_settings_hidden_lessons_hiddenlessons UNIQUE (hidden_lessons_id);

-- changeset denismalinin:1730668446098-8
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users
ALTER TABLE users
    ADD CONSTRAINT uc_users_user UNIQUE (user_id);

-- changeset denismalinin:1730668446098-9
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM users
ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_USER FOREIGN KEY (user_id) REFERENCES settings (id) ON DELETE CASCADE;

-- changeset denismalinin:1730668446098-10
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM settings_hidden_lessons
ALTER TABLE settings_hidden_lessons
    ADD CONSTRAINT fk_sethidles_on_hide_lesson_entity FOREIGN KEY (hidden_lessons_id) REFERENCES hide_lesson (id);

-- changeset denismalinin:1730668446098-11
--preconditions onFail:MARK_RAN
--preconditions onError:WARN
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM settings_hidden_lessons
ALTER TABLE settings_hidden_lessons
    ADD CONSTRAINT fk_sethidles_on_settings_entity FOREIGN KEY (settings_entity_id) REFERENCES settings (id);

