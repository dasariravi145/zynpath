-- =========================================================================
-- V6__indexes_and_performance_tuning.sql
-- Zynpath Performance, Leaderboard compound indexes, and Query Optimizations
-- =========================================================================

-- Compound indexes for active multiplayer matchmaking queries
CREATE INDEX IF NOT EXISTS idx_match_sessions_active ON match_sessions(match_state, game_mode) WHERE match_state IN ('LOBBY', 'IN_PROGRESS');

-- Compound indexes for daily leaderboard ranking
CREATE INDEX IF NOT EXISTS idx_daily_leaderboard_rank ON daily_challenge_attempts(canonical_date_utc, solve_duration_ms ASC, moves_count ASC) WHERE verification_status = 'VERIFIED';

-- Index for expiring multiplayer invitations cleanup
CREATE INDEX IF NOT EXISTS idx_inv_active ON multiplayer_invitations(recipient_player_id, status) WHERE status = 'PENDING';
