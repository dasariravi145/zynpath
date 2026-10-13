-- =========================================================================
-- V9__player_level_progress_and_sync_operations.sql
-- Zynpath Authoritative Solo Level Progress, Batch Sync Deduplication,
-- and Non-Negative Wallet Balance Safety Constraint
-- =========================================================================

-- 1. Authoritative Solo Level Progress (1,800 levels across 6 worlds)
CREATE TABLE IF NOT EXISTS player_level_progress (
    player_id VARCHAR(64) NOT NULL,
    level_id INT NOT NULL,
    world_id INT NOT NULL,
    stars INT NOT NULL DEFAULT 0,
    best_time_ms BIGINT NOT NULL DEFAULT 0,
    moves_count INT NOT NULL DEFAULT 0,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    best_hint_count INT NOT NULL DEFAULT 0,
    completed_at_ms BIGINT,
    updated_at_ms BIGINT NOT NULL,
    PRIMARY KEY (player_id, level_id),
    CONSTRAINT fk_progress_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_progress_player ON player_level_progress (player_id);
CREATE INDEX IF NOT EXISTS idx_progress_world ON player_level_progress (player_id, world_id);
CREATE INDEX IF NOT EXISTS idx_progress_level ON player_level_progress (level_id);

-- 2. Idempotent Sync Batch Operations Log (Deduplicates client offline batch replays)
CREATE TABLE IF NOT EXISTS sync_operations (
    operation_id VARCHAR(128) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    operation_type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    message TEXT,
    server_timestamp_ms BIGINT NOT NULL,
    CONSTRAINT fk_sync_op_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sync_op_player ON sync_operations (player_id);
CREATE INDEX IF NOT EXISTS idx_sync_op_timestamp ON sync_operations (server_timestamp_ms);

-- 3. Database-Level Invariant: Prevent Negative Wallet Balances
ALTER TABLE player_wallet_balances ADD CONSTRAINT chk_wallet_non_negative_balance CHECK (coin_balance >= 0);
