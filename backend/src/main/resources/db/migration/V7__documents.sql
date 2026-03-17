CREATE TABLE application_documents (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    resume_version VARCHAR(100),
    cover_letter_version VARCHAR(100),
    resume_ref VARCHAR(1000),
    cover_letter_ref VARCHAR(1000),
    portfolio_ref VARCHAR(1000),
    github_url VARCHAR(1000),
    linkedin_url VARCHAR(1000),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_documents_application UNIQUE (application_id)
);
