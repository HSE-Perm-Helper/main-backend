-- liquibase formatted sql

-- changeset denismalinin:1761945549000-1
CREATE TABLE excel_timetable (
    id VARCHAR(7) PRIMARY KEY,
    number INT,
    start DATE NOT NULL,
    "end" DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    education_type VARCHAR(50) NOT NULL,
    is_parent BOOLEAN NOT NULL,
    source VARCHAR(50) NOT NULL,
    is_visible BOOLEAN NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    created TIMESTAMP NOT NULL,
    updated TIMESTAMP NOT NULL
);

-- changeset denismalinin:1761945549000-2
CREATE INDEX idx_excel_timetable_start_end ON excel_timetable (start, "end");
CREATE INDEX idx_excel_timetable_type ON excel_timetable (type);
CREATE INDEX idx_excel_timetable_education_type ON excel_timetable (education_type);
CREATE INDEX idx_excel_timetable_source ON excel_timetable (source);
CREATE INDEX idx_excel_timetable_created_by ON excel_timetable (created_by);

-- changeset denismalinin:1761945549000-3
CREATE TABLE group_lessons (
    timetable_id VARCHAR(7) NOT NULL,
    "group" VARCHAR(100) NOT NULL,
    lessons JSONB NOT NULL,
    PRIMARY KEY (timetable_id, "group")
);

-- changeset denismalinin:1761945549000-4
ALTER TABLE group_lessons
    ADD CONSTRAINT fk_group_lessons_timetable_id
        FOREIGN KEY (timetable_id)
        REFERENCES excel_timetable (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION;

-- changeset denismalinin:1761945549000-5
CREATE INDEX idx_group_lessons_timetable_id ON group_lessons (timetable_id);
CREATE INDEX idx_group_lessons_group ON group_lessons ("group");

-- changeset denismalinin:1761945549000-6
CREATE TABLE excel_file_metadata (
    id VARCHAR(7) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hash VARCHAR(32) NOT NULL,
    CONSTRAINT uk_excel_file_metadata_hash UNIQUE (hash)
);

-- changeset denismalinin:1761945549000-7
CREATE INDEX idx_users_group ON users(user_group);

-- changeset denismalinin:1761945549000-8
CREATE INDEX idx_users_education_type ON users(education_type);

-- changeset denismalinin:1761945549000-9
ALTER TABLE users DROP COLUMN settings_id;

-- changeset denismalinin:1761945549000-10
ALTER TABLE excel_timetable ADD COLUMN lessons_hash INT NOT NULL;

-- changeset denismalinin:1761945549000-11
ALTER TABLE user_role DROP CONSTRAINT fk_user_role_on_user_entity;
ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION;

-- changeset denismalinin:1761945549000-12
ALTER TABLE hide_lesson
    ADD CONSTRAINT fk_hide_lesson_on_users
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION;
