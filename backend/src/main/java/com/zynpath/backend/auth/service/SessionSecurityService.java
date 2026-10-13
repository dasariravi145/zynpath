package com.zynpath.backend.auth.service;

import com.zynpath.backend.auth.model.AuthClaims;
import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PersistedSession;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.persistence.port.SessionPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Issues, validates, rotates, and revokes Zynpath application session tokens.
 *
 * Implements Production Security Controls:
 * - Decoupled from third-party provider tokens.
 * - Cryptographically random tokens (256-bit entropy) with durable PostgreSQL hashing (SHA-256).
 * - Zero implicit guest account backdoors; reject unknown, expired, malformed, or revoked tokens.
 * - Atomic token rotation upon refresh with token replay protection.
 * - Durable logout and revocation persisted across server restarts.
 */
@Service
public class SessionSecurityService implements AuthenticationBoundaryService {

    private static final Logger log = LoggerFactory.getLogger(SessionSecurityService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final long sessionTtlMillis;
    private final PlayerAccountService playerAccountService;
    private final SessionPersistencePort sessionPersistencePort;

    public SessionSecurityService(
            @Value("${zynpath.auth.session.ttl-seconds:2592000}") long ttlSeconds,
            PlayerAccountService playerAccountService,
            SessionPersistencePort sessionPersistencePort
    ) {
        this.sessionTtlMillis = ttlSeconds * 1000L;
        this.playerAccountService = playerAccountService;
        this.sessionPersistencePort = sessionPersistencePort;
    }

    /**
     * Issues a new application session for the given internal playerId.
     * Persists SHA-256 token hash to backing store.
     */
    public PlayerSession createSession(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("playerId cannot be null or blank");
        }

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = "zyn_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String tokenHash = hashToken(token);
        String sessionId = UUID.randomUUID().toString();

        long now = System.currentTimeMillis();
        long expiresAt = now + sessionTtlMillis;

        PersistedSession persisted = new PersistedSession(
                sessionId,
                tokenHash,
                playerId,
                now,
                expiresAt,
                now,
                false,
                null,
                null
        );

        sessionPersistencePort.save(persisted);
        log.debug("Created durable session sessionId={} for playerId={}, expiresAt={}", sessionId, playerId, expiresAt);
        return new PlayerSession(token, playerId, now, expiresAt);
    }

    /**
     * Validates a session token against durable storage.
     * Rejects unknown, malformed, expired, or revoked tokens.
     */
    public PlayerSession validateSession(String token) {
        if (token == null || token.isBlank()) {
            throw AuthException.sessionExpired("Session token is missing");
        }

        String cleanToken = cleanBearerToken(token);
        if (cleanToken == null || cleanToken.isBlank()) {
            throw AuthException.sessionExpired("Session token is malformed");
        }

        String tokenHash = hashToken(cleanToken);
        Optional<PersistedSession> sessionOpt = sessionPersistencePort.findByTokenHash(tokenHash);

        if (sessionOpt.isEmpty()) {
            throw AuthException.sessionExpired("Invalid session token");
        }

        PersistedSession persisted = sessionOpt.get();

        if (persisted.isRevoked()) {
            if (persisted.replacedByTokenHash() != null) {
                log.warn("Replay of rotated token detected for playerId={}, sessionId={}",
                        persisted.playerId(), persisted.sessionId());
            }
            throw AuthException.sessionExpired("Session has been revoked");
        }

        if (persisted.isExpired()) {
            throw AuthException.sessionExpired("Session has expired");
        }

        sessionPersistencePort.updateLastUsed(persisted.sessionId(), System.currentTimeMillis());
        return new PlayerSession(cleanToken, persisted.playerId(), persisted.createdAt(), persisted.expiresAt());
    }

    /**
     * Refreshes an existing valid session with atomic token rotation.
     * Prevents replay of previous or rotated tokens.
     */
    public PlayerSession refreshSession(String token) {
        if (token == null || token.isBlank()) {
            throw AuthException.sessionExpired("Session token is missing");
        }

        String cleanToken = cleanBearerToken(token);
        String oldTokenHash = hashToken(cleanToken);
        Optional<PersistedSession> existingOpt = sessionPersistencePort.findByTokenHash(oldTokenHash);

        if (existingOpt.isEmpty()) {
            throw AuthException.sessionExpired("Invalid session token");
        }

        PersistedSession existing = existingOpt.get();

        if (existing.isRevoked()) {
            if (existing.replacedByTokenHash() != null) {
                log.warn("Replay attack suspected! Revoked token re-used for refresh by playerId={}. Revoking all sessions.",
                        existing.playerId());
                sessionPersistencePort.revokeAllForPlayer(existing.playerId(), System.currentTimeMillis());
            }
            throw AuthException.sessionExpired("Session has been revoked");
        }

        if (existing.isExpired()) {
            throw AuthException.sessionExpired("Session has expired");
        }

        long now = System.currentTimeMillis();
        long newExpiresAt = now + sessionTtlMillis;

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String newToken = "zyn_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String newTokenHash = hashToken(newToken);
        String newSessionId = UUID.randomUUID().toString();

        PersistedSession newSession = new PersistedSession(
                newSessionId,
                newTokenHash,
                existing.playerId(),
                now,
                newExpiresAt,
                now,
                false,
                null,
                null
        );

        sessionPersistencePort.rotateSession(existing.sessionId(), oldTokenHash, newSession, now);
        log.info("Refreshed and rotated session for playerId={}", existing.playerId());

        return new PlayerSession(newToken, existing.playerId(), now, newExpiresAt);
    }

    /**
     * Revokes a session upon explicit player sign-out.
     */
    public void revokeSession(String token) {
        if (token != null && !token.isBlank()) {
            String cleanToken = cleanBearerToken(token);
            String tokenHash = hashToken(cleanToken);
            sessionPersistencePort.findByTokenHash(tokenHash).ifPresent(persisted -> {
                sessionPersistencePort.revoke(persisted.sessionId(), System.currentTimeMillis());
                log.info("Durable logout: revoked session for playerId={}", persisted.playerId());
            });
        }
    }

    /**
     * Revokes all active sessions for a specific player (e.g. account deletion, security reset).
     */
    public void revokeAllSessionsForPlayer(String playerId) {
        if (playerId != null && !playerId.isBlank()) {
            sessionPersistencePort.revokeAllForPlayer(playerId, System.currentTimeMillis());
            log.info("Durable security reset: revoked all sessions for playerId={}", playerId);
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

    public String cleanBearerToken(String token) {
        if (token == null) return null;
        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return trimmed;
    }

    /**
     * Computes SHA-256 hash hex string of the high-entropy random session token.
     */
    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
