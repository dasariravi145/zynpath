-- =========================================================================
-- V5__notifications_and_privacy.sql
-- Zynpath Notifications, Privacy Settings, Data Export, and Deletion Audits
-- =========================================================================

-- 1. Player In-App Notifications
CREATE TABLE IF NOT EXISTS player_notifications (
    notification_id VARCHAR(36) PRIMARY KEY,
    recipient_player_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(48) NOT NULL,
    title VARCHAR(128) NOT NULL,
    message VARCHAR(256) NOT NULL,
    related_resource_id VARCHAR(64),
    action_destination VARCHAR(256),
    created_at BIGINT NOT NULL,
    expires_at BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_dismissed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_notif_player FOREIGN KEY (recipient_player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_notif_recipient ON player_notifications(recipient_player_id);
CREATE INDEX IF NOT EXISTS idx_player_notif_unread ON player_notifications(recipient_player_id, is_read);
CREATE INDEX IF NOT EXISTS idx_player_notif_created ON player_notifications(created_at);

-- 2. Notification Preferences
CREATE TABLE IF NOT EXISTS notification_preferences (
    player_id VARCHAR(64) PRIMARY KEY,
    friend_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    multiplayer_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    daily_reminder_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    daily_reminder_hour INT NOT NULL DEFAULT 9,
    daily_reminder_minute INT NOT NULL DEFAULT 0,
    updated_at BIGINT NOT NULL,
    CONSTRAINT fk_notif_pref_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

-- 3. Device Push Registrations
CREATE TABLE IF NOT EXISTS device_push_registrations (
    registration_id VARCHAR(36) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    device_token VARCHAR(512) NOT NULL,
    platform VARCHAR(32) NOT NULL DEFAULT 'ANDROID',
    registered_at BIGINT NOT NULL,
    last_active_at BIGINT NOT NULL,
    CONSTRAINT uq_player_device_token UNIQUE (player_id, device_token),
    CONSTRAINT fk_push_reg_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_push_reg_player ON device_push_registrations(player_id);

-- 4. Notification Delivery Audit Events
CREATE TABLE IF NOT EXISTS notification_delivery_events (
    event_id VARCHAR(36) PRIMARY KEY,
    notification_id VARCHAR(36),
    recipient_player_id VARCHAR(64) NOT NULL,
    channel VARCHAR(32) NOT NULL,
    status VARCHAR(48) NOT NULL,
    attempted_at BIGINT NOT NULL,
    detail VARCHAR(512)
);

CREATE INDEX IF NOT EXISTS idx_notif_deliv_recipient ON notification_delivery_events(recipient_player_id);

-- 5. Player Privacy Settings
CREATE TABLE IF NOT EXISTS player_privacy_settings (
    player_id VARCHAR(64) PRIMARY KEY,
    profile_visibility VARCHAR(32) NOT NULL DEFAULT 'PUBLIC',
    allow_zynpath_id_search BOOLEAN NOT NULL DEFAULT TRUE,
    allow_friend_requests BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at BIGINT NOT NULL,
    CONSTRAINT chk_profile_visibility CHECK (profile_visibility IN ('PUBLIC', 'FRIENDS_ONLY', 'PRIVATE')),
    CONSTRAINT fk_privacy_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_privacy_visibility ON player_privacy_settings(profile_visibility);

-- 6. Data Export Requests Audit
CREATE TABLE IF NOT EXISTS account_export_requests (
    request_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    schema_version INT NOT NULL DEFAULT 1,
    exported_at BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'COMPLETED',
    CONSTRAINT fk_export_player FOREIGN KEY (player_id) REFERENCES player_accounts(player_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_export_player ON account_export_requests(player_id);

-- 7. Account Deletion Audits (Retained for legal audit without personal data)
CREATE TABLE IF NOT EXISTS account_deletion_audits (
    deletion_id VARCHAR(64) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    public_zynpath_id VARCHAR(32),
    deleted_at BIGINT NOT NULL,
    reason VARCHAR(128) DEFAULT 'USER_REQUESTED'
);

CREATE INDEX IF NOT EXISTS idx_deletion_timestamp ON account_deletion_audits(deleted_at);
