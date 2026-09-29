-- =========================================================================
-- V2__social_and_friend_relationships.sql
-- Zynpath Friend Relationships, Requests, Blocks, and Room Invitations
-- =========================================================================

-- 1. Friend Relationships (Mutual friendships; canonical ordering player_id_1 < player_id_2)
CREATE TABLE IF NOT EXISTS friend_relationships (
    player_id_1 VARCHAR(64) NOT NULL,
    player_id_2 VARCHAR(64) NOT NULL,
    established_at BIGINT NOT NULL,
    PRIMARY KEY (player_id_1, player_id_2),
    CONSTRAINT chk_canonical_friend_pair CHECK (player_id_1 < player_id_2),
    CONSTRAINT fk_friend_p1 FOREIGN KEY (player_id_1) REFERENCES player_accounts(player_id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_p2 FOREIGN KEY (player_id_2) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_friend_rel_p1 ON friend_relationships(player_id_1);
CREATE INDEX IF NOT EXISTS idx_friend_rel_p2 ON friend_relationships(player_id_2);

-- 2. Friend Requests (Pending, Accepted, Rejected, Cancelled)
CREATE TABLE IF NOT EXISTS friend_requests (
    request_id VARCHAR(36) PRIMARY KEY,
    sender_player_id VARCHAR(64) NOT NULL,
    recipient_player_id VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    CONSTRAINT chk_no_self_request CHECK (sender_player_id <> recipient_player_id),
    CONSTRAINT fk_req_sender FOREIGN KEY (sender_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_recipient FOREIGN KEY (recipient_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_friend_req_sender ON friend_requests(sender_player_id, status);
CREATE INDEX IF NOT EXISTS idx_friend_req_recipient ON friend_requests(recipient_player_id, status);

-- 3. Player Blocks (Directional blocking: blocker stops target from sending invites)
CREATE TABLE IF NOT EXISTS player_blocks (
    blocker_player_id VARCHAR(64) NOT NULL,
    blocked_player_id VARCHAR(64) NOT NULL,
    created_at BIGINT NOT NULL,
    PRIMARY KEY (blocker_player_id, blocked_player_id),
    CONSTRAINT chk_no_self_block CHECK (blocker_player_id <> blocked_player_id),
    CONSTRAINT fk_block_blocker FOREIGN KEY (blocker_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE,
    CONSTRAINT fk_block_blocked FOREIGN KEY (blocked_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_blocks_blocker ON player_blocks(blocker_player_id);
CREATE INDEX IF NOT EXISTS idx_player_blocks_blocked ON player_blocks(blocked_player_id);

-- 4. Multiplayer Room Invitations
CREATE TABLE IF NOT EXISTS multiplayer_invitations (
    invitation_id VARCHAR(36) PRIMARY KEY,
    sender_player_id VARCHAR(64) NOT NULL,
    recipient_player_id VARCHAR(64) NOT NULL,
    game_mode VARCHAR(32) NOT NULL,
    room_id VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at BIGINT NOT NULL,
    expires_at BIGINT NOT NULL,
    CONSTRAINT fk_inv_sender FOREIGN KEY (sender_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_recipient FOREIGN KEY (recipient_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_multiplayer_inv_recipient ON multiplayer_invitations(recipient_player_id, status);
CREATE INDEX IF NOT EXISTS idx_multiplayer_inv_expiry ON multiplayer_invitations(expires_at);
