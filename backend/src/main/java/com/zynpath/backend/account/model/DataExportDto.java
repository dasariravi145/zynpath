package com.zynpath.backend.account.model;

import com.zynpath.backend.notification.model.NotificationPreference;
import java.util.List;
import java.util.Map;

/**
 * Structured schema-versioned data export model ("Request My Data").
 *
 * Implements Prompt 32 Sections 39-42:
 * - Versioned portable JSON schema
 * - Explicit account, privacy, preference, friend, and statistic records
 * - No private keys or third-party data exposed
 */
public record DataExportDto(
    int schemaVersion,
    long exportedAt,
    AccountSummary account,
    PlayerPrivacySettings privacySettings,
    NotificationPreference notificationPreferences,
    List<String> friendsPublicIds,
    Map<String, Object> statisticsSummary
) {
    public record AccountSummary(
        String playerId,
        String publicZynpathId,
        String displayName,
        String accountType,
        long createdAt
    ) {}
}
