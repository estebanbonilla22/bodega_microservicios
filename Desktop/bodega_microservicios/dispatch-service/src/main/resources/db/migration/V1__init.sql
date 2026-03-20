CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS dispatches (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  movement_id UUID NOT NULL,
  address TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'CREATED',
  delivered_at TIMESTAMPTZ,
  notes TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_dispatches_movement_id ON dispatches(movement_id);

