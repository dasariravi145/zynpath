package com.zynpath.backend.auth.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.ExternalIdentity;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;

/**
 * Manages player account lifecycle, external identity mappings, and Public Zynpath ID issuance.
 *
 * Implements Prompt 18 Sections 12, 13, 20, 22, 24 & 25:
 * - Stable internal UUIDs decoupled from external provider subject IDs and guest UUIDs.
 * - Globally unique Public Zynpath IDs (ZYN-XXXX-YYYY) for safe friend discovery.
 * - Strict uniqueness for (provider, providerSubjectId) external identities.
 * - Safe guest linking with explicit ACCOUNT_LINK_CONFLICT handling when identities are already claimed.
 */
@Service
public class PlayerAccountService {

    private static final Logger log = LoggerFactory.getLogger(PlayerAccountService.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] ID_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray(); // Crockford-style Base32 subset

    // In-memory repositories for modular monolith (thread-safe, ready for DB migration)
    private final Map<String, PlayerAccount> playerAccountsById = new ConcurrentHashMap<>();
    private final Map<String, PlayerAccount> playerAccountsByPublicId = new ConcurrentHashMap<>();
    private final Map<String, ExternalIdentity> externalIdentitiesByKey = new ConcurrentHashMap<>();
    private final Map<String, String> playerIdsByGuestUuid = new ConcurrentHashMap<>();

    /**
     * Resolves or creates a player account for an authenticated external provider identity.
     */
    public PlayerAccount getOrCreateForExternalIdentity(VerifiedProviderIdentity verifiedIdentity, String optionalGuestUuid) {
        String identityKey = buildIdentityKey(verifiedIdentity.provider(), verifiedIdentity.subjectId());

        synchronized (this) {
            ExternalIdentity existingLink = externalIdentitiesByKey.get(identityKey);
            if (existingLink != null) {
                PlayerAccount account = playerAccountsById.get(existingLink.playerId());
                if (account != null) {
                    PlayerAccount updated = new PlayerAccount(
                            account.playerId(),
                            account.publicZynpathId(),
                            account.displayName(),
                            account.accountType(),
                            account.createdAt(),
                            System.currentTimeMillis()
                    );
                    playerAccountsById.put(updated.playerId(), updated);
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

            playerAccountsById.put(playerId, newAccount);
            playerAccountsByPublicId.put(publicZynpathId, newAccount);
            externalIdentitiesByKey.put(identityKey, newLink);

            if (optionalGuestUuid != null && !optionalGuestUuid.isBlank()) {
                playerIdsByGuestUuid.put(optionalGuestUuid, playerId);
            }

            log.info("Created new player account: playerId={}, publicZynpathId={}, provider={}",
                    playerId, publicZynpathId, verifiedIdentity.provider());

            return newAccount;
        }
    }

    /**
     * Links an existing guest identity to an external provider identity.
     * Throws ACCOUNT_LINK_CONFLICT if provider identity is already linked to another Zynpath account.
     */
    public PlayerAccount linkGuestAccount(VerifiedProviderIdentity verifiedIdentity, String guestUuid, String displayName) {
        if (guestUuid == null || guestUuid.isBlank()) {
            throw new IllegalArgumentException("Guest UUID is required for account linking");
        }

        String identityKey = buildIdentityKey(verifiedIdentity.provider(), verifiedIdentity.subjectId());

        synchronized (this) {
            ExternalIdentity existingLink = externalIdentitiesByKey.get(identityKey);
            if (existingLink != null) {
                // Identity is already linked to another player account - report conflict
                PlayerAccount existingAccount = playerAccountsById.get(existingLink.playerId());
                log.warn("Account link conflict: Provider identity {} already linked to playerId={}",
                        identityKey, existingLink.playerId());
                throw AuthException.accountLinkConflict(
                        "This " + verifiedIdentity.provider() + " account is already linked to another Zynpath player (" +
                                (existingAccount != null ? existingAccount.publicZynpathId() : "existing") + ")."
                );
            }

            // Check if guest already has a recorded playerId
            String existingPlayerId = playerIdsByGuestUuid.get(guestUuid);
            String playerId = existingPlayerId != null ? existingPlayerId : UUID.randomUUID().toString();
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

            playerAccountsById.put(playerId, linkedAccount);
            playerAccountsByPublicId.put(publicZynpathId, linkedAccount);
            externalIdentitiesByKey.put(identityKey, newLink);
            playerIdsByGuestUuid.put(guestUuid, playerId);

            log.info("Successfully linked guestUuid={} to provider={} playerId={}",
                    guestUuid, verifiedIdentity.provider(), playerId);

            return linkedAccount;
        }
    }

    /**
     * Retrieves an account by internal playerId.
     */
    public Optional<PlayerAccount> findById(String playerId) {
        return Optional.ofNullable(playerAccountsById.get(playerId));
    }

    /**
     * Retrieves an account by publicZynpathId.
     */
    public Optional<PlayerAccount> findByPublicId(String publicZynpathId) {
        return Optional.ofNullable(playerAccountsByPublicId.get(publicZynpathId));
    }

    /**
     * Retrieves all external authentication providers linked to a player account.
     */
    public List<AuthProvider> getLinkedProviders(String playerId) {
        List<AuthProvider> providers = new ArrayList<>();
        for (ExternalIdentity identity : externalIdentitiesByKey.values()) {
            if (identity.playerId().equals(playerId)) {
                providers.add(identity.provider());
            }
        }
        return providers;
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
            boolean removed = externalIdentitiesByKey.entrySet().removeIf(entry ->
                entry.getValue().playerId().equals(playerId) && entry.getValue().provider() == provider
            );
            if (removed) {
                log.info("Unlinked provider {} for playerId={}", provider, playerId);
            }
        }
    }

    /**
     * Deletes a player account and associated external identity mappings.
     */
    public void deleteAccount(String playerId) {
        synchronized (this) {
            PlayerAccount account = playerAccountsById.remove(playerId);
            if (account != null) {
                playerAccountsByPublicId.remove(account.publicZynpathId());
            }
            externalIdentitiesByKey.entrySet().removeIf(entry -> entry.getValue().playerId().equals(playerId));
            playerIdsByGuestUuid.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
            log.info("Permanently deleted player account and identity links for playerId={}", playerId);
        }
    }

    private String buildIdentityKey(AuthProvider provider, String subjectId) {
        return provider.name() + ":" + subjectId;
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
            if (!playerAccountsByPublicId.containsKey(candidate)) {
                return candidate;
            }
        }
    }
}
