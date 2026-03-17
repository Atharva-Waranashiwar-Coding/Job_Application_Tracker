CREATE TABLE application_notes (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    content TEXT,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_application_notes_category UNIQUE (application_id, category)
);

CREATE INDEX idx_application_notes_application ON application_notes(application_id);
