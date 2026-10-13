package com.zynpath.backend.persistence.port;

import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.ExternalIdentity;
import com.zynpath.backend.auth.model.PlayerAccount;

import java.util.List;
import java.util.Optional;

/**
 * Storage-agnostic persistence port for core player accounts and external provider identities.
 */
public interface PlayerAccountPersistencePort {

    Optional<PlayerAccount> findById(String playerId);

    Optional<PlayerAccount> findByPublicId(String publicZynpathId);

    boolean existsByPublicId(String publicZynpathId);

    Optional<ExternalIdentity> findExternalIdentity(AuthProvider provider, String subjectId);

    void saveAccount(PlayerAccount account);

    void saveExternalIdentity(ExternalIdentity identity);

    List<AuthProvider> getLinkedProviders(String playerId);

    void unlinkProvider(String playerId, AuthProvider provider);

    void deleteAccount(String playerId);

    Optional<String> findGuestPlayerId(String guestUuid);

    void bindGuestUuid(String guestUuid, String playerId);
}
