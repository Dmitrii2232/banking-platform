CREATE INDEX IF NOT EXISTS idx_events_product_version ON events(product_id, version);
CREATE INDEX IF NOT EXISTS idx_events_type ON events(event_type);
CREATE INDEX IF NOT EXISTS idx_events_created_at ON events(created_at);
CREATE INDEX IF NOT EXISTS idx_products_client ON products(client_id);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_client_master ON products(client_id, is_master) WHERE is_master = TRUE;
CREATE INDEX IF NOT EXISTS idx_products_master_active ON products(is_master, status) WHERE is_master = TRUE AND status = 'ACTIVE';
CREATE INDEX IF NOT EXISTS idx_idempotency_expires ON idempotency_keys(expires_at);
CREATE INDEX IF NOT EXISTS idx_outbox_unsent ON outbox_messages(sent, created_at) WHERE sent = FALSE;