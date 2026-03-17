CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    base_salary NUMERIC(12,2),
    bonus NUMERIC(12,2),
    equity NUMERIC(12,2),
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    location VARCHAR(255),
    offer_date DATE,
    response_deadline DATE,
    decision_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_offers_application ON offers(application_id);
