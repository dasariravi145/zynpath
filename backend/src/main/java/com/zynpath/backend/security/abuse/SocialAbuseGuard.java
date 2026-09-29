package com.zynpath.backend.security.abuse;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.social.service.SocialService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Social and invitation abuse prevention guard.
 *
 * Implements Prompt 36 Section 18, 20, 21, 22:
 * - Prevents friend request flooding and spam.
 * - Enforces block lists and non-self-interaction rules.
 * - Caps pending outgoing requests per player.
 */
@Component
public class SocialAbuseGuard {

    private static final int MAX_PENDING_OUTGOING_REQUESTS = 50;

    private final SocialService socialService;
    private final SecurityAuditLogger auditLogger;

    public SocialAbuseGuard(
            @Lazy SocialService socialService,
            SecurityAuditLogger auditLogger
    ) {
        this.socialService = socialService;
        this.auditLogger = auditLogger;
    }

    /**
     * Enforces friend request validity, capacity limits, and relationship safeguards.
     */
    public void assertFriendRequestPermitted(String senderPlayerId, String targetPlayerId) {
        if (senderPlayerId.equals(targetPlayerId)) {
            auditLogger.recordEvent(
                    SecurityEventType.INVITATION_SPAM_BLOCKED,
                    senderPlayerId,
                    SecurityContext.getClientIp(),
                    "friend_request:" + targetPlayerId,
                    "REJECTED",
                    Map.of("reason", "SELF_FRIEND_REQUEST")
            );
            throw AuthException.badRequest("SELF_REQUEST_PROHIBITED: You cannot send a friend request to yourself");
        }

        if (socialService.isBlocked(senderPlayerId, targetPlayerId) || socialService.isBlocked(targetPlayerId, senderPlayerId)) {
            auditLogger.recordEvent(
                    SecurityEventType.INVITATION_SPAM_BLOCKED,
                    senderPlayerId,
                    SecurityContext.getClientIp(),
                    "friend_request:" + targetPlayerId,
                    "REJECTED",
                    Map.of("reason", "USER_BLOCKED")
            );
            throw AuthException.forbidden("REQUEST_BLOCKED: Interaction unavailable due to user privacy settings");
        }

        if (socialService.areFriends(senderPlayerId, targetPlayerId)) {
            throw AuthException.badRequest("ALREADY_FRIENDS: You are already friends with this player");
        }

        int activeOutgoing = socialService.listOutgoingRequests(senderPlayerId).size();
        if (activeOutgoing >= MAX_PENDING_OUTGOING_REQUESTS) {
            auditLogger.recordEvent(
                    SecurityEventType.INVITATION_SPAM_BLOCKED,
                    senderPlayerId,
                    SecurityContext.getClientIp(),
                    "friend_request",
                    "RATE_LIMITED",
                    Map.of("reason", "OUTGOING_LIMIT_REACHED", "currentCount", activeOutgoing)
            );
            throw AuthException.badRequest("REQUEST_LIMIT_REACHED: Too many pending outgoing requests. Cancel existing requests before sending new ones.");
        }
    }
}
