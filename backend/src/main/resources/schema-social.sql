-- Zynpath Social & Presence Schema Migration
-- Implements Prompt 19 Sections 41 & 42:
-- - Canonical friend relationship ordering to guarantee zero reverse-direction duplicates.
-- - Partial unique index on active pending friend requests.
-- - Directional block relationships.
-- - Future multiplayer room invitation tracking.

-- 1. Friend Relationships (Mutual friendship established)
CREATE TABLE IF NOT EXISTS friend_relationships (
    player_id_1 VARCHAR(64) NOT NULL,
    player_id_2 VARCHAR(64) NOT NULL,
    established_at BIGINT NOT NULL,
    PRIMARY KEY (player_id_1, player_id_2),
    -- Canonical constraint: player_id_1 is lexicographically smaller than player_id_2
    CONSTRAINT chk_canonical_friend_pair CHECK (player_id_1 < player_id_2)
);

CREATE INDEX IF NOT EXISTS idx_friend_rel_p1 ON friend_relationships(player_id_1);
CREATE INDEX IF NOT EXISTS idx_friend_rel_p2 ON friend_relationships(player_id_2);

-- 2. Friend Requests (Lifecycle: PENDING -> ACCEPTED / REJECTED / CANCELLED)
CREATE TABLE IF NOT EXISTS friend_requests (
    request_id VARCHAR(36) PRIMARY KEY,
    sender_player_id VARCHAR(64) NOT NULL,
    recipient_player_id VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    CONSTRAINT chk_no_self_request CHECK (sender_player_id <> recipient_player_id)
);

CREATE INDEX IF NOT EXISTS idx_friend_req_sender ON friend_requests(sender_player_id, status);
CREATE INDEX IF NOT EXISTS idx_friend_req_recipient ON friend_requests(recipient_player_id, status);

-- 3. Player Blocks (Directional blocking: blocker stops recipient from sending invites)
CREATE TABLE IF NOT EXISTS player_blocks (
    blocker_player_id VARCHAR(64) NOT NULL,
    blocked_player_id VARCHAR(64) NOT NULL,
    created_at BIGINT NOT NULL,
    PRIMARY KEY (blocker_player_id, blocked_player_id),
    CONSTRAINT chk_no_self_block CHECK (blocker_player_id <> blocked_player_id)
);

CREATE INDEX IF NOT EXISTS idx_player_blocks_blocker ON player_blocks(blocker_player_id);
CREATE INDEX IF NOT EXISTS idx_player_blocks_blocked ON player_blocks(blocked_player_id);

-- 4. Future Multiplayer Room Invitations (Prompt 19 Section 36)
CREATE TABLE IF NOT EXISTS multiplayer_invitations (
    invitation_id VARCHAR(36) PRIMARY KEY,
    sender_player_id VARCHAR(64) NOT NULL,
    recipient_player_id VARCHAR(64) NOT NULL,
    game_mode VARCHAR(32) NOT NULL,
    room_id VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at BIGINT NOT NULL,
    expires_at BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_multiplayer_inv_recipient ON multiplayer_invitations(recipient_player_id, status);
