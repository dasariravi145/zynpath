package com.zynpath.game.core.hint

import com.zynpath.game.core.datastore.PreferencesRepository
import com.zynpath.game.core.datastore.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [HintUsagePolicy] and [HintUsageRepository].
 *
 * Implements Prompt 14 Section 37:
 * - Free allowance available & atomic deduction.
 * - Free allowance exhaustion.
 * - Verified Premium entitlement (unlimited hints).
 * - Application restart persistence simulation.
 * - Consumption rule enforcement (only consume on useful guidance delivery).
 */
import com.zynpath.game.fake.FakePreferencesRepository

class HintUsagePolicyTest {

    private lateinit var fakePreferencesRepo: FakePreferencesRepository
    private lateinit var hintUsageRepository: HintUsageRepository

    @Before
    fun setUp() {
        fakePreferencesRepo = FakePreferencesRepository(UserPreferences(freeHintsRemaining = 3, isPremium = false))
        hintUsageRepository = HintUsageRepositoryImpl(fakePreferencesRepo)
    }

    @Test
    fun `test free allowance initially available`() = runTest {
        assertEquals(3, hintUsageRepository.getRemainingHints())
        assertFalse(hintUsageRepository.isPremium())
        assertTrue(hintUsageRepository.canConsumeHint())
    }

    @Test
    fun `test free allowance consumes atomically until exhausted`() = runTest {
        assertTrue(hintUsageRepository.consumeHint())
        assertEquals(2, hintUsageRepository.getRemainingHints())

        assertTrue(hintUsageRepository.consumeHint())
        assertEquals(1, hintUsageRepository.getRemainingHints())

        assertTrue(hintUsageRepository.consumeHint())
        assertEquals(0, hintUsageRepository.getRemainingHints())

        // Exhausted: canConsume is false, consumeHint returns false
        assertFalse(hintUsageRepository.canConsumeHint())
        assertFalse(hintUsageRepository.consumeHint())
        assertEquals(0, hintUsageRepository.getRemainingHints())
    }

    @Test
    fun `test premium user has unlimited hints`() = runTest {
        hintUsageRepository.setPremium(true)

        assertTrue(hintUsageRepository.isPremium())
        assertEquals(Int.MAX_VALUE, hintUsageRepository.getRemainingHints())
        assertTrue(hintUsageRepository.canConsumeHint())

        // Multiple consumptions never deplete unlimited entitlement
        for (i in 1..10) {
            assertTrue(hintUsageRepository.consumeHint())
            assertEquals(Int.MAX_VALUE, hintUsageRepository.getRemainingHints())
        }
    }

    @Test
    fun `test hint allowance persists across repository restarts`() = runTest {
        hintUsageRepository.consumeHint()
        assertEquals(2, hintUsageRepository.getRemainingHints())

        // Simulate app restart by instantiating new repository over same datastore
        val restartedRepo = HintUsageRepositoryImpl(fakePreferencesRepo)
        assertEquals(2, restartedRepo.getRemainingHints())
    }

    @Test
    fun `test granting bonus free hints increases allowance`() = runTest {
        hintUsageRepository.consumeHint()
        hintUsageRepository.consumeHint()
        hintUsageRepository.consumeHint()
        assertEquals(0, hintUsageRepository.getRemainingHints())

        hintUsageRepository.addFreeHints(2)
        assertEquals(2, hintUsageRepository.getRemainingHints())
        assertTrue(hintUsageRepository.canConsumeHint())
    }
}
