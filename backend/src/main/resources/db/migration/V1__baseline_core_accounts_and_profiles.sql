-- =========================================================================
-- V1__baseline_core_accounts_and_profiles.sql
-- Zynpath Core Accounts, External Identities, Profiles, and Subscriptions
-- =========================================================================

-- 1. Player Accounts (Authoritative core identity)
CREATE TABLE IF NOT EXISTS player_accounts (
    player_id VARCHAR(64) PRIMARY KEY,
    public_zynpath_id VARCHAR(32) NOT NULL UNIQUE,
    display_name VARCHAR(64) NOT NULL,
    account_type VARCHAR(32) NOT NULL DEFAULT 'REGISTERED',
    created_at BIGINT NOT NULL,
    last_login_at BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_player_accounts_public_id ON player_accounts(public_zynpath_id);
CREATE INDEX IF NOT EXISTS idx_player_accounts_created_at ON player_accounts(created_at);

-- 2. External Provider Identities (OAuth mappings: Google, Facebook)
CREATE TABLE IF NOT EXISTS external_identities (
    provider VARCHAR(32) NOT NULL,
    provider_subject_id VARCHAR(128) NOT NULL,
    player_id VARCHAR(64) NOT NULL,
    linked_at BIGINT NOT NULL,
    PRIMARY KEY (provider, provider_subject_id),
    CONSTRAINT fk_external_identity_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ext_identity_player ON external_identities(player_id);

-- 3. Player Profiles (Public presentation state)
CREATE TABLE IF NOT EXISTS player_profiles (
    player_id VARCHAR(64) PRIMARY KEY,
    display_name VARCHAR(64) NOT NULL,
    public_zynpath_id VARCHAR(32) NOT NULL UNIQUE,
    avatar_id VARCHAR(64) NOT NULL DEFAULT 'avatar_01',
    equipped_frame_id VARCHAR(64) NOT NULL DEFAULT 'frame_default_slate',
    equipped_theme_id VARCHAR(64) NOT NULL DEFAULT 'theme_classic_midnight',
    equipped_path_effect_id VARCHAR(64) NOT NULL DEFAULT 'path_solid_glow',
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    CONSTRAINT fk_profile_account FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

-- 4. Subscription Entitlements (Google Play Billing server-authoritative state)
CREATE TABLE IF NOT EXISTS subscription_entitlements (
    account_id VARCHAR(64) PRIMARY KEY,
    status VARCHAR(32) NOT NULL DEFAULT 'FREE',
    product_id VARCHAR(64),
    base_plan_id VARCHAR(64),
    current_period_end_ms BIGINT NOT NULL DEFAULT 0,
    last_verified_at_ms BIGINT NOT NULL DEFAULT 0,
    is_auto_renewing BOOLEAN NOT NULL DEFAULT FALSE,
    purchase_token_hash VARCHAR(128),
    CONSTRAINT fk_subscription_account FOREIGN KEY (account_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sub_token_hash ON subscription_entitlements(purchase_token_hash);

-- 5. Daily Challenge Attempts & Results
CREATE TABLE IF NOT EXISTS daily_challenge_attempts (
    attempt_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    canonical_date_utc VARCHAR(10) NOT NULL,
    started_at_ms BIGINT NOT NULL,
    completed_at_ms BIGINT,
    solve_duration_ms BIGINT,
    moves_count INT,
    verification_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    is_provisional BOOLEAN NOT NULL DEFAULT FALSE,
    recorded_at_ms BIGINT NOT NULL,
    CONSTRAINT uq_daily_player_date UNIQUE (player_id, canonical_date_utc),
    CONSTRAINT fk_daily_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_daily_date_leaderboard ON daily_challenge_attempts(canonical_date_utc, solve_duration_ms, moves_count);

-- 6. Competitive Player Statistics
CREATE TABLE IF NOT EXISTS competitive_stats (
    player_id VARCHAR(64) PRIMARY KEY,
    quick_duel_matches_played INT NOT NULL DEFAULT 0,
    quick_duel_matches_won INT NOT NULL DEFAULT 0,
    friend_duel_matches_played INT NOT NULL DEFAULT 0,
    friend_duel_matches_won INT NOT NULL DEFAULT 0,
    mini_league_matches_played INT NOT NULL DEFAULT 0,
    mini_league_podiums INT NOT NULL DEFAULT 0,
    last_rating_delta INT NOT NULL DEFAULT 0,
    current_rating INT NOT NULL DEFAULT 1000,
    updated_at_ms BIGINT NOT NULL,
    CONSTRAINT fk_stats_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);
