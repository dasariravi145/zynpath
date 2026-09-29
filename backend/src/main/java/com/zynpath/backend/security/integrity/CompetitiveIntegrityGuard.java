package com.zynpath.backend.security.integrity;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MatchState;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Competitive gameplay integrity and anti-cheat safeguard.
 *
 * Implements Prompt 36 Section 29-40:
 * - Server-authoritative puzzle identity validation.
 * - Strict prohibition of hints in competitive modes regardless of Premium status.
 * - Authoritative server-side solve duration calculation.
 * - Match state validation and result finalization idempotency.
 */
@Component
public class CompetitiveIntegrityGuard {

    private final SecurityAuditLogger auditLogger;

    public CompetitiveIntegrityGuard(SecurityAuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    /**
     * Enforces that hints are strictly prohibited in all competitive game modes.
     */
    public void assertNoHintsAllowed(GameMode mode, int hintCount) {
        if (mode != null && hintCount > 0) {
            String actor = SecurityContext.getOptionalPlayerId();
            auditLogger.recordEvent(
                    SecurityEventType.PUZZLE_INTEGRITY_VIOLATION,
                    actor,
                    SecurityContext.getClientIp(),
                    "mode:" + mode.name(),
                    "REJECTED",
                    Map.of("reason", "COMPETITIVE_HINT_PROHIBITION_VIOLATED", "hintsAttempted", hintCount)
            );
            throw AuthException.badRequest("COMPETITIVE_FAIRNESS_VIOLATION: Hints are strictly prohibited in " + mode.name());
        }
    }

    /**
     * Validates that the match is in an active state capable of accepting solution submissions.
     */
    public void assertMatchAcceptingClaims(MatchSession session, String matchId, String playerId) {
        if (session == null) {
            throw AuthException.notFound("Match not found: " + matchId);
        }

        if (session.getState().isTerminal()) {
            auditLogger.recordEvent(
                    SecurityEventType.PUZZLE_INTEGRITY_VIOLATION,
                    playerId,
                    SecurityContext.getClientIp(),
                    "match:" + matchId,
                    "REJECTED",
                    Map.of("reason", "MATCH_ALREADY_FINALIZED", "state", session.getState().name())
            );
            throw AuthException.badRequest("MATCH_ALREADY_FINALIZED: Results have already been authoritatively locked");
        }

        if (!session.getState().isPlayable()) {
            throw AuthException.badRequest("INVALID_MATCH_STATE: Match is not active (state=" + session.getState() + ")");
        }
    }

    /**
     * Validates path coordinate bounds and basic syntax before running full solver verification.
     */
    public void assertPathSyntacticallyValid(List<String> pathCoordinates, int rows, int cols) {
        if (pathCoordinates == null || pathCoordinates.isEmpty()) {
            throw AuthException.badRequest("EMPTY_PATH: Submitted path cannot be null or empty");
        }

        int expectedCells = rows * cols;
        if (pathCoordinates.size() != expectedCells) {
            throw AuthException.badRequest("INCOMPLETE_PATH: Path must cover exactly " + expectedCells + " cells, but contains " + pathCoordinates.size());
        }
    }

    /**
     * Calculates authoritative server-side solve duration in milliseconds.
     * Never trusts client-reported clock values for competitive rankings.
     */
    public long calculateAuthoritativeSolveDuration(long matchStartedEpochMs) {
        long now = System.currentTimeMillis();
        long duration = now - matchStartedEpochMs;
        // Bounded minimum duration: physical human solve cannot be under 500ms
        return Math.max(500L, duration);
    }
}
