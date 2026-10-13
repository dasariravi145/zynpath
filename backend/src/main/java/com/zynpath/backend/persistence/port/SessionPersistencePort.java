package com.zynpath.backend.persistence.port;

import com.zynpath.backend.auth.model.PersistedSession;

import java.util.List;
import java.util.Optional;

/**
 * Storage-agnostic persistence port for durable player application sessions.
 * Implemented by both PostgreSQL JPA and standalone In-Memory adapters.
 */
public interface SessionPersistencePort {

    void save(PersistedSession session);

    Optional<PersistedSession> findByTokenHash(String tokenHash);

    void updateLastUsed(String sessionId, long lastUsedAt);

    void revoke(String sessionId, long revokedAt);

    void rotateSession(String oldSessionId, String oldTokenHash, PersistedSession newSession, long revokedAt);

    void revokeAllForPlayer(String playerId, long revokedAt);

    List<PersistedSession> findByPlayerId(String playerId);

    int purgeExpiredSessions(long cutoff);
}
