package com.zynpath.backend.account.model;

/**
 * Player privacy configuration settings.
 *
 * Implements Prompt 32 Sections 17-20:
 * - Profile visibility tier
 * - Direct Zynpath ID search discovery
 * - Permission to receive friend requests
 */
public record PlayerPrivacySettings(
    ProfileVisibility profileVisibility,
    boolean allowZynpathIdSearch,
    boolean allowFriendRequests
) {
    public static PlayerPrivacySettings defaultSettings() {
        return new PlayerPrivacySettings(ProfileVisibility.PUBLIC, true, true);
    }
}
