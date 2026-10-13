package com.zynpath.backend.auth.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.ExternalIdentity;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.persistence.port.PlayerAccountPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages player account lifecycle, external identity mappings, and Public Zynpath ID issuance.
 *
 * Implements Prompt 18 Sections 12, 13, 20, 22, 24 & 25:
 * - Stable internal UUIDs decoupled from external provider subject IDs and guest UUIDs.
 * - Globally unique Public Zynpath IDs (ZYN-XXXX-YYYY) for safe friend discovery.
 * - Strict uniqueness for (provider, providerSubjectId) external identities.
 * - Safe guest linking with explicit ACCOUNT_LINK_CONFLICT handling when identities are already claimed.
 *
 * Delegates state persistence to PlayerAccountPersistencePort (supporting both PostgreSQL JPA and In-Memory storage).
 */
@Service
public class PlayerAccountService {

    private static final Logger log = LoggerFactory.getLogger(PlayerAccountService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] ID_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray(); // Crockford-style Base32 subset

    private final PlayerAccountPersistencePort persistencePort;

    public PlayerAccountService(PlayerAccountPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Resolves or creates a player account for an authenticated external provider identity.
     */
    public PlayerAccount getOrCreateForExternalIdentity(VerifiedProviderIdentity verifiedIdentity, String optionalGuestUuid) {
        synchronized (this) {
            Optional<ExternalIdentity> existingLinkOpt = persistencePort.findExternalIdentity(
                    verifiedIdentity.provider(),
                    verifiedIdentity.subjectId()
            );

            if (existingLinkOpt.isPresent()) {
                ExternalIdentity existingLink = existingLinkOpt.get();
                Optional<PlayerAccount> accountOpt = persistencePort.findById(existingLink.playerId());
                if (accountOpt.isPresent()) {
                    PlayerAccount account = accountOpt.get();
                    PlayerAccount updated = new PlayerAccount(
                            account.playerId(),
                            account.publicZynpathId(),
                            account.displayName(),
                            account.accountType(),
                            account.createdAt(),
                            System.currentTimeMillis()
                    );
                    persistencePort.saveAccount(updated);
                    return updated;
                }
            }

            // Create new player account
            String playerId = UUID.randomUUID().toString();
            String publicZynpathId = generateUniquePublicZynpathId();
            String displayName = verifiedIdentity.displayName() != null && !verifiedIdentity.displayName().isBlank()
                    ? verifiedIdentity.displayName()
                    : "Player " + publicZynpathId.substring(4, 8);

            long now = System.currentTimeMillis();
            PlayerAccount newAccount = new PlayerAccount(
                    playerId,
                    publicZynpathId,
                    displayName,
                    "AUTHENTICATED",
                    now,
                    now
            );

            ExternalIdentity newLink = new ExternalIdentity(
                    verifiedIdentity.provider(),
                    verifiedIdentity.subjectId(),
                    playerId,
                    now
            );

            persistencePort.saveAccount(newAccount);
            persistencePort.saveExternalIdentity(newLink);

            if (optionalGuestUuid != null && !optionalGuestUuid.isBlank()) {
                persistencePort.bindGuestUuid(optionalGuestUuid, playerId);
            }

            log.info("Created new player account: playerId={}, publicZynpathId={}, provider={}",
                    playerId, publicZynpathId, verifiedIdentity.provider());

            return newAccount;
        }
    }

    /**
     * Resolves or creates an opaque server-recognized guest account.
     * Ensures real Quick Duel matchmaking without requiring third-party authentication.
     */
    /**
     * Resolves or creates an opaque server-recognized guest account.
     * Prevents arbitrary guest identifier impersonation of existing accounts.
     */
    public PlayerAccount getOrCreateGuestAccount(String optionalGuestUuid, String optionalDisplayName) {
        return getOrCreateGuestAccount(optionalGuestUuid, optionalDisplayName, null);
    }

    public PlayerAccount getOrCreateGuestAccount(String optionalGuestUuid, String optionalDisplayName, String optionalCallerPlayerId) {
        synchronized (this) {
            String guestUuid;
            if (optionalGuestUuid != null && !optionalGuestUuid.isBlank()) {
                Optional<String> existingPlayerIdOpt = persistencePort.findGuestPlayerId(optionalGuestUuid);
                if (existingPlayerIdOpt.isPresent()) {
                    // Only return existing account if the caller has verified possession via active session
                    if (optionalCallerPlayerId != null && optionalCallerPlayerId.equals(existingPlayerIdOpt.get())) {
                        Optional<PlayerAccount> accountOpt = persistencePort.findById(existingPlayerIdOpt.get());
                        if (accountOpt.isPresent()) {
                            return accountOpt.get();
                        }
                    } else {
                        log.warn("Unverified guestUuid access attempt rejected for existing account. Generating new guest identity.");
                    }
                }
            }

            // Generate fresh, cryptographically isolated guest identity
            guestUuid = UUID.randomUUID().toString();
            String playerId = UUID.randomUUID().toString();
            String publicZynpathId = generateUniquePublicZynpathId();
            String displayName = (optionalDisplayName != null && !optionalDisplayName.isBlank())
                    ? optionalDisplayName
                    : "Guest_" + publicZynpathId.substring(4, 8);

            long now = System.currentTimeMillis();
            PlayerAccount newAccount = new PlayerAccount(
                    playerId,
                    publicZynpathId,
                    displayName,
                    "GUEST",
                    now,
                    now
            );

            persistencePort.saveAccount(newAccount);
            persistencePort.bindGuestUuid(guestUuid, playerId);

            log.info("Resolved guest player account: playerId={}, publicZynpathId={}, displayName={}",
                    playerId, publicZynpathId, displayName);
            return newAccount;
        }
    }

    /**
     * Links an existing guest identity to an external provider identity.
     * Requires verified proof of ownership for existing accounts.
     * Throws ACCOUNT_LINK_CONFLICT if provider identity is already linked to another Zynpath account.
     */
    public PlayerAccount linkGuestAccount(VerifiedProviderIdentity verifiedIdentity, String guestUuid, String displayName) {
        return linkGuestAccount(verifiedIdentity, guestUuid, displayName, null);
    }

    public PlayerAccount linkGuestAccount(VerifiedProviderIdentity verifiedIdentity, String guestUuid, String displayName, String optionalCallerPlayerId) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new IllegalArgumentException("Guest UUID is required for account linking");
        }

        synchronized (this) {
            Optional<ExternalIdentity> existingLinkOpt = persistencePort.findExternalIdentity(
                    verifiedIdentity.provider(),
                    verifiedIdentity.subjectId()
            );

            if (existingLinkOpt.isPresent()) {
                ExternalIdentity existingLink = existingLinkOpt.get();
                Optional<PlayerAccount> existingAccount = persistencePort.findById(existingLink.playerId());
                log.warn("Account link conflict: Provider identity {}:{} already linked to playerId={}",
                        verifiedIdentity.provider(), verifiedIdentity.subjectId(), existingLink.playerId());
                throw AuthException.accountLinkConflict(
                        "This " + verifiedIdentity.provider() + " account is already linked to another Zynpath player (" +
                                (existingAccount.isPresent() ? existingAccount.get().publicZynpathId() : "existing") + ")."
                );
            }

            Optional<String> existingPlayerIdOpt = persistencePort.findGuestPlayerId(guestUuid);

            // Security verification: If guestUuid belongs to an existing account, caller must be authenticated as that account
            if (existingPlayerIdOpt.isPresent()) {
                String existingPlayerId = existingPlayerIdOpt.get();
                if (optionalCallerPlayerId == null || !optionalCallerPlayerId.equals(existingPlayerId)) {
                    log.warn("Unauthorized attempt to link existing guest account playerId={} without valid session", existingPlayerId);
                    throw AuthException.invalidCredential("Valid session token required to link existing guest account");
                }
            }

            String playerId = existingPlayerIdOpt.orElseGet(() ->
                    (optionalCallerPlayerId != null && !optionalCallerPlayerId.isBlank())
                            ? optionalCallerPlayerId
                            : UUID.randomUUID().toString()
            );
            String publicZynpathId = generateUniquePublicZynpathId();

            String effectiveName = displayName != null && !displayName.isBlank()
                    ? displayName
                    : (verifiedIdentity.displayName() != null ? verifiedIdentity.displayName() : "Player " + publicZynpathId.substring(4, 8));

            long now = System.currentTimeMillis();
            PlayerAccount linkedAccount = new PlayerAccount(
                    playerId,
                    publicZynpathId,
                    effectiveName,
                    "LINKED",
                    now,
                    now
            );

            ExternalIdentity newLink = new ExternalIdentity(
                    verifiedIdentity.provider(),
                    verifiedIdentity.subjectId(),
                    playerId,
                    now
            );

            persistencePort.saveAccount(linkedAccount);
            persistencePort.saveExternalIdentity(newLink);
            persistencePort.bindGuestUuid(guestUuid, playerId);

            log.info("Successfully linked guestUuid={} to provider={} playerId={}",
                    guestUuid, verifiedIdentity.provider(), playerId);

            return linkedAccount;
        }
    }

    /**
     * Retrieves an account by internal playerId.
     */
    public Optional<PlayerAccount> findById(String playerId) {
        return persistencePort.findById(playerId);
    }

    /**
     * Retrieves an account by publicZynpathId.
     */
    public Optional<PlayerAccount> findByPublicId(String publicZynpathId) {
        return persistencePort.findByPublicId(publicZynpathId);
    }

    /**
     * Retrieves all external authentication providers linked to a player account.
     */
    public List<AuthProvider> getLinkedProviders(String playerId) {
        return persistencePort.getLinkedProviders(playerId);
    }

    /**
     * Unlinks an external authentication provider.
     * Prevents unlinking if the provider is the player's last remaining authentication method.
     */
    public void unlinkProvider(String playerId, AuthProvider provider) {
        synchronized (this) {
            List<AuthProvider> current = getLinkedProviders(playerId);
            if (current.size() <= 1 && current.contains(provider)) {
                throw new AuthException("LAST_SIGN_IN_METHOD", "Cannot unlink your only sign-in method", HttpStatus.BAD_REQUEST);
            }
            persistencePort.unlinkProvider(playerId, provider);
            log.info("Unlinked provider {} for playerId={}", provider, playerId);
        }
    }

    /**
     * Deletes a player account and associated external identity mappings.
     */
    public void deleteAccount(String playerId) {
        synchronized (this) {
            persistencePort.deleteAccount(playerId);
            log.info("Permanently deleted player account and identity links for playerId={}", playerId);
        }
    }

    /**
     * Generates a unique, collision-resistant Public Zynpath ID in format ZYN-XXXX-YYYY.
     * Avoids confusing characters (0, O, 1, I).
     */
    private String generateUniquePublicZynpathId() {
        while (true) {
            StringBuilder sb = new StringBuilder("ZYN-");
            for (int i = 0; i < 4; i++) {
                sb.append(ID_ALPHABET[RANDOM.nextInt(ID_ALPHABET.length)]);
            }
            sb.append("-");
            for (int i = 0; i < 4; i++) {
                sb.append(ID_ALPHABET[RANDOM.nextInt(ID_ALPHABET.length)]);
            }
            String candidate = sb.toString();
            if (!persistencePort.existsByPublicId(candidate)) {
                return candidate;
            }
        }
    }
}
