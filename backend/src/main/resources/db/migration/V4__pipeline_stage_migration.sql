CREATE TABLE application_stages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    display_order INT NOT NULL,
    color_hex VARCHAR(20) NOT NULL,
    is_terminal_rejection BOOLEAN NOT NULL DEFAULT FALSE,
    is_terminal_success BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_application_stage_user_name UNIQUE (user_id, name)
);

ALTER TABLE applications
    ADD COLUMN stage_id BIGINT,
    ADD COLUMN stage_entered_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    ADD COLUMN deleted_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN location VARCHAR(255),
    ADD COLUMN work_mode VARCHAR(50),
    ADD COLUMN employment_type VARCHAR(50),
    ADD COLUMN salary_range VARCHAR(100),
    ADD COLUMN sponsorship_available BOOLEAN,
    ADD COLUMN application_source VARCHAR(100),
    ADD COLUMN application_deadline DATE,
    ADD COLUMN posting_url VARCHAR(1000),
    ADD COLUMN job_description TEXT;

INSERT INTO application_stages (user_id, name, display_order, color_hex, is_terminal_rejection, is_terminal_success)
SELECT u.id, 'Applied', 1, '#0F6DFF', FALSE, FALSE FROM users u
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO application_stages (user_id, name, display_order, color_hex, is_terminal_rejection, is_terminal_success)
SELECT u.id, 'Interviewing', 2, '#7A58FF', FALSE, FALSE FROM users u
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO application_stages (user_id, name, display_order, color_hex, is_terminal_rejection, is_terminal_success)
SELECT u.id, 'Offer', 3, '#0F9F82', FALSE, TRUE FROM users u
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO application_stages (user_id, name, display_order, color_hex, is_terminal_rejection, is_terminal_success)
SELECT u.id, 'Rejected', 4, '#DB4A63', TRUE, FALSE FROM users u
ON CONFLICT (user_id, name) DO NOTHING;

INSERT INTO application_stages (user_id, name, display_order, color_hex, is_terminal_rejection, is_terminal_success)
SELECT u.id, 'Hired', 5, '#F2A838', FALSE, TRUE FROM users u
ON CONFLICT (user_id, name) DO NOTHING;

WITH distinct_statuses AS (
    SELECT DISTINCT a.user_id, a.status
    FROM applications a
    WHERE a.status IS NOT NULL
), ranked AS (
    SELECT ds.user_id,
           ds.status,
           100 + ROW_NUMBER() OVER (PARTITION BY ds.user_id ORDER BY ds.status) AS display_order
    FROM distinct_statuses ds
    LEFT JOIN application_stages s ON s.user_id = ds.user_id AND s.name = ds.status
    WHERE s.id IS NULL
)
INSERT INTO application_stages (user_id, name, display_order, color_hex)
SELECT r.user_id, r.status, r.display_order, '#5A7494'
FROM ranked r
ON CONFLICT (user_id, name) DO NOTHING;

UPDATE applications a
SET stage_id = s.id,
    stage_entered_at = COALESCE(a.stage_entered_at, a.updated_at)
FROM application_stages s
WHERE s.user_id = a.user_id
  AND s.name = a.status;

CREATE TABLE application_stage_history (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    from_stage_id BIGINT REFERENCES application_stages(id) ON DELETE SET NULL,
    to_stage_id BIGINT NOT NULL REFERENCES application_stages(id) ON DELETE RESTRICT,
    changed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    reason VARCHAR(255)
);

INSERT INTO application_stage_history (application_id, from_stage_id, to_stage_id, changed_at, reason)
SELECT a.id, NULL, a.stage_id, COALESCE(a.stage_entered_at, a.updated_at), 'Migrated from legacy status'
FROM applications a
WHERE a.stage_id IS NOT NULL;

ALTER TABLE applications
    ALTER COLUMN stage_id SET NOT NULL,
    ALTER COLUMN stage_entered_at SET NOT NULL;

ALTER TABLE applications
    ADD CONSTRAINT fk_applications_stage
    FOREIGN KEY (stage_id) REFERENCES application_stages(id) ON DELETE RESTRICT;

ALTER TABLE applications
    DROP COLUMN status;

CREATE INDEX idx_application_stages_user_order ON application_stages(user_id, display_order);
CREATE INDEX idx_applications_user_stage ON applications(user_id, stage_id);
CREATE INDEX idx_applications_deleted_at ON applications(deleted_at);
CREATE INDEX idx_stage_history_application_changed ON application_stage_history(application_id, changed_at DESC);
