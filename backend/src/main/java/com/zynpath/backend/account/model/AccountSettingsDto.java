package com.zynpath.backend.account.model;

import com.zynpath.backend.auth.model.AuthProvider;
import java.util.List;

/**
 * Aggregated account settings and status transport model.
 *
 * Implements Prompt 32 Sections 5, 24, 27, 35:
 * - Current account identity
 * - Privacy settings
 * - Linked authentication providers
 * - Premium entitlement status
 */
public record AccountSettingsDto(
    String playerId,
    String publicZynpathId,
    String displayName,
    String accountType,
    long createdAt,
    PlayerPrivacySettings privacySettings,
    List<AuthProvider> linkedProviders,
    boolean isPremium
) {}
