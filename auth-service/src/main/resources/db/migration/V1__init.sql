CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  username TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  role TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Seeds (passwords will be set later when JWT/security is implemented)
INSERT INTO users (username, password_hash, role)
VALUES
  ('admin', 'CHANGE_ME', 'ADMIN'),
  ('operator', 'CHANGE_ME', 'OPERATOR')
ON CONFLICT (username) DO NOTHING;

