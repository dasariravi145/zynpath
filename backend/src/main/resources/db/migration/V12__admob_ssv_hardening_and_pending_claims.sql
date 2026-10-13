-- =========================================================================
-- V12__admob_ssv_hardening_and_pending_claims.sql
-- Zynpath AdMob Server-Side Verification Hardening & Pending Claims
-- =========================================================================

-- 1. Pending Reward Claims (Enforces two-phase verification and matching)
CREATE TABLE IF NOT EXISTS pending_reward_claims (
    claim_id VARCHAR(128) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    ad_unit_id VARCHAR(128) NOT NULL,
    level_id INT,
    reward_type VARCHAR(32) NOT NULL DEFAULT 'SOLO_HINT',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    transaction_id VARCHAR(128),
    created_at_ms BIGINT NOT NULL,
    verified_at_ms BIGINT,
    consumed_at_ms BIGINT,
    failure_reason VARCHAR(255),
    CONSTRAINT fk_pending_claim_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pending_claim_player ON pending_reward_claims (player_id);
CREATE INDEX IF NOT EXISTS idx_pending_claim_status ON pending_reward_claims (status);
CREATE UNIQUE INDEX IF NOT EXISTS uq_pending_claim_txn ON pending_reward_claims (transaction_id) WHERE transaction_id IS NOT NULL;

-- 2. Additive columns and constraints on admob_ssv_records
ALTER TABLE admob_ssv_records ADD COLUMN IF NOT EXISTS claim_id VARCHAR(128);
ALTER TABLE admob_ssv_records ADD COLUMN IF NOT EXISTS ad_unit_id VARCHAR(128);
ALTER TABLE admob_ssv_records ADD COLUMN IF NOT EXISTS reward_amount INT DEFAULT 1;
ALTER TABLE admob_ssv_records ADD COLUMN IF NOT EXISTS raw_query_string TEXT;

CREATE UNIQUE INDEX IF NOT EXISTS uq_admob_ssv_claim_id ON admob_ssv_records (claim_id) WHERE claim_id IS NOT NULL;

-- 3. Unique transaction constraint on reward_events to guarantee exactly-once grants
CREATE UNIQUE INDEX IF NOT EXISTS uq_reward_events_transaction_id ON reward_events (transaction_id) WHERE transaction_id IS NOT NULL;
