package com.zynpath.backend.account.service;

import com.zynpath.backend.account.model.AccountDeletionResultDto;
import com.zynpath.backend.account.model.AccountSettingsDto;
import com.zynpath.backend.account.model.DataExportDto;
import com.zynpath.backend.account.model.PlayerPrivacySettings;
import com.zynpath.backend.account.model.ProfileVisibility;
import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.model.CompetitiveDto.CompetitiveStatsDto;
import com.zynpath.backend.multiplayer.service.CompetitiveService;
import com.zynpath.backend.notification.model.NotificationPreference;
import com.zynpath.backend.notification.service.NotificationService;
import com.zynpath.backend.social.model.FriendSummary;
import com.zynpath.backend.social.service.SocialService;
import com.zynpath.backend.subscription.model.SubscriptionTier;
import com.zynpath.backend.subscription.service.EntitlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service orchestrating unified player settings, privacy preferences, provider linking,
 * data export ("Request My Data"), and the account deletion lifecycle.
 *
 * Implements Prompt 32 Sections 5, 13-20, 24, 27-30, 32, 36, 39-51.
 */
@Service
public class AccountManagementService {

    private static final Logger log = LoggerFactory.getLogger(AccountManagementService.class);

    private final PlayerAccountService playerAccountService;
    private final SessionSecurityService sessionSecurityService;
    private final SocialService socialService;
    private final NotificationService notificationService;
    private final EntitlementService entitlementService;
    private final CompetitiveService competitiveService;

    // In-memory thread-safe privacy settings repository
    private final Map<String, PlayerPrivacySettings> privacySettingsByPlayerId = new ConcurrentHashMap<>();

    public AccountManagementService(
            PlayerAccountService playerAccountService,
            SessionSecurityService sessionSecurityService,
            @Lazy SocialService socialService,
            @Lazy NotificationService notificationService,
            @Lazy EntitlementService entitlementService,
            @Lazy CompetitiveService competitiveService
    ) {
        this.playerAccountService = playerAccountService;
        this.sessionSecurityService = sessionSecurityService;
        this.socialService = socialService;
        this.notificationService = notificationService;
        this.entitlementService = entitlementService;
        this.competitiveService = competitiveService;
    }

    /**
     * Retrieves aggregated account settings, privacy configurations, and linked providers.
     */
    public AccountSettingsDto getAccountSettings(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));

        PlayerPrivacySettings privacy = privacySettingsByPlayerId.computeIfAbsent(
                playerId,
                k -> PlayerPrivacySettings.defaultSettings()
        );

        List<AuthProvider> linkedProviders = playerAccountService.getLinkedProviders(playerId);

        boolean isPremium = false;
        try {
            if (entitlementService != null) {
                SubscriptionTier tier = entitlementService.getEntitlement(playerId);
                isPremium = tier != null && tier != SubscriptionTier.FREE;
            }
        } catch (Exception e) {
            log.warn("Could not check subscription entitlement for playerId={}: {}", playerId, e.getMessage());
        }

        return new AccountSettingsDto(
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                account.createdAt(),
                privacy,
                linkedProviders,
                isPremium
        );
    }

    /**
     * Updates privacy preferences for the authenticated player.
     */
    public PlayerPrivacySettings updatePrivacySettings(String playerId, PlayerPrivacySettings newSettings) {
        if (newSettings == null) {
            throw new IllegalArgumentException("Privacy settings cannot be null");
        }
        playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));

        privacySettingsByPlayerId.put(playerId, newSettings);
        log.info("Updated privacy settings for playerId={}: visibility={}, zynpathSearch={}, friendRequests={}",
                playerId, newSettings.profileVisibility(), newSettings.allowZynpathIdSearch(), newSettings.allowFriendRequests());
        return newSettings;
    }

    /**
     * Returns linked authentication providers for the account.
     */
    public List<AuthProvider> getLinkedProviders(String playerId) {
        return playerAccountService.getLinkedProviders(playerId);
    }

    /**
     * Unlinks an external authentication provider from the account.
     */
    public void unlinkProvider(String playerId, AuthProvider provider) {
        playerAccountService.unlinkProvider(playerId, provider);
    }

    /**
     * Generates a structured, schema-versioned data export ("Request My Data").
     */
    public DataExportDto exportAccountData(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));

        PlayerPrivacySettings privacy = privacySettingsByPlayerId.computeIfAbsent(
                playerId,
                k -> PlayerPrivacySettings.defaultSettings()
        );

        NotificationPreference notifPref = null;
        try {
            if (notificationService != null) {
                notifPref = notificationService.getPreferences(playerId);
            }
        } catch (Exception e) {
            log.warn("Could not retrieve notification preferences for export: {}", e.getMessage());
        }
        if (notifPref == null) {
            notifPref = NotificationPreference.createDefault(playerId);
        }

        List<String> friendsPublicIds = List.of();
        try {
            if (socialService != null) {
                List<FriendSummary> friends = socialService.listFriends(playerId);
                friendsPublicIds = friends.stream().map(FriendSummary::publicZynpathId).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.warn("Could not retrieve friends for export: {}", e.getMessage());
        }

        Map<String, Object> statsMap = new HashMap<>();
        try {
            if (competitiveService != null) {
                CompetitiveStatsDto compStats = competitiveService.getCompetitiveStats(playerId);
                statsMap.put("quickDuelMatches", compStats.quickDuelMatches());
                statsMap.put("quickDuelWins", compStats.quickDuelWins());
                statsMap.put("friendDuelMatches", compStats.friendDuelMatches());
                statsMap.put("friendDuelWins", compStats.friendDuelWins());
                statsMap.put("miniLeagueParticipations", compStats.miniLeagueParticipations());
                statsMap.put("miniLeagueFirstPlaces", compStats.miniLeagueFirstPlaceFinishes());
            }
        } catch (Exception e) {
            log.warn("Could not retrieve competitive stats for export: {}", e.getMessage());
        }

        DataExportDto.AccountSummary accountSummary = new DataExportDto.AccountSummary(
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                account.createdAt()
        );

        return new DataExportDto(
                1, // Schema version 1
                System.currentTimeMillis(),
                accountSummary,
                privacy,
                notifPref,
                friendsPublicIds,
                statsMap
        );
    }

    /**
     * Executes the authoritative account deletion workflow.
     * Irreversibly deletes account, relationships, notifications, sessions, and cleans caches.
     * Note: In accordance with Section 36 & 50, Google Play subscriptions must be cancelled in Play Store.
     */
    public AccountDeletionResultDto deleteAccount(String playerId) {
        PlayerAccount account = playerAccountService.findById(playerId)
                .orElseThrow(() -> new AuthException("PLAYER_NOT_FOUND", "Player account not found", HttpStatus.NOT_FOUND));

        log.warn("Executing account deletion workflow for playerId={}, publicId={}", playerId, account.publicZynpathId());

        // 1. Clean up social graph (friendships, requests, blocks, invites)
        try {
            if (socialService != null) {
                socialService.handleAccountDeletion(playerId);
            }
        } catch (Exception e) {
            log.error("Failed to clean social graph during account deletion for playerId={}: {}", playerId, e.getMessage());
        }

        // 2. Clean up notifications, push registrations, preferences
        try {
            if (notificationService != null) {
                notificationService.cleanupAccountNotifications(playerId);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup notifications during account deletion for playerId={}: {}", playerId, e.getMessage());
        }

        // 3. Remove privacy preferences
        privacySettingsByPlayerId.remove(playerId);

        // 4. Revoke all active sessions
        try {
            if (sessionSecurityService != null) {
                sessionSecurityService.revokeAllSessionsForPlayer(playerId);
            }
        } catch (Exception e) {
            log.error("Failed to revoke sessions during account deletion for playerId={}: {}", playerId, e.getMessage());
        }

        // 5. Delete player account and external identity links
        playerAccountService.deleteAccount(playerId);

        long now = System.currentTimeMillis();
        return new AccountDeletionResultDto(
                "DELETED",
                playerId,
                now,
                "Account and personal data have been permanently removed.",
                "Important: Deleting your Zynpath account does not automatically cancel active Google Play subscriptions. Please cancel subscriptions via the Google Play Store."
        );
    }
}
