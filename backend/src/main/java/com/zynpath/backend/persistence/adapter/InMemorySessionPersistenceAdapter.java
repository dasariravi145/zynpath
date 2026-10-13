package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.auth.model.PersistedSession;
import com.zynpath.backend.persistence.port.SessionPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adapter for SessionPersistencePort using ConcurrentHashMap.
 * Retained for fast offline testing and development profiles.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemorySessionPersistenceAdapter implements SessionPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(InMemorySessionPersistenceAdapter.class);

    private final Map<String, PersistedSession> sessionsByTokenHash = new ConcurrentHashMap<>();
    private final Map<String, String> tokenHashBySessionId = new ConcurrentHashMap<>();

    @Override
    public void save(PersistedSession session) {
        sessionsByTokenHash.put(session.tokenHash(), session);
        tokenHashBySessionId.put(session.sessionId(), session.tokenHash());
    }

    @Override
    public Optional<PersistedSession> findByTokenHash(String tokenHash) {
        return Optional.ofNullable(sessionsByTokenHash.get(tokenHash));
    }

    @Override
    public void updateLastUsed(String sessionId, long lastUsedAt) {
        String tokenHash = tokenHashBySessionId.get(sessionId);
        if (tokenHash != null) {
            sessionsByTokenHash.computeIfPresent(tokenHash, (k, s) ->
                    new PersistedSession(
                            s.sessionId(),
                            s.tokenHash(),
                            s.playerId(),
                            s.createdAt(),
                            s.expiresAt(),
                            lastUsedAt,
                            s.isRevoked(),
                            s.revokedAt(),
                            s.replacedByTokenHash()
                    )
            );
        }
    }

    @Override
    public void revoke(String sessionId, long revokedAt) {
        String tokenHash = tokenHashBySessionId.get(sessionId);
        if (tokenHash != null) {
            sessionsByTokenHash.computeIfPresent(tokenHash, (k, s) ->
                    new PersistedSession(
                            s.sessionId(),
                            s.tokenHash(),
                            s.playerId(),
                            s.createdAt(),
                            s.expiresAt(),
                            s.lastUsedAt(),
                            true,
                            revokedAt,
                            s.replacedByTokenHash()
                    )
            );
        }
    }

    @Override
    public void rotateSession(String oldSessionId, String oldTokenHash, PersistedSession newSession, long revokedAt) {
        sessionsByTokenHash.computeIfPresent(oldTokenHash, (k, s) ->
                new PersistedSession(
                        s.sessionId(),
                        s.tokenHash(),
                        s.playerId(),
                        s.createdAt(),
                        s.expiresAt(),
                        s.lastUsedAt(),
                        true,
                        revokedAt,
                        newSession.tokenHash()
                )
        );
        save(newSession);
    }

    @Override
    public void revokeAllForPlayer(String playerId, long revokedAt) {
        for (Map.Entry<String, PersistedSession> entry : sessionsByTokenHash.entrySet()) {
            PersistedSession s = entry.getValue();
            if (playerId.equals(s.playerId()) && !s.isRevoked()) {
                sessionsByTokenHash.put(entry.getKey(), new PersistedSession(
                        s.sessionId(),
                        s.tokenHash(),
                        s.playerId(),
                        s.createdAt(),
                        s.expiresAt(),
                        s.lastUsedAt(),
                        true,
                        revokedAt,
                        s.replacedByTokenHash()
                ));
            }
        }
    }

    @Override
    public List<PersistedSession> findByPlayerId(String playerId) {
        return sessionsByTokenHash.values().stream()
                .filter(s -> playerId.equals(s.playerId()))
                .toList();
    }

    @Override
    public int purgeExpiredSessions(long cutoff) {
        int before = sessionsByTokenHash.size();
        sessionsByTokenHash.entrySet().removeIf(e -> e.getValue().expiresAt() < cutoff);
        return before - sessionsByTokenHash.size();
    }
}
