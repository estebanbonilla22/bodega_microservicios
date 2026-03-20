CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS stock_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID NOT NULL UNIQUE,
  quantity BIGINT NOT NULL DEFAULT 0,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS movements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID NOT NULL,
  type TEXT NOT NULL,
  quantity BIGINT NOT NULL CHECK (quantity > 0),
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  note TEXT,
  created_by TEXT
);

CREATE INDEX IF NOT EXISTS idx_movements_product_id ON movements(product_id);
CREATE INDEX IF NOT EXISTS idx_movements_occurred_at ON movements(occurred_at);

