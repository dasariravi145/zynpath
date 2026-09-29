-- =========================================================================
-- V4__cosmetics_and_ad_rewards.sql
-- Zynpath Cosmetic Preferences, Hint Balances, and Rewarded Ad Events
-- =========================================================================

-- 1. Player Cosmetics Preferences
CREATE TABLE IF NOT EXISTS player_cosmetics (
    player_id VARCHAR(64) PRIMARY KEY,
    theme_id VARCHAR(64) NOT NULL DEFAULT 'theme_classic_midnight',
    path_effect_id VARCHAR(64) NOT NULL DEFAULT 'path_solid_glow',
    avatar_frame_id VARCHAR(64) NOT NULL DEFAULT 'frame_default_slate',
    updated_at_ms BIGINT NOT NULL,
    CONSTRAINT fk_cosmetics_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_cosmetics_updated ON player_cosmetics (updated_at_ms);

-- 2. Player Hint Balances
CREATE TABLE IF NOT EXISTS player_hint_balances (
    player_id VARCHAR(64) PRIMARY KEY,
    free_hints_remaining INT NOT NULL DEFAULT 3,
    rewarded_credits INT NOT NULL DEFAULT 0,
    total_available INT NOT NULL DEFAULT 3,
    last_updated_ms BIGINT NOT NULL,
    CONSTRAINT fk_hint_balance_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

-- 3. Reward Events (Idempotent tracking of earned hint rewards)
CREATE TABLE IF NOT EXISTS reward_events (
    reward_event_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    transaction_id VARCHAR(128),
    reward_type VARCHAR(32) NOT NULL DEFAULT 'SOLO_HINT',
    amount INT NOT NULL DEFAULT 1,
    ad_unit_id VARCHAR(128),
    verification_status VARCHAR(32) NOT NULL,
    granted_at_ms BIGINT NOT NULL,
    consumed_at_ms BIGINT,
    CONSTRAINT fk_reward_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reward_player_id ON reward_events (player_id);
CREATE INDEX IF NOT EXISTS idx_reward_transaction_id ON reward_events (transaction_id);

-- 4. AdMob SSV Records (Server-Side Verification idempotency records)
CREATE TABLE IF NOT EXISTS admob_ssv_records (
    transaction_id VARCHAR(128) PRIMARY KEY,
    key_id VARCHAR(64),
    signature TEXT,
    custom_data TEXT,
    user_id VARCHAR(64),
    verified_at_ms BIGINT NOT NULL,
    is_valid BOOLEAN NOT NULL,
    failure_reason VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_ssv_user ON admob_ssv_records (user_id);
