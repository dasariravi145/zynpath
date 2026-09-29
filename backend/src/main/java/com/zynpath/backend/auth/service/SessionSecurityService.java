package com.zynpath.backend.auth.service;

import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Issues, validates, and revokes Zynpath application session tokens.
 *
 * Implements Prompt 18 Sections 14, 15, 32 & AuthenticationBoundaryService:
 * - Application sessions are decoupled from third-party provider tokens.
 * - Enforces cryptographically random tokens with TTL and server-side invalidation.
 * - Protects authenticated backend endpoints.
 */
@Service
public class SessionSecurityService implements AuthenticationBoundaryService {

    private static final Logger log = LoggerFactory.getLogger(SessionSecurityService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final long sessionTtlMillis;
    private final PlayerAccountService playerAccountService;
    private final Map<String, PlayerSession> activeSessionsByToken = new ConcurrentHashMap<>();

    public SessionSecurityService(
            @Value("${zynpath.auth.session.ttl-seconds:2592000}") long ttlSeconds,
            PlayerAccountService playerAccountService
    ) {
        this.sessionTtlMillis = ttlSeconds * 1000L;
        this.playerAccountService = playerAccountService;
    }

    /**
     * Issues a new application session for the given internal playerId.
     */
    public PlayerSession createSession(String playerId) {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = "zyn_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        long now = System.currentTimeMillis();
        long expiresAt = now + sessionTtlMillis;

        PlayerSession session = new PlayerSession(token, playerId, now, expiresAt);
        activeSessionsByToken.put(token, session);

        log.debug("Created session for playerId={}, expiresAt={}", playerId, expiresAt);
        return session;
    }

    /**
     * Validates a session token and returns the active session.
     */
    public PlayerSession validateSession(String token) {
        if (token == null || token.isBlank()) {
            throw AuthException.sessionExpired("Session token is missing");
        }

        String cleanToken = cleanBearerToken(token);
        PlayerSession session = activeSessionsByToken.get(cleanToken);

        if (session == null) {
            throw AuthException.sessionExpired("Invalid session token");
        }

        if (session.isExpired()) {
            activeSessionsByToken.remove(cleanToken);
            throw AuthException.sessionExpired("Session has expired");
        }

        return session;
    }

    /**
     * Refreshes an existing valid session, rotating and extending expiration.
     * Implements Prompt 38 Section 30.
     */
    public PlayerSession refreshSession(String token) {
        PlayerSession existing = validateSession(token);
        String cleanToken = cleanBearerToken(token);
        activeSessionsByToken.remove(cleanToken);
        return createSession(existing.playerId());
    }

    /**
     * Revokes a session upon explicit player sign-out.
     */
    public void revokeSession(String token) {
        if (token != null && !token.isBlank()) {
            String cleanToken = cleanBearerToken(token);
            PlayerSession removed = activeSessionsByToken.remove(cleanToken);
            if (removed != null) {
                log.info("Revoked session for playerId={}", removed.playerId());
            }
        }
    }

    /**
     * Revokes all active sessions for a specific player (e.g. upon account deletion or security reset).
     */
    public void revokeAllSessionsForPlayer(String playerId) {
        if (playerId != null && !playerId.isBlank()) {
            activeSessionsByToken.entrySet().removeIf(entry -> playerId.equals(entry.getValue().playerId()));
            log.info("Revoked all active sessions for playerId={}", playerId);
        }
    }

    /**
     * Implementation of AuthenticationBoundaryService for cross-module endpoint authorization.
     */
    @Override
    public AuthClaims verifyBearerToken(String token) {
        if (token == null || token.isBlank()) {
            return new AuthClaims(null, null, "ANONYMOUS", false);
        }

        try {
            PlayerSession session = validateSession(token);
            Optional<PlayerAccount> account = playerAccountService.findById(session.playerId());
            String publicId = account.map(PlayerAccount::publicZynpathId).orElse(session.playerId());

            return new AuthClaims(session.playerId(), publicId, "ZYNPATH_SESSION", true);
        } catch (AuthException e) {
            log.debug("Bearer token verification failed: {}", e.getMessage());
            return new AuthClaims(null, null, "INVALID", false);
        }
    }

    private String cleanBearerToken(String token) {
        if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
            return token.substring(7).trim();
        }
        return token.trim();
    }
}
