package com.zynpath.backend.security.authorization;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.social.service.SocialService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Object-level and resource-level authorization validation service.
 *
 * Implements Prompt 36 Section 17, 18, 20, 21, 22, 23:
 * - Validates ownership of player records, stats, export files, notifications, and profile data.
 * - Protects match and room states against unauthorized observation or mutation.
 * - Enforces block lists and prevents self-directed abuse.
 */
@Service
public class ResourceAuthorizationService {

    private final MatchSessionService matchSessionService;
    private final SocialService socialService;
    private final SecurityAuditLogger auditLogger;

    public ResourceAuthorizationService(
            @Lazy MatchSessionService matchSessionService,
            @Lazy SocialService socialService,
            SecurityAuditLogger auditLogger
    ) {
        this.matchSessionService = matchSessionService;
        this.socialService = socialService;
        this.auditLogger = auditLogger;
    }

    /**
     * Asserts that the currently authenticated player owns the targeted private resource.
     */
    public void assertOwnership(String resourceOwnerPlayerId, String resourceType) {
        String callingPlayerId = SecurityContext.requirePlayerId();
        if (!callingPlayerId.equals(resourceOwnerPlayerId)) {
            auditLogger.recordEvent(
                    SecurityEventType.AUTHORIZATION_DENIED,
                    callingPlayerId,
                    SecurityContext.getClientIp(),
                    resourceType + ":" + resourceOwnerPlayerId,
                    "DENIED",
                    Map.of("reason", "OWNERSHIP_MISMATCH", "owner", resourceOwnerPlayerId)
            );
            throw AuthException.forbidden("ACCESS_DENIED: You do not have permission to access or modify this resource");
        }
    }

    /**
     * Asserts that the calling player is an active registered participant in the specified match.
     */
    public void assertMatchParticipant(String matchId, String playerId) {
        MatchSession session = matchSessionService.getSession(matchId);
        if (session == null || !session.hasParticipant(playerId)) {
            auditLogger.recordEvent(
                    SecurityEventType.AUTHORIZATION_DENIED,
                    playerId,
                    SecurityContext.getClientIp(),
                    "match:" + matchId,
                    "DENIED",
                    Map.of("reason", "NOT_A_MATCH_PARTICIPANT")
            );
            throw AuthException.forbidden("NOT_A_PARTICIPANT: You do not belong to match: " + matchId);
        }
    }

    /**
     * Asserts that an action does not target the actor themselves (e.g. self-invitations, self-friend requests).
     */
    public void assertNotSelfAction(String targetPlayerId, String actionName) {
        String actor = SecurityContext.requirePlayerId();
        if (actor.equals(targetPlayerId)) {
            auditLogger.recordEvent(
                    SecurityEventType.ABUSE_SUSPECTED,
                    actor,
                    SecurityContext.getClientIp(),
                    actionName,
                    "REJECTED",
                    Map.of("reason", "SELF_TARGET_PROHIBITED", "target", targetPlayerId)
            );
            throw AuthException.badRequest("SELF_ACTION_PROHIBITED: Cannot target yourself for " + actionName);
        }
    }

    /**
     * Asserts that neither player has blocked the other.
     */
    public void assertNotBlocked(String playerA, String playerB) {
        if (socialService.isBlocked(playerA, playerB) || socialService.isBlocked(playerB, playerA)) {
            auditLogger.recordEvent(
                    SecurityEventType.AUTHORIZATION_DENIED,
                    playerA,
                    SecurityContext.getClientIp(),
                    "social_action",
                    "BLOCKED",
                    Map.of("reason", "BLOCKED_RELATIONSHIP", "target", playerB)
            );
            throw AuthException.forbidden("ACTION_BLOCKED: Interaction unavailable due to player blocking settings");
        }
    }
}
