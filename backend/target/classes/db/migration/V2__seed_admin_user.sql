-- Seed a default admin user (username: admin, password: admin)
INSERT INTO users (username, password_hash, display_name, role)
VALUES ('admin', '$2b$12$HNBlEEwHvFB5JyYExEU6uOmji8A/hndN9CjXUQy82yP.f5Hm9nie6', 'Administrator', 'ADMIN')
ON CONFLICT (username) DO NOTHING;
