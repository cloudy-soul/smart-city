ALTER TABLE userses
    ADD COLUMN student_active boolean NOT NULL DEFAULT true,
    ADD COLUMN credential_version integer NOT NULL DEFAULT 0;
