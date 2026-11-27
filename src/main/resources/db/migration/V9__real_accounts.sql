ALTER TABLE userses ALTER COLUMN birthday DROP NOT NULL;
CREATE SEQUENCE atlas_student_number START WITH 1000000;
CREATE UNIQUE INDEX atlas_login_unique ON userses (username) WHERE username IS NOT NULL AND password_hash IS NOT NULL;
