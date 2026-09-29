-- =========================================================================
-- V7__wallet_and_multiplayer_settlement.sql
-- Zynpath Authoritative Virtual Wallet Ledger and Multiplayer Settlements
-- =========================================================================

-- 1. Player Wallet Balances
CREATE TABLE IF NOT EXISTS player_wallet_balances (
    player_id VARCHAR(64) PRIMARY KEY,
    coin_balance INT NOT NULL DEFAULT 60,
    total_stars INT NOT NULL DEFAULT 0,
    updated_at_ms BIGINT NOT NULL,
    CONSTRAINT fk_wallet_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_wallet_balance_updated ON player_wallet_balances (updated_at_ms);

-- 2. Immutable Wallet Transactions (Ledger)
CREATE TABLE IF NOT EXISTS wallet_transactions (
    transaction_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    type VARCHAR(32) NOT NULL,
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    metadata_json TEXT,
    created_at_ms BIGINT NOT NULL,
    CONSTRAINT fk_wallet_tx_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_wallet_tx_player_id ON wallet_transactions (player_id);
CREATE INDEX IF NOT EXISTS idx_wallet_tx_created ON wallet_transactions (created_at_ms);

-- 3. Authoritative Multiplayer Settlements
CREATE TABLE IF NOT EXISTS multiplayer_settlements (
    match_id VARCHAR(64) PRIMARY KEY,
    mode VARCHAR(32) NOT NULL,
    player_count INT NOT NULL,
    settlement_status VARCHAR(32) NOT NULL,
    payouts_json TEXT,
    settled_at_ms BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mp_settlement_status ON multiplayer_settlements (settlement_status);
