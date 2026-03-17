CREATE TABLE application_contacts (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    recruiter_name VARCHAR(200),
    recruiter_email VARCHAR(255),
    referral_contact_name VARCHAR(200),
    referral_contact_email VARCHAR(255),
    outreach_date DATE,
    referral_requested BOOLEAN NOT NULL DEFAULT FALSE,
    referral_received BOOLEAN NOT NULL DEFAULT FALSE,
    follow_up_notes TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_application_contacts_application ON application_contacts(application_id);
