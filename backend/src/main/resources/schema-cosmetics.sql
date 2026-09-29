-- Schema for player cosmetic preferences
-- Prompt 28 Section 52 & 53

CREATE TABLE IF NOT EXISTS player_cosmetics (
    player_id VARCHAR(64) PRIMARY KEY,
    theme_id VARCHAR(64) NOT NULL DEFAULT 'theme_classic_midnight',
    path_effect_id VARCHAR(64) NOT NULL DEFAULT 'path_solid_glow',
    avatar_frame_id VARCHAR(64) NOT NULL DEFAULT 'frame_default_slate',
    updated_at_ms BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_player_cosmetics_updated ON player_cosmetics (updated_at_ms);
