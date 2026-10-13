package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.ExternalIdentity;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.persistence.port.PlayerAccountPersistencePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory adapter for PlayerAccountPersistencePort using ConcurrentHashMap.
 * Retained for fast offline unit testing and development.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "in-memory", matchIfMissing = true)
public class InMemoryPlayerAccountPersistenceAdapter implements PlayerAccountPersistencePort {

    private final Map<String, PlayerAccount> playerAccountsById = new ConcurrentHashMap<>();
    private final Map<String, PlayerAccount> playerAccountsByPublicId = new ConcurrentHashMap<>();
    private final Map<String, ExternalIdentity> externalIdentitiesByKey = new ConcurrentHashMap<>();
    private final Map<String, String> playerIdsByGuestUuid = new ConcurrentHashMap<>();

    private String buildIdentityKey(AuthProvider provider, String subjectId) {
        return provider.name() + ":" + subjectId;
    }

    @Override
    public Optional<PlayerAccount> findById(String playerId) {
        return Optional.ofNullable(playerAccountsById.get(playerId));
    }

    @Override
    public Optional<PlayerAccount> findByPublicId(String publicZynpathId) {
        return Optional.ofNullable(playerAccountsByPublicId.get(publicZynpathId));
    }

    @Override
    public boolean existsByPublicId(String publicZynpathId) {
        return playerAccountsByPublicId.containsKey(publicZynpathId);
    }

    @Override
    public Optional<ExternalIdentity> findExternalIdentity(AuthProvider provider, String subjectId) {
        return Optional.ofNullable(externalIdentitiesByKey.get(buildIdentityKey(provider, subjectId)));
    }

    @Override
    public void saveAccount(PlayerAccount account) {
        playerAccountsById.put(account.playerId(), account);
        playerAccountsByPublicId.put(account.publicZynpathId(), account);
    }

    @Override
    public void saveExternalIdentity(ExternalIdentity identity) {
        externalIdentitiesByKey.put(buildIdentityKey(identity.provider(), identity.providerSubjectId()), identity);
    }

    @Override
    public List<AuthProvider> getLinkedProviders(String playerId) {
        List<AuthProvider> providers = new ArrayList<>();
        for (ExternalIdentity identity : externalIdentitiesByKey.values()) {
            if (identity.playerId().equals(playerId)) {
                providers.add(identity.provider());
            }
        }
        return providers;
    }

    @Override
    public void unlinkProvider(String playerId, AuthProvider provider) {
        externalIdentitiesByKey.entrySet().removeIf(entry ->
                entry.getValue().playerId().equals(playerId) && entry.getValue().provider() == provider
        );
    }

    @Override
    public void deleteAccount(String playerId) {
        PlayerAccount account = playerAccountsById.remove(playerId);
        if (account != null) {
            playerAccountsByPublicId.remove(account.publicZynpathId());
        }
        externalIdentitiesByKey.entrySet().removeIf(entry -> entry.getValue().playerId().equals(playerId));
        playerIdsByGuestUuid.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
    }

    @Override
    public Optional<String> findGuestPlayerId(String guestUuid) {
        return Optional.ofNullable(playerIdsByGuestUuid.get(guestUuid));
    }

    @Override
    public void bindGuestUuid(String guestUuid, String playerId) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new IllegalArgumentException("guestUuid must not be blank");
        }
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("playerId must not be blank");
        }
        playerIdsByGuestUuid.compute(guestUuid, (k, existing) -> {
            if (existing != null && !existing.equals(playerId)) {
                throw AuthException.accountLinkConflict("Guest identity is already bound to another player account");
            }
            return playerId;
        });
    }
}
