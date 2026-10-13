package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.persistence.entity.ExternalIdentityEntity;
import com.zynpath.backend.persistence.entity.PlayerAccountEntity;
import com.zynpath.backend.persistence.repository.ExternalIdentityRepository;
import com.zynpath.backend.persistence.repository.PlayerAccountRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 17.36A: Real PostgreSQL 18 Guest Identity Binding Integration Tests.
 *
 * Verifies:
 * - Real PostgreSQL composite primary key integrity for ("GUEST", guestUuid)
 * - Existing guest UUID cannot be rebound to another player
 * - Same-owner binding is safe and idempotent
 * - Concurrent bindings across threads simulate multi-instance Cloud Run race conditions
 * - New provider account creation cannot claim another player's guest UUID
 * - Authorized linking succeeds while unauthorized linking fails safely without modifying database state
 */
@SpringBootTest
@ActiveProfiles("postgres")
class PostgresGuestIdentityBindingIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PostgresGuestIdentityBindingIntegrationTest.class);

    private static final String TEST_DB_HOST = "localhost";
    private static final int TEST_DB_PORT = 55432;
    private static final String TEST_DB_NAME = "zynpath_test_isolated";
    private static final String TEST_DB_USER = "zynpath_test";
    private static final String TEST_DB_PASSWORD = "zynpath_local_test_2026";
    private static final String TEST_SCHEMA = "zynpath_app_schema";

    @DynamicPropertySource
    static void configurePostgresProperties(DynamicPropertyRegistry registry) {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String jdbcUrl = "jdbc:postgresql://" + TEST_DB_HOST + ":" + TEST_DB_PORT + "/" + TEST_DB_NAME
                + "?currentSchema=" + TEST_SCHEMA + "&sslmode=disable";

        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> TEST_DB_USER);
        registry.add("spring.datasource.password", () -> TEST_DB_PASSWORD);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> TEST_SCHEMA);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.default-schema", () -> TEST_SCHEMA);
        registry.add("spring.flyway.schemas", () -> TEST_SCHEMA);
        registry.add("spring.flyway.create-schemas", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
        registry.add("spring.flyway.validate-on-migrate", () -> "true");
        registry.add("zynpath.persistence.mode", () -> "jpa");
    }

    @BeforeAll
    static void initUtc() {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Autowired
    private JpaPlayerAccountPersistenceAdapter adapter;

    @Autowired
    private PlayerAccountRepository accountRepository;

    @Autowired
    private ExternalIdentityRepository identityRepository;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private DataSource dataSource;

    private PlayerAccount createTestAccount(String playerId, String publicId, String displayName) {
        long now = System.currentTimeMillis();
        PlayerAccount account = new PlayerAccount(playerId, publicId, displayName, "REGISTERED", now, now);
        adapter.saveAccount(account);
        return account;
    }

    @Test
    @DisplayName("PostgreSQL: Binds new guest UUID to player account and persists row")
    void testPostgres_bindNewGuestUuid_persistsToDatabase() throws Exception {
        String playerId = "p_pg_bind_01_" + UUID.randomUUID().toString().substring(0, 8);
        String publicId = "ZYN-PG01-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        createTestAccount(playerId, publicId, "Postgres Player 1");

        String guestUuid = "guest-pg-uuid-01-" + UUID.randomUUID();
        adapter.bindGuestUuid(guestUuid, playerId);

        // Verify via adapter
        Optional<String> boundPlayer = adapter.findGuestPlayerId(guestUuid);
        assertThat(boundPlayer).contains(playerId);

        // Verify directly via raw JDBC SQL
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT player_id FROM zynpath_app_schema.external_identities WHERE provider = 'GUEST' AND provider_subject_id = ?")) {
            ps.setString(1, guestUuid);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Database row must exist in PostgreSQL");
                assertEquals(playerId, rs.getString("player_id"));
            }
        }
    }

    @Test
    @DisplayName("PostgreSQL: Existing guest UUID cannot be rebound to another player")
    void testPostgres_existingGuestUuidCannotBeReboundToAnotherPlayer() throws Exception {
        String ownerPlayerId = "p_pg_owner_" + UUID.randomUUID().toString().substring(0, 8);
        String attackerPlayerId = "p_pg_attacker_" + UUID.randomUUID().toString().substring(0, 8);
        createTestAccount(ownerPlayerId, "ZYN-OWN1-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(), "Owner");
        createTestAccount(attackerPlayerId, "ZYN-ATK1-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(), "Attacker");

        String guestUuid = "guest-pg-uuid-rebind-" + UUID.randomUUID();
        adapter.bindGuestUuid(guestUuid, ownerPlayerId);
        assertThat(adapter.findGuestPlayerId(guestUuid)).contains(ownerPlayerId);

        // Attacker attempts to rebind the existing guest UUID
        AuthException ex = assertThrows(AuthException.class, () ->
                adapter.bindGuestUuid(guestUuid, attackerPlayerId)
        );
        assertEquals("ACCOUNT_LINK_CONFLICT", ex.getErrorCode());

        // Verify the database row in PostgreSQL was NOT modified
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT player_id FROM zynpath_app_schema.external_identities WHERE provider = 'GUEST' AND provider_subject_id = ?")) {
            ps.setString(1, guestUuid);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(ownerPlayerId, rs.getString("player_id"), "Owner must not have changed in PostgreSQL");
            }
        }
    }

    @Test
    @DisplayName("PostgreSQL: Same-owner binding is safe and idempotent")
    void testPostgres_sameOwnerBindingIsIdempotent() {
        String ownerPlayerId = "p_pg_idem_" + UUID.randomUUID().toString().substring(0, 8);
        createTestAccount(ownerPlayerId, "ZYN-IDEM-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(), "Idempotent User");

        String guestUuid = "guest-pg-uuid-idem-" + UUID.randomUUID();
        adapter.bindGuestUuid(guestUuid, ownerPlayerId);

        // Repeated bindings by the same owner must succeed without throwing
        adapter.bindGuestUuid(guestUuid, ownerPlayerId);
        adapter.bindGuestUuid(guestUuid, ownerPlayerId);

        assertThat(adapter.findGuestPlayerId(guestUuid)).contains(ownerPlayerId);
    }

    @Test
    @DisplayName("PostgreSQL: Concurrent bindings across threads simulate Cloud Run race condition safely")
    void testPostgres_concurrentConflictingBindingsAcrossThreads() throws Exception {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        String contestedGuestUuid = "guest-pg-race-" + UUID.randomUUID();
        List<String> playerIds = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            String pid = "p_pg_race_" + i + "_" + UUID.randomUUID().toString().substring(0, 6);
            createTestAccount(pid, "ZYN-RC" + i + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(), "Racer " + i);
            playerIds.add(pid);
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String candidatePlayerId = playerIds.get(i);
            executor.submit(() -> {
                try {
                    startLatch.await(); // Synchronize all threads to execute simultaneously
                    adapter.bindGuestUuid(contestedGuestUuid, candidatePlayerId);
                    successCount.incrementAndGet();
                } catch (AuthException ex) {
                    if ("ACCOUNT_LINK_CONFLICT".equals(ex.getErrorCode())) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Fire all threads
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "All concurrent threads must finish within timeout");
        executor.shutdown();

        // Exactly one thread must succeed in establishing ownership
        assertEquals(1, successCount.get(), "Exactly one candidate must succeed in binding the guest UUID");
        assertEquals(threadCount - 1, conflictCount.get(), "All other concurrent candidates must receive conflict");

        // Exactly one row exists in PostgreSQL, matching the winning player
        Optional<String> winningPlayerOpt = adapter.findGuestPlayerId(contestedGuestUuid);
        assertTrue(winningPlayerOpt.isPresent());
        assertTrue(playerIds.contains(winningPlayerOpt.get()));
    }

    @Test
    @DisplayName("PostgreSQL: New provider account creation cannot claim another player's guest UUID")
    void testPostgres_newProviderAccountCannotClaimExistingGuestUuid() {
        String victimPlayerId = "p_pg_vic_" + UUID.randomUUID().toString().substring(0, 8);
        createTestAccount(victimPlayerId, "ZYN-VIC1-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(), "Victim");

        String victimGuestUuid = "guest-pg-victim-" + UUID.randomUUID();
        adapter.bindGuestUuid(victimGuestUuid, victimPlayerId);

        // Attacker creates a new Google account and passes victim's guestUuid
        String attackerSub = "google-pg-sub-attacker-" + UUID.randomUUID();
        VerifiedProviderIdentity attackerGoogle = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                attackerSub,
                "Attacker",
                "attacker@example.com",
                true
        );

        PlayerAccount attackerAccount = playerAccountService.getOrCreateForExternalIdentity(attackerGoogle, victimGuestUuid);

        // Attacker account created
        assertThat(attackerAccount.playerId()).isNotEqualTo(victimPlayerId);

        // But victim's guest UUID remains intact in PostgreSQL
        Optional<String> boundOwner = adapter.findGuestPlayerId(victimGuestUuid);
        assertThat(boundOwner).contains(victimPlayerId);
    }

    @Test
    @DisplayName("PostgreSQL: Authorized guest-to-provider linking continues to work")
    void testPostgres_authorizedGuestToProviderLinkingPreservesMapping() {
        PlayerAccount guestAccount = playerAccountService.getOrCreateGuestAccount(null, "PostgresGuest");
        String guestPlayerId = guestAccount.playerId();
        String guestUuid = "guest-pg-link-legit-" + UUID.randomUUID();
        adapter.bindGuestUuid(guestUuid, guestPlayerId);

        String googleSub = "google-pg-sub-legit-" + UUID.randomUUID();
        VerifiedProviderIdentity googleIdentity = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                googleSub,
                "Legit User",
                "legit@example.com",
                true
        );

        PlayerAccount linked = playerAccountService.linkGuestAccount(
                googleIdentity,
                guestUuid,
                "Legit User",
                guestPlayerId
        );

        assertThat(linked.playerId()).isEqualTo(guestPlayerId);
        assertThat(linked.accountType()).isEqualTo("LINKED");
        assertThat(adapter.findGuestPlayerId(guestUuid)).contains(guestPlayerId);
        assertThat(adapter.findExternalIdentity(AuthProvider.GOOGLE, googleSub))
                .isPresent()
                .hasValueSatisfying(id -> assertThat(id.playerId()).isEqualTo(guestPlayerId));
    }

    @Test
    @DisplayName("PostgreSQL: Unauthorized linking fails without altering PostgreSQL identity mappings")
    void testPostgres_unauthorizedLinkingFailsWithoutAlteringDatabase() {
        String victimPlayerId = "p_pg_vic2_" + UUID.randomUUID().toString().substring(0, 8);
        createTestAccount(victimPlayerId, "ZYN-VIC2-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase(), "Victim 2");

        String victimGuestUuid = "guest-pg-unauth-link-" + UUID.randomUUID();
        adapter.bindGuestUuid(victimGuestUuid, victimPlayerId);

        String attackerSub = "google-pg-sub-attacker2-" + UUID.randomUUID();
        VerifiedProviderIdentity attackerGoogle = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                attackerSub,
                "Attacker 2",
                "attacker2@example.com",
                true
        );

        // Linking attempt without session fails with AuthException
        assertThatThrownBy(() -> playerAccountService.linkGuestAccount(
                attackerGoogle,
                victimGuestUuid,
                "Attacker 2",
                null
        )).isInstanceOf(AuthException.class);

        // Linking attempt with wrong session fails with AuthException
        assertThatThrownBy(() -> playerAccountService.linkGuestAccount(
                attackerGoogle,
                victimGuestUuid,
                "Attacker 2",
                "wrong-player-id"
        )).isInstanceOf(AuthException.class);

        // PostgreSQL row remains untouched
        assertThat(adapter.findGuestPlayerId(victimGuestUuid)).contains(victimPlayerId);
        assertThat(adapter.findExternalIdentity(AuthProvider.GOOGLE, attackerSub)).isEmpty();
    }
}
