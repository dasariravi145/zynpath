package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.ads.model.RewardGrantCommand;
import com.zynpath.backend.ads.model.RewardGrantResult;
import com.zynpath.backend.ads.model.RewardGrantStatus;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.ads.model.RewardVerificationStatus;
import com.zynpath.backend.persistence.entity.AdMobSsvRecordEntity;
import com.zynpath.backend.persistence.entity.PendingRewardClaimEntity;
import com.zynpath.backend.persistence.entity.PlayerHintBalanceEntity;
import com.zynpath.backend.persistence.entity.RewardEventEntity;
import com.zynpath.backend.persistence.repository.AdMobSsvRecordRepository;
import com.zynpath.backend.persistence.repository.PendingRewardClaimRepository;
import com.zynpath.backend.persistence.repository.PlayerHintBalanceRepository;
import com.zynpath.backend.persistence.repository.RewardEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaAdRewardPersistenceAdapterTest {

    private PlayerHintBalanceRepository balanceRepository;
    private PendingRewardClaimRepository claimRepository;
    private AdMobSsvRecordRepository ssvRepository;
    private RewardEventRepository eventRepository;
    private JpaAdRewardPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        balanceRepository = mock(PlayerHintBalanceRepository.class);
        claimRepository = mock(PendingRewardClaimRepository.class);
        ssvRepository = mock(AdMobSsvRecordRepository.class);
        eventRepository = mock(RewardEventRepository.class);
        adapter = new JpaAdRewardPersistenceAdapter(balanceRepository, claimRepository, ssvRepository, eventRepository);
    }

    private RewardGrantCommand createCommand(String playerId, String claimId, String txnId, String adUnitId) {
        return new RewardGrantCommand(
                playerId,
                claimId,
                txnId,
                adUnitId,
                1,
                "test-key-id",
                "test-sig",
                "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}",
                "ad_unit=" + adUnitId + "&custom_data=...",
                5,
                10,
                1,
                System.currentTimeMillis()
        );
    }

    @Test
    @DisplayName("1. Unexpected database exception in native insert is not swallowed and propagates")
    void lockOrCreateBalance_nativeInsertException_propagatesImmediately() {
        RewardGrantCommand command = createCommand("player-100", "claim-100", "tx-100", "admob-unit-01");

        doThrow(new DataAccessResourceFailureException("Database connection lost", null))
                .when(balanceRepository).insertDefaultBalanceIfNotExists(eq("player-100"), anyLong());

        assertThrows(DataAccessResourceFailureException.class, () -> adapter.grantRewardAtomic(command));

        verify(balanceRepository, never()).findByPlayerIdWithLock(anyString());
        verify(claimRepository, never()).save(any());
        verify(balanceRepository, never()).save(any());
        verify(eventRepository, never()).save(any());
        verify(ssvRepository, never()).save(any());
    }

    @Test
    @DisplayName("2. Missing balance row after insert throws IllegalStateException without creating fallback")
    void lockOrCreateBalance_missingRowAfterInsert_throwsIllegalStateException() {
        RewardGrantCommand command = createCommand("player-100", "claim-100", "tx-100", "admob-unit-01");

        when(ssvRepository.findById("tx-100")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-100")).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> adapter.grantRewardAtomic(command));
        assertTrue(ex.getMessage().contains("Failed to acquire locked hint balance"));

        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. First-time player acquires row lock and executes atomic reward grant")
    void firstTimePlayer_acquiresLock_andGrantsReward() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("player-new", "claim-new", "tx-new", "admob-unit-01");

        PlayerHintBalanceEntity balanceEntity = new PlayerHintBalanceEntity("player-new", 3, 0, 3, now);
        when(ssvRepository.findById("tx-new")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-new")).thenReturn(Optional.of(balanceEntity));
        when(claimRepository.findById("claim-new")).thenReturn(Optional.empty());
        when(eventRepository.countByPlayerIdAndGrantedAtMsGreaterThanEqual(eq("player-new"), anyLong())).thenReturn(0L);
        when(balanceRepository.save(any(PlayerHintBalanceEntity.class))).thenAnswer(i -> i.getArgument(0));

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertTrue(result.isSuccess());
        assertNotNull(result.updatedBalance());
        assertEquals(1, result.updatedBalance().rewardedCredits());
        assertEquals(4, result.updatedBalance().totalAvailable());

        InOrder inOrder = inOrder(balanceRepository, claimRepository, eventRepository, ssvRepository);
        inOrder.verify(balanceRepository).insertDefaultBalanceIfNotExists(eq("player-new"), anyLong());
        inOrder.verify(balanceRepository).findByPlayerIdWithLock("player-new");
        inOrder.verify(claimRepository).save(any(PendingRewardClaimEntity.class));
        inOrder.verify(balanceRepository).save(any(PlayerHintBalanceEntity.class));
        inOrder.verify(eventRepository).save(any(RewardEventEntity.class));
        inOrder.verify(ssvRepository).save(any(AdMobSsvRecordEntity.class));
        inOrder.verify(claimRepository).flush();
        inOrder.verify(balanceRepository).flush();
        inOrder.verify(eventRepository).flush();
        inOrder.verify(ssvRepository).flush();
    }

    @Test
    @DisplayName("4. Recheck under lock detects duplicate transaction and returns DUPLICATE_TRANSACTION")
    void replayProtection_duplicateTransactionUnderLock_returnsDuplicateFailure() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("player-dup", "claim-dup", "tx-dup", "admob-unit-01");

        PlayerHintBalanceEntity balanceEntity = new PlayerHintBalanceEntity("player-dup", 3, 0, 3, now);

        // Pre-check returns empty, but under lock returns already committed record
        AdMobSsvRecordEntity existingRecord = new AdMobSsvRecordEntity(
                "tx-dup", "key-1", "sig", "{}", "player-dup", "claim-dup", "admob-unit-01", 1, "qs", now, true, null
        );
        when(ssvRepository.findById("tx-dup"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingRecord));
        when(balanceRepository.findByPlayerIdWithLock("player-dup")).thenReturn(Optional.of(balanceEntity));

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.DUPLICATE_TRANSACTION, result.status());

        verify(claimRepository, never()).save(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("5. Ownership mismatch returns OWNERSHIP_MISMATCH without mutating or poisoning victim's claim")
    void claimValidation_ownershipMismatch_doesNotPoisonVictimClaim() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("attacker-bob", "alice-claim", "tx-attack", "admob-unit-01");

        PlayerHintBalanceEntity attackerBalance = new PlayerHintBalanceEntity("attacker-bob", 3, 0, 3, now);
        when(ssvRepository.findById("tx-attack")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("attacker-bob")).thenReturn(Optional.of(attackerBalance));

        PendingRewardClaimEntity aliceClaim = new PendingRewardClaimEntity(
                "alice-claim", "alice", "admob-unit-01", 1, "SOLO_HINT",
                RewardVerificationStatus.PENDING_VERIFICATION.name(), null, now, null, null, null
        );
        when(claimRepository.findById("alice-claim")).thenReturn(Optional.of(aliceClaim));

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.OWNERSHIP_MISMATCH, result.status());

        // Verify victim's claim was NOT modified or saved
        assertEquals(RewardVerificationStatus.PENDING_VERIFICATION.name(), aliceClaim.getStatus());
        verify(claimRepository, never()).save(any());
        verify(balanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("6. Existing claim ad-unit mismatch returns AD_UNIT_MISMATCH without mutating claim")
    void claimValidation_adUnitMismatch_doesNotPoisonClaim() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("player-1", "claim-1", "tx-1", "expected-unit-A");

        PlayerHintBalanceEntity balance = new PlayerHintBalanceEntity("player-1", 3, 0, 3, now);
        when(ssvRepository.findById("tx-1")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-1")).thenReturn(Optional.of(balance));

        PendingRewardClaimEntity claim = new PendingRewardClaimEntity(
                "claim-1", "player-1", "different-unit-B", 1, "SOLO_HINT",
                RewardVerificationStatus.PENDING_VERIFICATION.name(), null, now, null, null, null
        );
        when(claimRepository.findById("claim-1")).thenReturn(Optional.of(claim));

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.AD_UNIT_MISMATCH, result.status());
        verify(claimRepository, never()).save(any());
    }

    @Test
    @DisplayName("7. Existing claim reward-type mismatch returns REWARD_TYPE_MISMATCH without mutating claim")
    void claimValidation_rewardTypeMismatch_doesNotPoisonClaim() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("player-1", "claim-1", "tx-1", "admob-unit-01");

        PlayerHintBalanceEntity balance = new PlayerHintBalanceEntity("player-1", 3, 0, 3, now);
        when(ssvRepository.findById("tx-1")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-1")).thenReturn(Optional.of(balance));

        PendingRewardClaimEntity claim = new PendingRewardClaimEntity(
                "claim-1", "player-1", "admob-unit-01", 1, "UNSUPPORTED_TYPE",
                RewardVerificationStatus.PENDING_VERIFICATION.name(), null, now, null, null, null
        );
        when(claimRepository.findById("claim-1")).thenReturn(Optional.of(claim));

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.REWARD_TYPE_MISMATCH, result.status());
        verify(claimRepository, never()).save(any());
    }

    @Test
    @DisplayName("8. Already verified claim returns DUPLICATE_CLAIM_ALREADY_VERIFIED")
    void claimValidation_alreadyVerified_returnsDuplicateFailure() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("player-1", "claim-1", "tx-1", "admob-unit-01");

        PlayerHintBalanceEntity balance = new PlayerHintBalanceEntity("player-1", 3, 0, 3, now);
        when(ssvRepository.findById("tx-1")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-1")).thenReturn(Optional.of(balance));

        PendingRewardClaimEntity claim = new PendingRewardClaimEntity(
                "claim-1", "player-1", "admob-unit-01", 1, "SOLO_HINT",
                RewardVerificationStatus.SERVER_VERIFIED.name(), "tx-old", now, now, null, null
        );
        when(claimRepository.findById("claim-1")).thenReturn(Optional.of(claim));

        RewardGrantResult result = adapter.grantRewardAtomic(command);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.DUPLICATE_CLAIM_ALREADY_VERIFIED, result.status());
    }

    @Test
    @DisplayName("9. Database constraint exception during SSV save propagates for rollback")
    void atomicity_downstreamException_propagatesForRollback() {
        long now = System.currentTimeMillis();
        RewardGrantCommand command = createCommand("player-1", "claim-1", "tx-1", "admob-unit-01");

        PlayerHintBalanceEntity balance = new PlayerHintBalanceEntity("player-1", 3, 0, 3, now);
        when(ssvRepository.findById("tx-1")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-1")).thenReturn(Optional.of(balance));
        when(claimRepository.findById("claim-1")).thenReturn(Optional.empty());
        when(eventRepository.countByPlayerIdAndGrantedAtMsGreaterThanEqual(eq("player-1"), anyLong())).thenReturn(0L);
        when(balanceRepository.save(any())).thenReturn(balance);

        when(ssvRepository.save(any())).thenThrow(new DataIntegrityViolationException("Unique constraint violation uq_admob_ssv_records_transaction_id"));

        assertThrows(DataIntegrityViolationException.class, () -> adapter.grantRewardAtomic(command));
    }

    @Test
    @DisplayName("10. Different transactions for same player succeed sequentially under lock")
    void differentTransactions_samePlayer_succeedSequentially() {
        long now = System.currentTimeMillis();
        RewardGrantCommand cmd1 = createCommand("player-multi", "claim-1", "tx-1", "admob-unit-01");
        RewardGrantCommand cmd2 = createCommand("player-multi", "claim-2", "tx-2", "admob-unit-01");

        PlayerHintBalanceEntity bal1 = new PlayerHintBalanceEntity("player-multi", 3, 0, 3, now);
        PlayerHintBalanceEntity bal2 = new PlayerHintBalanceEntity("player-multi", 3, 1, 4, now);

        when(ssvRepository.findById("tx-1")).thenReturn(Optional.empty());
        when(ssvRepository.findById("tx-2")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-multi"))
                .thenReturn(Optional.of(bal1))
                .thenReturn(Optional.of(bal2));
        when(claimRepository.findById(anyString())).thenReturn(Optional.empty());
        when(eventRepository.countByPlayerIdAndGrantedAtMsGreaterThanEqual(eq("player-multi"), anyLong()))
                .thenReturn(0L)
                .thenReturn(1L);
        when(balanceRepository.save(any(PlayerHintBalanceEntity.class))).thenAnswer(i -> i.getArgument(0));

        RewardGrantResult res1 = adapter.grantRewardAtomic(cmd1);
        RewardGrantResult res2 = adapter.grantRewardAtomic(cmd2);

        assertTrue(res1.isSuccess());
        assertEquals(1, res1.updatedBalance().rewardedCredits());

        assertTrue(res2.isSuccess());
        assertEquals(2, res2.updatedBalance().rewardedCredits());
    }

    @Test
    @DisplayName("11. Step 17.22: Unconstrained command allows 6+ daily and 10+ cumulative rewards without blocking")
    void unconstrainedCommand_allowsExcessDailyAndCumulativeRewards() {
        long now = System.currentTimeMillis();
        // Command with UNLIMITED_DAILY_REWARDS (Integer.MAX_VALUE) and UNLIMITED_STORED_CREDITS (Integer.MAX_VALUE)
        RewardGrantCommand unconstrainedCmd = new RewardGrantCommand(
                "player-unconstrained", "claim-100", "tx-100", "admob-unit-01", 1,
                "key-1", "sig-1", "{}", "qs", Integer.MAX_VALUE, Integer.MAX_VALUE, 1, now
        );

        // Player balance already has 15 rewarded credits, and 8 daily rewards in last 24h
        PlayerHintBalanceEntity highBalance = new PlayerHintBalanceEntity("player-unconstrained", 3, 15, 18, now);
        when(ssvRepository.findById("tx-100")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-unconstrained")).thenReturn(Optional.of(highBalance));
        when(claimRepository.findById("claim-100")).thenReturn(Optional.empty());
        when(balanceRepository.save(any(PlayerHintBalanceEntity.class))).thenAnswer(i -> i.getArgument(0));

        RewardGrantResult result = adapter.grantRewardAtomic(unconstrainedCmd);

        assertTrue(result.isSuccess(), "Grant must succeed when limits are unconstrained");
        assertEquals(16, result.updatedBalance().rewardedCredits());
        assertEquals(19, result.updatedBalance().totalAvailable());
    }

    @Test
    @DisplayName("12. Step 17.22: Finite limits are enforced when explicitly configured on command")
    void finiteLimits_enforcedWhenExplicitlyConfigured() {
        long now = System.currentTimeMillis();
        RewardGrantCommand finiteDailyCmd = new RewardGrantCommand(
                "player-finite", "claim-200", "tx-200", "admob-unit-01", 1,
                "key-1", "sig-1", "{}", "qs", 5, 10, 1, now
        );

        PlayerHintBalanceEntity balance = new PlayerHintBalanceEntity("player-finite", 3, 2, 5, now);
        when(ssvRepository.findById("tx-200")).thenReturn(Optional.empty());
        when(balanceRepository.findByPlayerIdWithLock("player-finite")).thenReturn(Optional.of(balance));
        when(claimRepository.findById("claim-200")).thenReturn(Optional.empty());
        // Mock 5 daily rewards already granted
        when(eventRepository.countByPlayerIdAndGrantedAtMsGreaterThanEqual(eq("player-finite"), anyLong())).thenReturn(5L);

        RewardGrantResult result = adapter.grantRewardAtomic(finiteDailyCmd);

        assertFalse(result.isSuccess());
        assertEquals(RewardGrantStatus.DAILY_LIMIT_EXCEEDED, result.status());
    }
}
