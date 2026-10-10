-- =========================================================================
-- V8__update_welcome_coins_and_solo_economy.sql
-- Update default welcome coin balance to 300 coins for new players.
-- Existing wallet balances are preserved.
-- =========================================================================

ALTER TABLE player_wallet_balances ALTER COLUMN coin_balance SET DEFAULT 300;
