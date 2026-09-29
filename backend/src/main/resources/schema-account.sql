-- =========================================================================
-- ZYNPATH ACCOUNT MANAGEMENT & PRIVACY SCHEMA (Prompt 32)
-- Relational DDL for Player Privacy Settings, Data Export, and Deletion Audit
-- =========================================================================

-- 1. Player Privacy Settings
CREATE TABLE IF NOT EXISTS player_privacy_settings (
    player_id VARCHAR(64) PRIMARY KEY,
    profile_visibility VARCHAR(32) NOT NULL DEFAULT 'PUBLIC',
    allow_zynpath_id_search BOOLEAN NOT NULL DEFAULT TRUE,
    allow_friend_requests BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at BIGINT NOT NULL,
    CONSTRAINT chk_profile_visibility CHECK (profile_visibility IN ('PUBLIC', 'FRIENDS_ONLY', 'PRIVATE'))
);

CREATE INDEX IF NOT EXISTS idx_privacy_visibility ON player_privacy_settings(profile_visibility);

-- 2. Data Export Requests Audit
CREATE TABLE IF NOT EXISTS account_export_requests (
    request_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    schema_version INT NOT NULL DEFAULT 1,
    exported_at BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
    CONSTRAINT fk_export_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_export_player ON account_export_requests(player_id);

-- 3. Account Deletion Audit
CREATE TABLE IF NOT EXISTS account_deletion_audits (
    deletion_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    public_zynpath_id VARCHAR(32),
    deleted_at BIGINT NOT NULL,
    reason VARCHAR(128) DEFAULT 'USER_REQUESTED'
);

CREATE INDEX IF NOT EXISTS idx_deletion_timestamp ON account_deletion_audits(deleted_at);
