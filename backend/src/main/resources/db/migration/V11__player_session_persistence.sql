-- =========================================================================
-- V11__player_session_persistence.sql
-- Zynpath Durable Player Session Persistence, Token Hashing & Revocation
-- =========================================================================

CREATE TABLE IF NOT EXISTS player_sessions (
    session_id VARCHAR(64) PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    player_id VARCHAR(64) NOT NULL,
    created_at BIGINT NOT NULL,
    expires_at BIGINT NOT NULL,
    last_used_at BIGINT NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at BIGINT,
    replaced_by_token_hash VARCHAR(64),
    CONSTRAINT fk_player_sessions_account FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_player_sessions_token_hash ON player_sessions(token_hash);
CREATE INDEX IF NOT EXISTS idx_player_sessions_player_id ON player_sessions(player_id);
CREATE INDEX IF NOT EXISTS idx_player_sessions_expires_at ON player_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_player_sessions_is_revoked ON player_sessions(is_revoked);
