package com.zynpath.backend.auth.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.persistence.adapter.InMemoryPlayerAccountPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerAccountServiceTest {

    private InMemoryPlayerAccountPersistenceAdapter persistenceAdapter;
    private PlayerAccountService playerAccountService;

    @BeforeEach
    void setUp() {
        persistenceAdapter = new InMemoryPlayerAccountPersistenceAdapter();
        playerAccountService = new PlayerAccountService(persistenceAdapter);
    }

    @Test
    @DisplayName("Existing guest UUID cannot be rebound to another player in in-memory persistence")
    void existingGuestUuidCannotBeReboundToAnotherPlayer_inMemory() {
        String guestUuid = "guest-uuid-fixed-001";
        String player1 = "player-owner-001";
        String player2 = "player-attacker-002";

        // Initial binding by rightful owner
        persistenceAdapter.bindGuestUuid(guestUuid, player1);
        assertThat(persistenceAdapter.findGuestPlayerId(guestUuid)).contains(player1);

        // Attempted rebinding by another player must fail safely
        assertThatThrownBy(() -> persistenceAdapter.bindGuestUuid(guestUuid, player2))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException authEx = (AuthException) ex;
                    assertThat(authEx.getErrorCode()).isEqualTo("ACCOUNT_LINK_CONFLICT");
                    assertThat(authEx.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                });

        // Identity mapping must remain unmodified
        assertThat(persistenceAdapter.findGuestPlayerId(guestUuid)).contains(player1);
    }

    @Test
    @DisplayName("Same-owner binding remains safe and idempotent in in-memory persistence")
    void sameOwnerBindingRemainsSafeAndIdempotent_inMemory() {
        String guestUuid = "guest-uuid-idempotent-001";
        String player1 = "player-owner-001";

        persistenceAdapter.bindGuestUuid(guestUuid, player1);
        // Repeated binding by same player must succeed without error
        persistenceAdapter.bindGuestUuid(guestUuid, player1);

        assertThat(persistenceAdapter.findGuestPlayerId(guestUuid)).contains(player1);
    }

    @Test
    @DisplayName("New provider account creation cannot claim another player's guest UUID")
    void newProviderAccountCreationCannotClaimAnotherPlayersGuestUuid() {
        String victimGuestUuid = "victim-guest-uuid-001";
        String victimPlayerId = "victim-player-001";

        // Victim owns guest identity
        persistenceAdapter.bindGuestUuid(victimGuestUuid, victimPlayerId);

        // Attacker creates a new Google account and passes victim's guestUuid
        VerifiedProviderIdentity attackerGoogle = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                "attacker-google-sub-001",
                "Attacker",
                "attacker@example.com",
                true
        );

        PlayerAccount attackerAccount = playerAccountService.getOrCreateForExternalIdentity(
                attackerGoogle,
                victimGuestUuid
        );

        // Attacker gets their own account created normally
        assertThat(attackerAccount).isNotNull();
        assertThat(attackerAccount.playerId()).isNotEqualTo(victimPlayerId);
        assertThat(attackerAccount.accountType()).isEqualTo("AUTHENTICATED");

        // BUT victim's guestUuid is NOT bound to attacker and remains owned by victim
        Optional<String> boundOwner = persistenceAdapter.findGuestPlayerId(victimGuestUuid);
        assertThat(boundOwner).contains(victimPlayerId);
        assertThat(boundOwner.get()).isNotEqualTo(attackerAccount.playerId());
    }

    @Test
    @DisplayName("Authorized guest-to-provider linking continues to work")
    void authorizedGuestToProviderLinkingContinuesToWork() {
        // Guest creates guest account
        PlayerAccount guestAccount = playerAccountService.getOrCreateGuestAccount(null, "GuestPlayer");
        String guestPlayerId = guestAccount.playerId();
        String guestUuid = "guest-uuid-legit-001";
        persistenceAdapter.bindGuestUuid(guestUuid, guestPlayerId);

        // Guest links to Google provider identity with valid session proof (callerPlayerId == guestPlayerId)
        VerifiedProviderIdentity googleIdentity = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                "google-legit-sub-001",
                "Verified Google User",
                "user@example.com",
                true
        );

        PlayerAccount linked = playerAccountService.linkGuestAccount(
                googleIdentity,
                guestUuid,
                "Verified Google User",
                guestPlayerId
        );

        assertThat(linked.playerId()).isEqualTo(guestPlayerId);
        assertThat(linked.accountType()).isEqualTo("LINKED");
        assertThat(linked.publicZynpathId()).isEqualTo(guestAccount.publicZynpathId());
        assertThat(persistenceAdapter.findGuestPlayerId(guestUuid)).contains(guestPlayerId);
        assertThat(persistenceAdapter.findExternalIdentity(AuthProvider.GOOGLE, "google-legit-sub-001"))
                .isPresent()
                .hasValueSatisfying(id -> assertThat(id.playerId()).isEqualTo(guestPlayerId));
    }

    @Test
    @DisplayName("Unauthorized linking fails without changing identity mappings")
    void unauthorizedLinkingFailsWithoutChangingIdentityMappings() {
        // Victim owns guest identity
        String guestUuid = "guest-uuid-protected-001";
        String victimPlayerId = "victim-player-001";
        persistenceAdapter.bindGuestUuid(guestUuid, victimPlayerId);

        VerifiedProviderIdentity attackerGoogle = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                "attacker-google-sub-002",
                "Attacker",
                "attacker@example.com",
                true
        );

        // Attempt 1: Linking with no session (null caller)
        assertThatThrownBy(() -> playerAccountService.linkGuestAccount(
                attackerGoogle,
                guestUuid,
                "Attacker",
                null
        )).isInstanceOf(AuthException.class)
          .satisfies(ex -> assertThat(((AuthException) ex).getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));

        // Attempt 2: Linking with different player session
        assertThatThrownBy(() -> playerAccountService.linkGuestAccount(
                attackerGoogle,
                guestUuid,
                "Attacker",
                "attacker-different-player-id"
        )).isInstanceOf(AuthException.class)
          .satisfies(ex -> assertThat(((AuthException) ex).getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));

        // Guest identity mapping must be unmodified
        assertThat(persistenceAdapter.findGuestPlayerId(guestUuid)).contains(victimPlayerId);

        // No external identity link was created for attacker
        assertThat(persistenceAdapter.findExternalIdentity(AuthProvider.GOOGLE, "attacker-google-sub-002")).isEmpty();
    }
}
