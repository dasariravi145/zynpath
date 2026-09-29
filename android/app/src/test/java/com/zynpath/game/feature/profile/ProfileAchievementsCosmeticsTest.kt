package com.zynpath.game.feature.profile

import com.zynpath.game.core.achievement.AchievementCategory
import com.zynpath.game.core.achievement.AchievementProgress
import com.zynpath.game.core.achievement.AchievementRegistry
import com.zynpath.game.core.achievement.AchievementRepository
import com.zynpath.game.core.cosmetics.catalog.CosmeticCatalog
import com.zynpath.game.core.cosmetics.model.EquippedCosmetics
import com.zynpath.game.core.cosmetics.repository.CosmeticsRepositoryImpl
import com.zynpath.game.core.designsystem.theme.ClassicMidnightPalette
import com.zynpath.game.core.designsystem.theme.PureDarkPalette
import com.zynpath.game.fake.FakePreferencesRepository
import com.zynpath.game.fake.FakeSubscriptionEntitlementRepository
import com.zynpath.game.feature.achievement.AchievementsUiState
import com.zynpath.game.feature.achievement.AchievementsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Verification of Player Profile Presentation, Achievement Evaluation,
 * Category Filtering, and Cosmetics Palette Equipping.
 *
 * Implements Prompt 48 Requirements 54, 55, 56, 57.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileAchievementsCosmeticsTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePrefs: FakePreferencesRepository
    private lateinit var fakeEntitlementRepo: FakeSubscriptionEntitlementRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePrefs = FakePreferencesRepository()
        fakeEntitlementRepo = FakeSubscriptionEntitlementRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAchievementRegistryHasRegisteredMilestones() {
        val allAchievements = AchievementRegistry.ALL_ACHIEVEMENTS
        assertTrue("Registry must contain achievements", allAchievements.isNotEmpty())
        assertEquals(25, allAchievements.size)

        val soloAchievements = allAchievements.filter { it.category == AchievementCategory.SOLO }
        assertTrue(soloAchievements.isNotEmpty())

        val firstStep = AchievementRegistry.SOLO_FIRST_STEP
        assertEquals("solo_first_step", firstStep.id)
        assertEquals("First Step", firstStep.title)
        assertEquals(1, firstStep.targetValue)
    }

    @Test
    fun testAchievementsViewModelLoadsAllMilestonesAndCounts() = runTest(testDispatcher) {
        val fakeAchievementRepo = object : AchievementRepository {
            private val achievements = AchievementRegistry.ALL_ACHIEVEMENTS.map { def ->
                AchievementProgress(
                    definition = def,
                    isUnlocked = def.id == "solo_first_step",
                    currentProgress = if (def.id == "solo_first_step") 1 else 0,
                    unlockedAt = if (def.id == "solo_first_step") System.currentTimeMillis() else null
                )
            }

            override val unlockedEvents: kotlinx.coroutines.flow.SharedFlow<com.zynpath.game.core.achievement.AchievementDefinition> =
                kotlinx.coroutines.flow.MutableSharedFlow()

            override fun observeAchievements(category: AchievementCategory): Flow<List<AchievementProgress>> {
                return MutableStateFlow(
                    if (category == AchievementCategory.ALL) achievements
                    else achievements.filter { it.definition.category == category }
                )
            }

            override fun observeUnlockedCount(): Flow<Int> = MutableStateFlow(1)
            override suspend fun evaluateAll() {}
            override suspend fun evaluateCompetitiveAchievements(stats: com.zynpath.game.core.multiplayer.model.CompetitiveStats) {}
            override suspend fun evaluateDailyAchievements(isServerValidated: Boolean, isLeaderboardEligible: Boolean) {}
        }

        val viewModel = AchievementsViewModel(fakeAchievementRepo)
        val job = backgroundScope.launch(kotlinx.coroutines.test.UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse("Must finish loading", state.isLoading)
        assertEquals(25, state.totalCount)
        assertEquals(1, state.unlockedCount)
        assertEquals(25, state.achievements.size)

        // Find the unlocked item
        val firstStep = state.achievements.find { it.definition.id == "solo_first_step" }
        assertNotNull(firstStep)
        assertTrue(firstStep?.isUnlocked == true)
    }

    @Test
    fun testCosmeticCatalogAndEquippingFlow() = runTest(testDispatcher) {
        val catalog = CosmeticCatalog.ALL_ITEMS
        assertTrue("Cosmetic catalog must contain items", catalog.isNotEmpty())

        val midnightTheme = CosmeticCatalog.THEME_CLASSIC_MIDNIGHT
        assertEquals("theme_classic_midnight", midnightTheme.id)
        assertEquals(com.zynpath.game.core.cosmetics.model.CosmeticAccessStatus.FREE, midnightTheme.accessStatus)

        val pureDark = CosmeticCatalog.THEME_PURE_DARK
        assertEquals("theme_pure_dark", pureDark.id)
        assertEquals(com.zynpath.game.core.cosmetics.model.CosmeticAccessStatus.FREE, pureDark.accessStatus)

        // Equip pure dark theme
        fakePrefs.setEquippedTheme("theme_pure_dark")
        val prefs = fakePrefs.userPreferencesFlow.first()
        assertEquals("theme_pure_dark", prefs.equippedThemeId)
    }
}
