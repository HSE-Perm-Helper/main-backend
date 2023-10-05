INSERT INTO service_data (key, value) VALUES ('saved_schedules', '') ON CONFLICT DO NOTHING
INSERT INTO service_data (key, value) VALUES ('start_time', '') ON CONFLICT DO NOTHING