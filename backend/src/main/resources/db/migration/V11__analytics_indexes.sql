CREATE INDEX idx_applications_user_applied_at ON applications(user_id, applied_at DESC);
CREATE INDEX idx_applications_user_stage_entered_at ON applications(user_id, stage_entered_at DESC);
CREATE INDEX idx_applications_user_priority ON applications(user_id, priority);
CREATE INDEX idx_applications_user_deadline ON applications(user_id, application_deadline);
CREATE INDEX idx_applications_user_source ON applications(user_id, application_source);
CREATE INDEX idx_applications_user_work_mode ON applications(user_id, work_mode);
CREATE INDEX idx_applications_user_location ON applications(user_id, location);

CREATE INDEX idx_activity_log_user_created_at ON activity_log(user_id, created_at DESC);
