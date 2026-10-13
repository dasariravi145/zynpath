package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.auth.model.PersistedSession;
import com.zynpath.backend.persistence.entity.PlayerSessionEntity;
import com.zynpath.backend.persistence.port.SessionPersistencePort;
import com.zynpath.backend.persistence.repository.PlayerSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL JPA adapter for SessionPersistencePort.
 * Provides durable storage for hashed session tokens, expirations, and revocations.
 * Active when zynpath.persistence.mode=jpa.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaSessionPersistenceAdapter implements SessionPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaSessionPersistenceAdapter.class);

    private final PlayerSessionRepository sessionRepository;

    public JpaSessionPersistenceAdapter(PlayerSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public void save(PersistedSession session) {
        PlayerSessionEntity entity = toEntity(session);
        sessionRepository.save(entity);
        log.debug("PostgreSQL JPA: Saved session id={}, playerId={}, expiresAt={}",
                session.sessionId(), session.playerId(), session.expiresAt());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersistedSession> findByTokenHash(String tokenHash) {
        return sessionRepository.findByTokenHash(tokenHash)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void updateLastUsed(String sessionId, long lastUsedAt) {
        sessionRepository.findById(sessionId).ifPresent(entity -> {
            entity.setLastUsedAt(lastUsedAt);
            sessionRepository.save(entity);
        });
    }

    @Override
    @Transactional
    public void revoke(String sessionId, long revokedAt) {
        sessionRepository.findById(sessionId).ifPresent(entity -> {
            entity.setRevoked(true);
            entity.setRevokedAt(revokedAt);
            sessionRepository.save(entity);
            log.info("PostgreSQL JPA: Revoked session id={}, playerId={}", entity.getSessionId(), entity.getPlayerId());
        });
    }

    @Override
    @Transactional
    public void rotateSession(String oldSessionId, String oldTokenHash, PersistedSession newSession, long revokedAt) {
        sessionRepository.findById(oldSessionId).ifPresent(oldEntity -> {
            oldEntity.setRevoked(true);
            oldEntity.setRevokedAt(revokedAt);
            oldEntity.setReplacedByTokenHash(newSession.tokenHash());
            sessionRepository.save(oldEntity);
        });
        sessionRepository.save(toEntity(newSession));
        log.info("PostgreSQL JPA: Rotated session oldSessionId={} -> newSessionId={}, playerId={}",
                oldSessionId, newSession.sessionId(), newSession.playerId());
    }

    @Override
    @Transactional
    public void revokeAllForPlayer(String playerId, long revokedAt) {
        int count = sessionRepository.revokeAllForPlayer(playerId, revokedAt);
        log.info("PostgreSQL JPA: Revoked all {} active sessions for playerId={}", count, playerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersistedSession> findByPlayerId(String playerId) {
        return sessionRepository.findByPlayerId(playerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public int purgeExpiredSessions(long cutoff) {
        int purged = sessionRepository.purgeExpiredSessions(cutoff);
        log.info("PostgreSQL JPA: Purged {} expired sessions", purged);
        return purged;
    }

    private PlayerSessionEntity toEntity(PersistedSession domain) {
        return new PlayerSessionEntity(
                domain.sessionId(),
                domain.tokenHash(),
                domain.playerId(),
                domain.createdAt(),
                domain.expiresAt(),
                domain.lastUsedAt(),
                domain.isRevoked(),
                domain.revokedAt(),
                domain.replacedByTokenHash()
        );
    }

    private PersistedSession toDomain(PlayerSessionEntity entity) {
        return new PersistedSession(
                entity.getSessionId(),
                entity.getTokenHash(),
                entity.getPlayerId(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getLastUsedAt(),
                entity.isRevoked(),
                entity.getRevokedAt(),
                entity.getReplacedByTokenHash()
        );
    }
}
