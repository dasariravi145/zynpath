package com.zynpath.backend.wallet;

import com.zynpath.backend.wallet.model.WalletBalance;
import com.zynpath.backend.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletServiceTest {

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService();
    }

    @Test
    void testWelcomeReward_grants300Once() {
        String playerId = "p_test_1";
        boolean first = walletService.ensureWelcomeReward(playerId);
        assertTrue(first);
        assertEquals(300, walletService.getBalance(playerId).coinBalance());

        // Repeated call must return false and not duplicate coins
        boolean second = walletService.ensureWelcomeReward(playerId);
        assertFalse(second);
        assertEquals(300, walletService.getBalance(playerId).coinBalance());
    }

    @Test
    void testCreditAndDebit_withIdempotency() {
        String playerId = "p_test_2";
        walletService.ensureWelcomeReward(playerId); // 300

        // Credit +100
        WalletBalance b1 = walletService.credit(playerId, "SOLO_FIRST_CLEAR", 100, "clear_lvl_1", "{}");
        assertEquals(400, b1.coinBalance());

        // Duplicate credit returns same balance
        WalletBalance b1Dup = walletService.credit(playerId, "SOLO_FIRST_CLEAR", 100, "clear_lvl_1", "{}");
        assertEquals(400, b1Dup.coinBalance());

        // Debit -300 for extra hint
        WalletBalance b2 = walletService.debit(playerId, "EXTRA_HINT", 300, "hint_1", "{}");
        assertEquals(100, b2.coinBalance());

        // Duplicate debit returns same balance
        WalletBalance b2Dup = walletService.debit(playerId, "EXTRA_HINT", 300, "hint_1", "{}");
        assertEquals(100, b2Dup.coinBalance());

        // Debit more than balance throws exception
        assertThrows(IllegalStateException.class, () ->
                walletService.debit(playerId, "EXTRA_HINT", 300, "hint_2", "{}")
        );
        assertEquals(100, walletService.getBalance(playerId).coinBalance());
    }

    @Test
    void testReassignGuestTransactions_preservesBalance() {
        String guestId = "guest_123";
        String authId = "auth_456";

        walletService.ensureWelcomeReward(guestId); // 300
        walletService.credit(guestId, "SOLO_FIRST_CLEAR", 100, "clear_lvl_1", "{}"); // 400

        WalletBalance reassigned = walletService.reassignGuestTransactions(guestId, authId);
        assertEquals(400, reassigned.coinBalance());
        assertEquals(400, walletService.getBalance(authId).coinBalance());
        assertEquals(0, walletService.getBalance(guestId).coinBalance());
    }
}
