CREATE TABLE interview_rounds (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    round_name VARCHAR(100) NOT NULL,
    round_type VARCHAR(50) NOT NULL,
    scheduled_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    interviewer_name VARCHAR(255),
    notes TEXT,
    feedback TEXT,
    result_status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_interview_rounds_application ON interview_rounds(application_id);
CREATE INDEX idx_interview_rounds_scheduled ON interview_rounds(scheduled_at);
