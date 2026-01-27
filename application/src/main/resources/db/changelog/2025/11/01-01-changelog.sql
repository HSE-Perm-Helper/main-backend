-- liquibase formatted sql

-- changeset denismalinin:1761945526000-1
ALTER TABLE settings
    ADD education_type VARCHAR(31);

-- changeset denismalinin:1761945526000-2
UPDATE settings
SET education_type = 'BACHELOR_OFFLINE';

-- changeset denismalinin:1761945526000-3
ALTER TABLE settings
    ALTER COLUMN education_type SET NOT NULL;

-- changeset denismalinin:1761945526000-4
ALTER TABLE users
    ADD COLUMN education_type VARCHAR(31),
    ADD COLUMN user_group VARCHAR(50),
    ADD COLUMN is_enabled_new_schedule_notification BOOLEAN,
    ADD COLUMN is_enabled_changed_schedule_notification BOOLEAN,
    ADD COLUMN is_enabled_coming_lessons_notification BOOLEAN;

UPDATE users u
SET
    education_type = s.education_type,
    user_group = s.user_group,
    is_enabled_new_schedule_notification = s.is_enabled_new_schedule_notification,
    is_enabled_changed_schedule_notification = s.is_enabled_changed_schedule_notification,
    is_enabled_coming_lessons_notification = s.is_enabled_coming_lessons_notifications
FROM settings s
WHERE s.id = u.settings_id;

-- changeset denismalinin:1761945526000-5
ALTER TABLE users
    ALTER COLUMN education_type SET NOT NULL,
    ALTER COLUMN user_group SET NOT NULL,
    ALTER COLUMN is_enabled_new_schedule_notification SET NOT NULL,
    ALTER COLUMN is_enabled_changed_schedule_notification SET NOT NULL,
    ALTER COLUMN is_enabled_coming_lessons_notification SET NOT NULL;

-- changeset denismalinin:1761945526000-6
CREATE TABLE hide_lesson_new AS
SELECT
    u.id AS user_id,
    hl.lesson,
    hl.lesson_type,
    hl.sub_group
FROM hide_lesson hl
JOIN settings_hidden_lessons ssh ON ssh.hidden_lessons_id = hl.id
JOIN users u ON u.settings_id = ssh.settings_entity_id;

ALTER TABLE users DROP CONSTRAINT fk_users_on_user;
DROP TABLE settings_hidden_lessons;
DROP TABLE hide_lesson;
DROP TABLE settings;

ALTER TABLE hide_lesson_new RENAME TO hide_lesson;

ALTER TABLE hide_lesson
    ADD CONSTRAINT pk_hide_lesson PRIMARY KEY (user_id, lesson, lesson_type, sub_group);

CREATE INDEX idx_hide_lesson_user_id ON hide_lesson(user_id);