-- =========================================================================
-- V3__multiplayer_sessions_and_results.sql
-- Zynpath Multiplayer Sessions, Participants, and Authoritative Results
-- =========================================================================

-- 1. Match Sessions (Durable header record for multiplayer matches)
CREATE TABLE IF NOT EXISTS match_sessions (
    match_id VARCHAR(64) PRIMARY KEY,
    game_mode VARCHAR(32) NOT NULL,
    host_player_id VARCHAR(64) NOT NULL,
    match_state VARCHAR(32) NOT NULL,
    puzzle_id VARCHAR(64) NOT NULL,
    puzzle_fingerprint VARCHAR(128) NOT NULL,
    created_at BIGINT NOT NULL,
    started_at BIGINT,
    ended_at BIGINT,
    expires_at BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_match_sessions_state ON match_sessions(match_state);
CREATE INDEX IF NOT EXISTS idx_match_sessions_created ON match_sessions(created_at);

-- 2. Match Participants
CREATE TABLE IF NOT EXISTS match_participants (
    match_id VARCHAR(64) NOT NULL,
    player_id VARCHAR(64) NOT NULL,
    public_zynpath_id VARCHAR(32) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    avatar_id VARCHAR(64),
    joined_at BIGINT NOT NULL,
    is_ready BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at BIGINT,
    solve_time_ms BIGINT,
    finish_order INT,
    is_winner BOOLEAN NOT NULL DEFAULT FALSE,
    is_forfeited BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (match_id, player_id),
    CONSTRAINT fk_match_participant_session FOREIGN KEY (match_id) REFERENCES match_sessions(match_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_match_participants_player ON match_participants(player_id);

-- 3. Match Results (Immutable authoritative outcome records)
CREATE TABLE IF NOT EXISTS match_results (
    result_id VARCHAR(64) PRIMARY KEY,
    match_id VARCHAR(64) NOT NULL,
    player_id VARCHAR(64) NOT NULL,
    public_zynpath_id VARCHAR(32) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    completed BOOLEAN NOT NULL,
    solve_time_ms BIGINT NOT NULL,
    finish_order INT NOT NULL,
    is_winner BOOLEAN NOT NULL,
    outcome_status VARCHAR(32) NOT NULL,
    recorded_at BIGINT NOT NULL,
    CONSTRAINT uk_match_player_result UNIQUE (match_id, player_id),
    CONSTRAINT fk_match_result_session FOREIGN KEY (match_id) REFERENCES match_sessions(match_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_match_results_player ON match_results(player_id);
CREATE INDEX IF NOT EXISTS idx_match_results_match ON match_results(match_id);
