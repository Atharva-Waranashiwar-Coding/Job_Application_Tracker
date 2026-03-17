-- Update the default admin password to "admin123".
-- This is a new migration to avoid modifying an already-applied migration.

UPDATE users
SET password_hash = '$2b$12$yfaHdX7o2dgI/MY4EhW68.s4494cIUR.KrXbhxLmH1UaJP.sBl7iW'
WHERE username = 'admin';
