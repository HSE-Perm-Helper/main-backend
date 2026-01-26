-- liquibase formatted sql

-- changeset denismalinin:1730668933639-1
--preconditions onError:MARK_RAN
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public' AND tablename  = 'user_event_entity';
ALTER TABLE user_event_entity
    DROP CONSTRAINT fkinfnvyqtvkj43bkbgd054qjct;

-- changeset denismalinin:1730668933639-2
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM pg_tables WHERE schemaname = 'public' AND tablename  = 'user_event_entity';
DROP TABLE user_event_entity CASCADE;

-- changeset denismalinin:1730668933639-3
--preconditions onError:MARK_RAN
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='settings' and column_name='include_common_english';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='settings' and column_name='include_common_minor';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='settings' and column_name='include_quarter_schedule';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='settings' and column_name='is_enabled_new_common_schedule_notification';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='settings' and column_name='is_enabled_new_quarter_schedule_notification';
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_name='settings' and column_name='is_enabled_remote_calendar';
ALTER TABLE settings
    DROP COLUMN include_common_english;
ALTER TABLE settings
    DROP COLUMN include_common_minor;
ALTER TABLE settings
    DROP COLUMN include_quarter_schedule;
ALTER TABLE settings
    DROP COLUMN is_enabled_new_common_schedule_notification;
ALTER TABLE settings
    DROP COLUMN is_enabled_new_quarter_schedule_notification;
ALTER TABLE settings
    DROP COLUMN is_enabled_remote_calendar;

-- changeset denismalinin:1730668933639-9
--preconditions onError:MARK_RAN
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM pg_class where relname = 'user_event_entity_seq'
DROP SEQUENCE user_event_entity_seq CASCADE;

