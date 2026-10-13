-- =========================================================================
-- V10__multiplayer_dynamic_puzzles.sql
-- Zynpath Multiplayer Dynamic Puzzle Inventory, Durable Storage, and Repeat Tracking
-- =========================================================================

-- 1. Generated Puzzles (Durable inventory for dynamic solver-verified puzzles)
CREATE TABLE IF NOT EXISTS multiplayer_generated_puzzles (
    puzzle_id VARCHAR(64) PRIMARY KEY,
    generator_version INT NOT NULL,
    fingerprint VARCHAR(128) NOT NULL UNIQUE,
    difficulty_tier VARCHAR(32) NOT NULL,
    game_mode VARCHAR(32) NOT NULL,
    grid_rows INT NOT NULL,
    grid_cols INT NOT NULL,
    required_cells_json TEXT NOT NULL,
    checkpoints_json TEXT NOT NULL,
    blocked_edges_json TEXT NOT NULL,
    canonical_solution_json TEXT NOT NULL,
    cognitive_score DOUBLE PRECISION NOT NULL,
    is_validated BOOLEAN NOT NULL DEFAULT TRUE,
    inventory_status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    created_at BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mp_puzzles_lookup 
    ON multiplayer_generated_puzzles(game_mode, difficulty_tier, inventory_status);

CREATE INDEX IF NOT EXISTS idx_mp_puzzles_tier 
    ON multiplayer_generated_puzzles(difficulty_tier);

-- 2. Puzzle Assignments (Match to puzzle mapping with idempotency protection)
CREATE TABLE IF NOT EXISTS multiplayer_puzzle_assignments (
    assignment_id VARCHAR(64) PRIMARY KEY,
    puzzle_id VARCHAR(64) NOT NULL,
    match_id VARCHAR(64) NOT NULL,
    game_mode VARCHAR(32) NOT NULL,
    difficulty_tier VARCHAR(32) NOT NULL,
    assigned_at BIGINT NOT NULL,
    participant_ids_json TEXT,
    CONSTRAINT uk_match_assignment UNIQUE (match_id)
);

CREATE INDEX IF NOT EXISTS idx_mp_assignments_puzzle ON multiplayer_puzzle_assignments(puzzle_id);
CREATE INDEX IF NOT EXISTS idx_mp_assignments_time ON multiplayer_puzzle_assignments(assigned_at);

-- 3. Player Puzzle Assignment History (For recent repeat suppression)
CREATE TABLE IF NOT EXISTS multiplayer_player_puzzle_history (
    id BIGSERIAL PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    puzzle_id VARCHAR(64) NOT NULL,
    fingerprint VARCHAR(128) NOT NULL,
    game_mode VARCHAR(32) NOT NULL,
    assigned_at BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_player_puzzle_history_lookup 
    ON multiplayer_player_puzzle_history(player_id, assigned_at DESC);
