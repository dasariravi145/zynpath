package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.ads.model.RewardGrantCommand;
import com.zynpath.backend.ads.model.RewardGrantResult;
import com.zynpath.backend.ads.model.RewardGrantStatus;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.ads.model.RewardVerificationStatus;
import com.zynpath.backend.persistence.entity.AdMobSsvRecordEntity;
import com.zynpath.backend.persistence.entity.PendingRewardClaimEntity;
import com.zynpath.backend.persistence.entity.PlayerAccountEntity;
import com.zynpath.backend.persistence.entity.PlayerHintBalanceEntity;
import com.zynpath.backend.persistence.entity.RewardEventEntity;
import com.zynpath.backend.persistence.repository.AdMobSsvRecordRepository;
import com.zynpath.backend.persistence.repository.PendingRewardClaimRepository;
import com.zynpath.backend.persistence.repository.PlayerAccountRepository;
import com.zynpath.backend.persistence.repository.PlayerHintBalanceRepository;
import com.zynpath.backend.persistence.repository.RewardEventRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step 17.15: Real PostgreSQL 18 Reward Transaction Integration Testing.
 *
 * Verifies transaction atomicity, PESSIMISTIC_WRITE row locking, duplicate prevention,
 * and concurrent callback handling against an isolated PostgreSQL 18 container instance.
 */
@SpringBootTest
@ActiveProfiles("postgres")
class PostgresRewardTransactionIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(PostgresRewardTransactionIntegrationTest.class);

    private static final String TEST_DB_HOST = "localhost";
    private static final int TEST_DB_PORT = 55432;
    private static final String TEST_DB_NAME = "zynpath_test_isolated";
    private static final String TEST_DB_USER = "zynpath_test";
    private static final String TEST_DB_PASSWORD = "zynpath_local_test_2026";
    private static final String TEST_SCHEMA = "zynpath_app_schema";

    @DynamicPropertySource
    static void configurePostgresProperties(DynamicPropertyRegistry registry) {
        // Enforce UTC timezone for JVM to prevent timezone discrepancies
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
    private DataSource dataSource;

    @Autowired
    private JpaAdRewardPersistenceAdapter adapter;

    @Autowired
    private PlayerHintBalanceRepository balanceRepository;

    @Autowired
    private PendingRewardClaimRepository claimRepository;

    @Autowired
    private AdMobSsvRecordRepository ssvRepository;

    @Autowired
    private RewardEventRepository eventRepository;

    @Autowired
    private PlayerAccountRepository playerAccountRepository;

    @BeforeEach
    void verifyIsolationAndFailClosed() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            String url = meta.getURL();
            String user = meta.getUserName();
            String productVersion = meta.getDatabaseProductVersion();

            log.info("Verified PostgreSQL Connection: URL={}, user={}, version={}", url, user, productVersion);

            // Strict safety check: Fail closed if not pointing to isolated test DB
            if (!url.contains(String.valueOf(TEST_DB_PORT)) || !url.contains(TEST_DB_NAME)) {
                throw new IllegalStateException("FAIL CLOSED: Non-test database detected! URL=" + url);
            }

            assertTrue(productVersion.contains("18"), "Must run against PostgreSQL 18, got: " + productVersion);
        }
    }

    private String seedPlayer(String prefix) {
        String playerId = prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
        String publicId = "ZYN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        playerAccountRepository.save(new PlayerAccountEntity(
                playerId,
                publicId,
                "Test Player " + prefix,
                "REGISTERED",
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                false
        ));
        return playerId;
    }

    private RewardGrantCommand createCommand(String playerId, String claimId, String txnId, String adUnitId) {
        return createCommand(playerId, claimId, txnId, adUnitId, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private RewardGrantCommand createCommand(String playerId, String claimId, String txnId, String adUnitId, int maxDaily, int maxStored) {
        return new RewardGrantCommand(
                playerId,
                claimId,
                txnId,
                adUnitId,
                1,
                "key_pg18_test",
                "sig_pg18_test",
                "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}",
                "ad_unit=" + adUnitId + "&custom_data=...",
                maxDaily,
                maxStored,
                1,
                System.currentTimeMillis()
        );
    }

    @Test
    @DisplayName("0. Confirm PostgreSQL 18 version and Flyway V12 migration status on real database")
    void confirmPostgres18AndFlywayV12Applied() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Confirm PostgreSQL version
            try (ResultSet rs = stmt.executeQuery("SELECT version();")) {
                assertTrue(rs.next());
                String version = rs.getString(1);
                log.info("Running on real PostgreSQL engine: {}", version);
                assertTrue(version.contains("PostgreSQL 18"), "Expected PostgreSQL 18, found: " + version);
            }

            // 2. Confirm Flyway migrations applied through V12
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT version, description, success FROM " + TEST_SCHEMA + ".flyway_schema_history WHERE version = '12'")) {
                assertTrue(rs.next(), "Flyway migration V12 record must exist");
                assertEquals("12", rs.getString("version"));
                assertTrue(rs.getBoolean("success"), "Flyway V12 must have been applied successfully");
            }
        }
    }

    @Test
    @DisplayName("1. First-time player balance initialization succeeds in real PostgreSQL")
    void firstTimePlayer_balanceInitialization_succeeds() {
        String playerId = seedPlayer("pg18_first_time");
        String claimId = "claim_init_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId = "tx_init_" + UUID.randomUUID().toString().substring(0, 8);

        RewardGrantCommand command = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_001");

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertTrue(result.isSuccess(), "Grant must succeed for first-time player");
        assertNotNull(result.updatedBalance());
        assertEquals(1, result.updatedBalance().rewardedCredits());
        assertEquals(4, result.updatedBalance().totalAvailable(), "Default 3 free hints + 1 rewarded = 4");

        // Verify state in PostgreSQL
        PlayerHintBalanceEntity balance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(3, balance.getFreeHintsRemaining());
        assertEquals(1, balance.getRewardedCredits());
        assertEquals(4, balance.getTotalAvailable());

        assertTrue(ssvRepository.findById(txnId).isPresent(), "SSV record must be persisted in PostgreSQL");
        assertTrue(eventRepository.findByTransactionId(txnId).isPresent(), "Reward event must be persisted in PostgreSQL");
        assertTrue(claimRepository.findById(claimId).isPresent(), "Claim must be persisted in PostgreSQL");
        assertEquals(RewardVerificationStatus.SERVER_VERIFIED.name(), claimRepository.findById(claimId).get().getStatus());
    }

    @Test
    @DisplayName("2. Two concurrent callbacks for the same player and same transaction ID grant exactly one reward")
    void twoConcurrentCallbacks_samePlayerAndSameTxnId_grantsExactlyOneReward() throws Exception {
        String playerId = seedPlayer("pg18_conc_dup");
        String claimId = "claim_cdup_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId = "tx_cdup_" + UUID.randomUUID().toString().substring(0, 8);

        RewardGrantCommand command = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_001");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger duplicateCount = new AtomicInteger(0);
        List<RewardGrantResult> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    RewardGrantResult result = adapter.grantRewardAtomic(command);
                    results.add(result);
                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                    } else if (result.status() == RewardGrantStatus.DUPLICATE_TRANSACTION) {
                        duplicateCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    log.error("Concurrent task threw error: {}", e.getMessage(), e);
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(1, successCount.get(), "Exactly one concurrent callback must succeed");
        assertEquals(1, duplicateCount.get(), "Duplicate concurrent callback must be rejected as DUPLICATE_TRANSACTION");

        // Verify PostgreSQL final state: balance must have exactly 1 credit, not 2
        PlayerHintBalanceEntity balance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(1, balance.getRewardedCredits(), "Rewarded credits in PostgreSQL must be exactly 1");

        // Exactly one SSV record and one reward event in PostgreSQL
        assertTrue(ssvRepository.findById(txnId).isPresent());
        assertTrue(eventRepository.findByTransactionId(txnId).isPresent());
    }

    @Test
    @DisplayName("3. Two different valid transaction IDs for the same player grant correct credits without lost updates")
    void twoDifferentValidTxnIds_samePlayer_grantsCorrectCreditsWithoutLostUpdates() throws Exception {
        String playerId = seedPlayer("pg18_conc_diff");
        String claim1 = "claim_diff1_" + UUID.randomUUID().toString().substring(0, 8);
        String txn1 = "tx_diff1_" + UUID.randomUUID().toString().substring(0, 8);
        String claim2 = "claim_diff2_" + UUID.randomUUID().toString().substring(0, 8);
        String txn2 = "tx_diff2_" + UUID.randomUUID().toString().substring(0, 8);

        RewardGrantCommand cmd1 = createCommand(playerId, claim1, txn1, "admob_unit_rewarded_001");
        RewardGrantCommand cmd2 = createCommand(playerId, claim2, txn2, "admob_unit_rewarded_001");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        List<RewardGrantResult> results = new CopyOnWriteArrayList<>();

        executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                RewardGrantResult r1 = adapter.grantRewardAtomic(cmd1);
                results.add(r1);
                if (r1.isSuccess()) successCount.incrementAndGet();
            } catch (Exception e) {
                log.error("Task 1 failed: {}", e.getMessage(), e);
            }
        });

        executor.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                RewardGrantResult r2 = adapter.grantRewardAtomic(cmd2);
                results.add(r2);
                if (r2.isSuccess()) successCount.incrementAndGet();
            } catch (Exception e) {
                log.error("Task 2 failed: {}", e.getMessage(), e);
            }
        });

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(2, successCount.get(), "Both distinct transactions must succeed");

        // Verify PostgreSQL final state: balance must have exactly 2 credits (no lost updates under row lock)
        PlayerHintBalanceEntity balance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(2, balance.getRewardedCredits(), "Rewarded credits must be exactly 2 (lost update prevented)");
        assertEquals(5, balance.getTotalAvailable(), "Total available must be 3 + 2 = 5");

        assertTrue(ssvRepository.findById(txn1).isPresent());
        assertTrue(ssvRepository.findById(txn2).isPresent());
    }

    @Test
    @DisplayName("4. A duplicate transaction is rejected without increasing the balance")
    void duplicateTransaction_rejectedWithoutIncreasingBalance() {
        String playerId = seedPlayer("pg18_dup_txn");
        String claimId = "claim_dt_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId = "tx_dt_" + UUID.randomUUID().toString().substring(0, 8);

        RewardGrantCommand command1 = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_001");
        RewardGrantResult result1 = adapter.grantRewardAtomic(command1);
        assertTrue(result1.isSuccess());

        // Second call with same txnId (even different claimId)
        String claimId2 = "claim_dt2_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand command2 = createCommand(playerId, claimId2, txnId, "admob_unit_rewarded_001");
        RewardGrantResult result2 = adapter.grantRewardAtomic(command2);

        assertFalse(result2.isSuccess());
        assertEquals(RewardGrantStatus.DUPLICATE_TRANSACTION, result2.status());

        // Balance in PostgreSQL must still be 1
        PlayerHintBalanceEntity balance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(1, balance.getRewardedCredits());
    }

    @Test
    @DisplayName("5. A duplicate claim cannot grant a second reward")
    void duplicateClaim_cannotGrantSecondReward() {
        String playerId = seedPlayer("pg18_dup_claim");
        String claimId = "claim_dc_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId1 = "tx_dc1_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId2 = "tx_dc2_" + UUID.randomUUID().toString().substring(0, 8);

        RewardGrantCommand command1 = createCommand(playerId, claimId, txnId1, "admob_unit_rewarded_001");
        RewardGrantResult result1 = adapter.grantRewardAtomic(command1);
        assertTrue(result1.isSuccess());

        // Attempt second callback with different txnId but already-verified claimId
        RewardGrantCommand command2 = createCommand(playerId, claimId, txnId2, "admob_unit_rewarded_001");
        RewardGrantResult result2 = adapter.grantRewardAtomic(command2);

        assertFalse(result2.isSuccess());
        assertEquals(RewardGrantStatus.DUPLICATE_CLAIM_ALREADY_VERIFIED, result2.status());

        // Balance remains 1
        PlayerHintBalanceEntity balance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(1, balance.getRewardedCredits());
    }

    @Test
    @DisplayName("6. Ownership mismatch does not modify or reject the legitimate player's pending claim")
    void ownershipMismatch_doesNotModifyOrRejectLegitimatePlayerClaim() {
        String alice = seedPlayer("pg18_alice_own");
        String bob = seedPlayer("pg18_bob_own");

        String claimId = "claim_alice_" + UUID.randomUUID().toString().substring(0, 8);
        long now = System.currentTimeMillis();

        // Alice registers legitimate pending claim
        PendingRewardClaimEntity aliceClaim = new PendingRewardClaimEntity(
                claimId,
                alice,
                "admob_unit_rewarded_001",
                1,
                "SOLO_HINT",
                RewardVerificationStatus.PENDING_VERIFICATION.name(),
                null,
                now,
                null,
                null,
                null
        );
        claimRepository.save(aliceClaim);

        // Bob attempts SSV callback claiming Alice's claimId
        String bobTxnId = "tx_bob_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand bobCommand = createCommand(bob, claimId, bobTxnId, "admob_unit_rewarded_001");

        RewardGrantResult bobResult = adapter.grantRewardAtomic(bobCommand);

        assertFalse(bobResult.isSuccess());
        assertEquals(RewardGrantStatus.OWNERSHIP_MISMATCH, bobResult.status());

        // Verify Alice's claim in PostgreSQL was NOT modified or poisoned to REJECTED
        PendingRewardClaimEntity reloadedClaim = claimRepository.findById(claimId).orElseThrow();
        assertEquals(RewardVerificationStatus.PENDING_VERIFICATION.name(), reloadedClaim.getStatus());
        assertEquals(alice, reloadedClaim.getPlayerId());

        // Now Alice's legitimate callback arrives: must succeed
        String aliceTxnId = "tx_alice_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand aliceCommand = createCommand(alice, claimId, aliceTxnId, "admob_unit_rewarded_001");

        RewardGrantResult aliceResult = adapter.grantRewardAtomic(aliceCommand);
        assertTrue(aliceResult.isSuccess());
        assertEquals(1, aliceResult.updatedBalance().rewardedCredits());

        // Claim in PostgreSQL transitioned cleanly to SERVER_VERIFIED
        PendingRewardClaimEntity verifiedClaim = claimRepository.findById(claimId).orElseThrow();
        assertEquals(RewardVerificationStatus.SERVER_VERIFIED.name(), verifiedClaim.getStatus());
        assertEquals(aliceTxnId, verifiedClaim.getTransactionId());
    }

    @Test
    @DisplayName("7. Ad-unit mismatch does not poison an existing pending claim")
    void adUnitMismatch_doesNotPoisonExistingPendingClaim() {
        String playerId = seedPlayer("pg18_adunit_mismatch");
        String claimId = "claim_adunit_" + UUID.randomUUID().toString().substring(0, 8);
        long now = System.currentTimeMillis();

        PendingRewardClaimEntity pendingClaim = new PendingRewardClaimEntity(
                claimId,
                playerId,
                "admob_unit_rewarded_001",
                1,
                "SOLO_HINT",
                RewardVerificationStatus.PENDING_VERIFICATION.name(),
                null,
                now,
                null,
                null,
                null
        );
        claimRepository.save(pendingClaim);

        // Callback arrives with mismatched ad unit
        String txnId = "tx_mismatch_ad_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand command = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_002");

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.AD_UNIT_MISMATCH, result.status());

        // Verify claim is not poisoned
        PendingRewardClaimEntity reloaded = claimRepository.findById(claimId).orElseThrow();
        assertEquals(RewardVerificationStatus.PENDING_VERIFICATION.name(), reloaded.getStatus());
    }

    @Test
    @DisplayName("8. Reward-type mismatch does not poison an existing pending claim")
    void rewardTypeMismatch_doesNotPoisonExistingPendingClaim() {
        String playerId = seedPlayer("pg18_rewtype_mismatch");
        String claimId = "claim_type_" + UUID.randomUUID().toString().substring(0, 8);
        long now = System.currentTimeMillis();

        PendingRewardClaimEntity pendingClaim = new PendingRewardClaimEntity(
                claimId,
                playerId,
                "admob_unit_rewarded_001",
                1,
                "UNSUPPORTED_REWARD",
                RewardVerificationStatus.PENDING_VERIFICATION.name(),
                null,
                now,
                null,
                null,
                null
        );
        claimRepository.save(pendingClaim);

        String txnId = "tx_mismatch_type_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand command = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_001");

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.REWARD_TYPE_MISMATCH, result.status());

        // Claim remains in pending status
        PendingRewardClaimEntity reloaded = claimRepository.findById(claimId).orElseThrow();
        assertEquals(RewardVerificationStatus.PENDING_VERIFICATION.name(), reloaded.getStatus());
    }

    @Test
    @DisplayName("9. A forced downstream database failure rolls back all reward-related mutations in real PostgreSQL")
    void forcedDownstreamDatabaseFailure_rollsBackAllRewardMutations() {
        String playerId = seedPlayer("pg18_rollback");
        String claimId = "claim_rb_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId = "tx_rb_" + UUID.randomUUID().toString().substring(0, 8);

        // In PostgreSQL schema V4/V12: key_id VARCHAR(64).
        // Passing 100 characters forces PostgreSQL 'value too long for type character varying(64)' at flush.
        String oversizedKeyId = "K".repeat(100);
        RewardGrantCommand failingCommand = new RewardGrantCommand(
                playerId,
                claimId,
                txnId,
                "admob_unit_rewarded_001",
                1,
                oversizedKeyId,
                "sig_pg18_test",
                "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}",
                "ad_unit=admob_unit_rewarded_001&custom_data=...",
                5,
                10,
                1,
                System.currentTimeMillis()
        );

        // Expect exception on flush inside transactional method
        assertThrows(Exception.class, () -> adapter.grantRewardAtomic(failingCommand));

        // Verify full rollback in PostgreSQL
        assertTrue(claimRepository.findById(claimId).isEmpty(), "Pending claim must be rolled back");
        assertTrue(ssvRepository.findById(txnId).isEmpty(), "SSV record must be rolled back");
        assertTrue(eventRepository.findByTransactionId(txnId).isEmpty(), "Reward event must be rolled back");

        // Player hint balance: rewarded_credits must be 0
        Optional<PlayerHintBalanceEntity> balanceOpt = balanceRepository.findById(playerId);
        if (balanceOpt.isPresent()) {
            assertEquals(0, balanceOpt.get().getRewardedCredits(), "Rewarded credits must not be incremented");
        }
    }

    @Test
    @DisplayName("10. PostgreSQL unique constraints and row locks enforce replay protection across separate transactions/connections")
    void uniqueConstraintsAndRowLocks_enforceReplayProtectionAcrossSeparateConnections() {
        String alice = seedPlayer("pg18_alice_unique");
        String bob = seedPlayer("pg18_bob_unique");
        String sharedTxnId = "tx_cross_uniq_" + UUID.randomUUID().toString().substring(0, 8);
        String claimAlice = "claim_al_u_" + UUID.randomUUID().toString().substring(0, 8);
        String claimBob = "claim_bob_u_" + UUID.randomUUID().toString().substring(0, 8);

        RewardGrantCommand cmdAlice = createCommand(alice, claimAlice, sharedTxnId, "admob_unit_rewarded_001");
        RewardGrantCommand cmdBob = createCommand(bob, claimBob, sharedTxnId, "admob_unit_rewarded_001");

        // Alice completes transaction on Connection 1
        RewardGrantResult resAlice = adapter.grantRewardAtomic(cmdAlice);
        assertTrue(resAlice.isSuccess(), "Alice's reward grant must succeed");

        // Bob attempts to claim the same transaction on Connection 2
        RewardGrantResult resBob = adapter.grantRewardAtomic(cmdBob);
        assertFalse(resBob.isSuccess(), "Bob must not be able to replay Alice's transaction ID");
        assertEquals(RewardGrantStatus.DUPLICATE_TRANSACTION, resBob.status());

        // Verify Bob got 0 credits in PostgreSQL
        Optional<PlayerHintBalanceEntity> bobBalance = balanceRepository.findById(bob);
        if (bobBalance.isPresent()) {
            assertEquals(0, bobBalance.get().getRewardedCredits(), "Bob must not receive credits");
        }

        // Verify exactly one SSV record exists for sharedTxnId, belonging to Alice
        AdMobSsvRecordEntity record = ssvRepository.findById(sharedTxnId).orElseThrow();
        assertEquals(alice, record.getUserId(), "SSV record must belong to Alice");
        assertEquals(claimAlice, record.getClaimId());
    }

    @Test
    @DisplayName("11. Step 17.23: At least 12 distinct valid reward grants on the same day succeed in real PostgreSQL")
    void twelveDistinctValidRewardGrantsOnSameDay_allSucceedWithoutArbitraryCaps() {
        String playerId = seedPlayer("pg18_twelve_grants");
        int targetGrants = 12;

        List<String> txnIds = new java.util.ArrayList<>();
        List<String> claimIds = new java.util.ArrayList<>();

        for (int i = 0; i < targetGrants; i++) {
            String claimId = "claim_12_" + i + "_" + UUID.randomUUID().toString().substring(0, 8);
            String txnId = "tx_12_" + i + "_" + UUID.randomUUID().toString().substring(0, 8);
            claimIds.add(claimId);
            txnIds.add(txnId);

            RewardGrantCommand command = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_001");
            RewardGrantResult result = adapter.grantRewardAtomic(command);

            assertTrue(result.isSuccess(), "Grant #" + (i + 1) + " must succeed");
            assertNotNull(result.updatedBalance());
            assertEquals(i + 1, result.updatedBalance().rewardedCredits());
            assertEquals(3 + (i + 1), result.updatedBalance().totalAvailable());
        }

        // Verify balance in real PostgreSQL
        PlayerHintBalanceEntity balance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(3, balance.getFreeHintsRemaining());
        assertEquals(12, balance.getRewardedCredits(), "Must have exactly 12 rewarded credits");
        assertEquals(15, balance.getTotalAvailable(), "Must have exactly 3 + 12 = 15 total available");

        // Verify all 12 SSV records, claims, and reward events in PostgreSQL
        for (int i = 0; i < targetGrants; i++) {
            assertTrue(ssvRepository.findById(txnIds.get(i)).isPresent(), "SSV record #" + i + " must exist");
            assertTrue(claimRepository.findById(claimIds.get(i)).isPresent(), "Claim #" + i + " must exist");
            assertEquals(RewardVerificationStatus.SERVER_VERIFIED.name(), claimRepository.findById(claimIds.get(i)).get().getStatus());
            assertTrue(eventRepository.findByTransactionId(txnIds.get(i)).isPresent(), "Reward event #" + i + " must exist");
        }

        // Verify replaying transaction ID from the 12 completed grants gives 0 additional hints
        String replayClaim = "claim_replay_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand replayTxnCommand = createCommand(playerId, replayClaim, txnIds.get(0), "admob_unit_rewarded_001");
        RewardGrantResult replayTxnResult = adapter.grantRewardAtomic(replayTxnCommand);
        assertFalse(replayTxnResult.isSuccess());
        assertEquals(RewardGrantStatus.DUPLICATE_TRANSACTION, replayTxnResult.status());

        // Verify replaying claim ID gives 0 additional hints
        String replayTxn = "tx_replay_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand replayClaimCommand = createCommand(playerId, claimIds.get(0), replayTxn, "admob_unit_rewarded_001");
        RewardGrantResult replayClaimResult = adapter.grantRewardAtomic(replayClaimCommand);
        assertFalse(replayClaimResult.isSuccess());
        assertEquals(RewardGrantStatus.DUPLICATE_CLAIM_ALREADY_VERIFIED, replayClaimResult.status());

        // Verify final balance unchanged
        PlayerHintBalanceEntity finalBalance = balanceRepository.findById(playerId).orElseThrow();
        assertEquals(12, finalBalance.getRewardedCredits());
    }

    @Test
    @DisplayName("12. Step 17.23: Explicit finite limits still function if configured (regression safety)")
    void explicitFiniteLimits_enforcedWhenConfigured() {
        String playerId = seedPlayer("pg18_finite_limits");
        int maxDaily = 3;
        int maxStored = 5;

        // Perform 3 grants with maxDaily = 3
        for (int i = 0; i < 3; i++) {
            String claimId = "claim_fin_" + i + "_" + UUID.randomUUID().toString().substring(0, 8);
            String txnId = "tx_fin_" + i + "_" + UUID.randomUUID().toString().substring(0, 8);
            RewardGrantCommand cmd = createCommand(playerId, claimId, txnId, "admob_unit_rewarded_001", maxDaily, maxStored);
            RewardGrantResult res = adapter.grantRewardAtomic(cmd);
            assertTrue(res.isSuccess());
        }

        // 4th grant with maxDaily = 3 -> fails with DAILY_LIMIT_EXCEEDED
        String claimId4 = "claim_fin_4_" + UUID.randomUUID().toString().substring(0, 8);
        String txnId4 = "tx_fin_4_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand cmd4 = createCommand(playerId, claimId4, txnId4, "admob_unit_rewarded_001", maxDaily, maxStored);
        RewardGrantResult res4 = adapter.grantRewardAtomic(cmd4);
        assertFalse(res4.isSuccess());
        assertEquals(RewardGrantStatus.DAILY_LIMIT_EXCEEDED, res4.status());

        // But with production unconstrained limits (Integer.MAX_VALUE), 4th grant succeeds!
        String claimIdUnconstrained = "claim_unconstrained_" + UUID.randomUUID().toString().substring(0, 8);
        String txnIdUnconstrained = "tx_unconstrained_" + UUID.randomUUID().toString().substring(0, 8);
        RewardGrantCommand cmdUnconstrained = createCommand(playerId, claimIdUnconstrained, txnIdUnconstrained, "admob_unit_rewarded_001");
        RewardGrantResult resUnconstrained = adapter.grantRewardAtomic(cmdUnconstrained);
        assertTrue(resUnconstrained.isSuccess());
        assertEquals(4, resUnconstrained.updatedBalance().rewardedCredits());
    }
}
