package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.entity.PlayerWalletBalanceEntity;
import com.zynpath.backend.persistence.entity.WalletTransactionEntity;
import com.zynpath.backend.persistence.repository.PlayerWalletBalanceRepository;
import com.zynpath.backend.persistence.repository.WalletTransactionRepository;
import com.zynpath.backend.wallet.model.WalletBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaWalletPersistenceAdapterTest {

    private PlayerWalletBalanceRepository balanceRepository;
    private WalletTransactionRepository transactionRepository;
    private JpaWalletPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        balanceRepository = mock(PlayerWalletBalanceRepository.class);
        transactionRepository = mock(WalletTransactionRepository.class);
        adapter = new JpaWalletPersistenceAdapter(balanceRepository, transactionRepository);
    }

    @Test
    void testCredit_newPlayer_createsBalanceAndTransaction() {
        String playerId = "p_100";
        when(transactionRepository.findByIdempotencyKey("tx_key_1")).thenReturn(Optional.empty());
        when(balanceRepository.findByIdForUpdate(playerId)).thenReturn(Optional.empty());

        WalletBalance result = adapter.credit(playerId, "LEVEL_REWARD", 50, "tx_key_1", "{}");

        assertEquals(50, result.coinBalance());
        verify(balanceRepository).save(any(PlayerWalletBalanceEntity.class));
        verify(transactionRepository).save(any(WalletTransactionEntity.class));
    }

    @Test
    void testCredit_duplicateIdempotencyKey_returnsCurrentWithoutDuplicating() {
        String playerId = "p_100";
        WalletTransactionEntity existingTx = new WalletTransactionEntity(
                "tx_abc", playerId, "LEVEL_REWARD", 50, 50, "tx_key_dup", "{}", System.currentTimeMillis()
        );
        when(transactionRepository.findByIdempotencyKey("tx_key_dup")).thenReturn(Optional.of(existingTx));
        when(balanceRepository.findById(playerId)).thenReturn(Optional.of(
                new PlayerWalletBalanceEntity(playerId, 50, 0, System.currentTimeMillis())
        ));

        WalletBalance result = adapter.credit(playerId, "LEVEL_REWARD", 50, "tx_key_dup", "{}");

        assertEquals(50, result.coinBalance());
        verify(balanceRepository, never()).save(any(PlayerWalletBalanceEntity.class));
    }

    @Test
    void testDebit_sufficientFunds_updatesBalance() {
        String playerId = "p_100";
        PlayerWalletBalanceEntity existing = new PlayerWalletBalanceEntity(playerId, 300, 5, System.currentTimeMillis());
        when(transactionRepository.findByIdempotencyKey("hint_debit_1")).thenReturn(Optional.empty());
        when(balanceRepository.findByIdForUpdate(playerId)).thenReturn(Optional.of(existing));

        WalletBalance result = adapter.debit(playerId, "EXTRA_HINT", 100, "hint_debit_1", "{}");

        assertEquals(200, result.coinBalance());
        verify(balanceRepository).save(existing);
        verify(transactionRepository).save(any(WalletTransactionEntity.class));
    }

    @Test
    void testDebit_insufficientFunds_throwsIllegalStateException() {
        String playerId = "p_100";
        PlayerWalletBalanceEntity existing = new PlayerWalletBalanceEntity(playerId, 50, 0, System.currentTimeMillis());
        when(transactionRepository.findByIdempotencyKey("hint_debit_2")).thenReturn(Optional.empty());
        when(balanceRepository.findByIdForUpdate(playerId)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () ->
                adapter.debit(playerId, "EXTRA_HINT", 100, "hint_debit_2", "{}")
        );
        verify(balanceRepository, never()).save(any(PlayerWalletBalanceEntity.class));
    }

    @Test
    void testEnsureWelcomeReward_grantsOnce() {
        String playerId = "p_new_welcome";
        when(transactionRepository.existsByIdempotencyKey("welcome_" + playerId)).thenReturn(false);
        when(transactionRepository.existsByPlayerIdAndType(playerId, "WELCOME_REWARD")).thenReturn(false);
        when(balanceRepository.findByIdForUpdate(playerId)).thenReturn(Optional.empty());

        boolean granted = adapter.ensureWelcomeReward(playerId, 300);

        assertTrue(granted);
        verify(balanceRepository).save(any(PlayerWalletBalanceEntity.class));
    }

    @Test
    void testEnsureWelcomeReward_duplicate_rejected() {
        String playerId = "p_existing_welcome";
        when(transactionRepository.existsByIdempotencyKey("welcome_" + playerId)).thenReturn(true);

        boolean granted = adapter.ensureWelcomeReward(playerId, 300);

        assertFalse(granted);
        verify(balanceRepository, never()).save(any(PlayerWalletBalanceEntity.class));
    }
}
