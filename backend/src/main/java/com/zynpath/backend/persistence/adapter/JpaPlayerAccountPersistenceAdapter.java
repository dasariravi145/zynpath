package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.ExternalIdentity;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.persistence.entity.ExternalIdentityEntity;
import com.zynpath.backend.persistence.entity.ExternalIdentityId;
import com.zynpath.backend.persistence.entity.PlayerAccountEntity;
import com.zynpath.backend.persistence.port.PlayerAccountPersistencePort;
import com.zynpath.backend.persistence.repository.ExternalIdentityRepository;
import com.zynpath.backend.persistence.repository.PlayerAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL JPA adapter for PlayerAccountPersistencePort.
 * Provides durable storage for player accounts, Public Zynpath IDs, and external OAuth identity links.
 * Active when zynpath.persistence.mode=jpa.
 */
@Repository
@ConditionalOnProperty(name = "zynpath.persistence.mode", havingValue = "jpa")
public class JpaPlayerAccountPersistenceAdapter implements PlayerAccountPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(JpaPlayerAccountPersistenceAdapter.class);

    private final PlayerAccountRepository accountRepository;
    private final ExternalIdentityRepository identityRepository;

    public JpaPlayerAccountPersistenceAdapter(
            PlayerAccountRepository accountRepository,
            ExternalIdentityRepository identityRepository
    ) {
        this.accountRepository = accountRepository;
        this.identityRepository = identityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerAccount> findById(String playerId) {
        return accountRepository.findById(playerId)
                .filter(entity -> !entity.isDeleted())
                .map(this::toDomainAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerAccount> findByPublicId(String publicZynpathId) {
        return accountRepository.findByPublicZynpathId(publicZynpathId)
                .filter(entity -> !entity.isDeleted())
                .map(this::toDomainAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPublicId(String publicZynpathId) {
        return accountRepository.existsByPublicZynpathId(publicZynpathId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExternalIdentity> findExternalIdentity(AuthProvider provider, String subjectId) {
        return identityRepository.findByProviderAndProviderSubjectId(provider.name(), subjectId)
                .map(this::toDomainIdentity);
    }

    @Override
    @Transactional
    public void saveAccount(PlayerAccount account) {
        PlayerAccountEntity entity = new PlayerAccountEntity(
                account.playerId(),
                account.publicZynpathId(),
                account.displayName(),
                account.accountType(),
                account.createdAt(),
                account.lastActiveAt(),
                false
        );
        accountRepository.save(entity);
        log.info("PostgreSQL JPA: Saved player account playerId={}, publicId={}", account.playerId(), account.publicZynpathId());
    }

    @Override
    @Transactional
    public void saveExternalIdentity(ExternalIdentity identity) {
        ExternalIdentityEntity entity = new ExternalIdentityEntity(
                identity.provider().name(),
                identity.providerSubjectId(),
                identity.playerId(),
                identity.linkedAt()
        );
        identityRepository.save(entity);
        log.info("PostgreSQL JPA: Linked external identity provider={}, subjectId={} to playerId={}",
                identity.provider(), identity.providerSubjectId(), identity.playerId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthProvider> getLinkedProviders(String playerId) {
        List<ExternalIdentityEntity> links = identityRepository.findByPlayerId(playerId);
        List<AuthProvider> providers = new ArrayList<>();
        for (ExternalIdentityEntity link : links) {
            try {
                if (!"GUEST".equalsIgnoreCase(link.getProvider())) {
                    providers.add(AuthProvider.valueOf(link.getProvider()));
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return providers;
    }

    @Override
    @Transactional
    public void unlinkProvider(String playerId, AuthProvider provider) {
        identityRepository.deleteByPlayerIdAndProvider(playerId, provider.name());
        log.info("PostgreSQL JPA: Unlinked provider {} for playerId={}", provider, playerId);
    }

    @Override
    @Transactional
    public void deleteAccount(String playerId) {
        identityRepository.deleteByPlayerId(playerId);
        accountRepository.findById(playerId).ifPresent(account -> {
            account.setDeleted(true);
            accountRepository.save(account);
        });
        log.info("PostgreSQL JPA: Soft-deleted player account and purged identities for playerId={}", playerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findGuestPlayerId(String guestUuid) {
        return identityRepository.findByProviderAndProviderSubjectId("GUEST", guestUuid)
                .map(ExternalIdentityEntity::getPlayerId);
    }

    @Override
    @Transactional
    public void bindGuestUuid(String guestUuid, String playerId) {
        ExternalIdentityEntity entity = new ExternalIdentityEntity(
                "GUEST",
                guestUuid,
                playerId,
                System.currentTimeMillis()
        );
        identityRepository.save(entity);
    }

    private PlayerAccount toDomainAccount(PlayerAccountEntity entity) {
        return new PlayerAccount(
                entity.getPlayerId(),
                entity.getPublicZynpathId(),
                entity.getDisplayName(),
                entity.getAccountType(),
                entity.getCreatedAt(),
                entity.getLastLoginAt()
        );
    }

    private ExternalIdentity toDomainIdentity(ExternalIdentityEntity entity) {
        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(entity.getProvider());
        } catch (IllegalArgumentException e) {
            provider = AuthProvider.GOOGLE;
        }
        return new ExternalIdentity(
                provider,
                entity.getProviderSubjectId(),
                entity.getPlayerId(),
                entity.getLinkedAt()
        );
    }
}
