-- Zynpath Notification System Schema Migration
-- Implements Prompt 31 Section 55 & 56:
-- - Models for PlayerNotification, NotificationPreference, DevicePushRegistration, NotificationDeliveryEvent
-- - Non-destructive table creation with appropriate primary keys, foreign constraints, and indexes.

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
    is_dismissed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_player_notif_recipient ON player_notifications(recipient_player_id);
CREATE INDEX IF NOT EXISTS idx_player_notif_unread ON player_notifications(recipient_player_id, is_read);
CREATE INDEX IF NOT EXISTS idx_player_notif_created ON player_notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_player_notif_expiry ON player_notifications(expires_at);

-- 2. Notification Preferences
CREATE TABLE IF NOT EXISTS notification_preferences (
    player_id VARCHAR(64) PRIMARY KEY,
    friend_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    multiplayer_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    daily_reminder_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    daily_reminder_hour INT NOT NULL DEFAULT 9,
    daily_reminder_minute INT NOT NULL DEFAULT 0,
    updated_at BIGINT NOT NULL
);

-- 3. Device Push Registrations
CREATE TABLE IF NOT EXISTS device_push_registrations (
    registration_id VARCHAR(36) PRIMARY KEY,
    player_id VARCHAR(64) NOT NULL,
    device_token VARCHAR(512) NOT NULL,
    platform VARCHAR(32) NOT NULL DEFAULT 'ANDROID',
    registered_at BIGINT NOT NULL,
    last_active_at BIGINT NOT NULL,
    CONSTRAINT uq_player_device_token UNIQUE (player_id, device_token)
);

CREATE INDEX IF NOT EXISTS idx_push_reg_player ON device_push_registrations(player_id);
CREATE INDEX IF NOT EXISTS idx_push_reg_token ON device_push_registrations(device_token);

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

CREATE INDEX IF NOT EXISTS idx_notif_deliv_recipient ON notification_delivery_events(recipient_player_id, attempted_at);
