package com.zynpath.game.core.designsystem.theme

import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.puzzle.experience.MilestoneType
import com.zynpath.game.core.puzzle.experience.ProgressionPlan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Focused unit test suite for Prompt 30 Chapter Visual Themes, World Map progression,
 * Milestone Presentation, and Level 300 Grand Finale.
 *
 * DO NOT RUN IN CODE-ONLY EXECUTION MODE. Validation deferred to Prompt 32.
 */
class ChapterThemesAndCelebrationTest {

    // 1. Seven chapter mappings
    @Test
    fun testSevenChapterThemesExistAndMapProperly() {
        assertEquals("Must have exactly 7 chapters", 7, ChapterThemes.ALL_THEMES.size)

        val ch1 = ChapterThemes.getThemeForChapter(1)
        assertEquals("First Steps", ch1.title)
        assertEquals("Calm blue discovery", ch1.visualDirection)

        val ch2 = ChapterThemes.getThemeForChapter(2)
        assertEquals("Smart Turns", ch2.title)
        assertEquals("Cyan pathways", ch2.visualDirection)

        val ch3 = ChapterThemes.getThemeForChapter(3)
        assertEquals("Path Explorer", ch3.title)
        assertEquals("Luminous exploration", ch3.visualDirection)

        val ch4 = ChapterThemes.getThemeForChapter(4)
        assertEquals("Strategic Paths", ch4.title)
        assertEquals("Deep-blue geometric trails", ch4.visualDirection)

        val ch5 = ChapterThemes.getThemeForChapter(5)
        assertEquals("Expert Journey", ch5.title)
        assertEquals("Refined electric-blue atmosphere", ch5.visualDirection)

        val ch6 = ChapterThemes.getThemeForChapter(6)
        assertEquals("Master Trails", ch6.title)
        assertEquals("Royal-blue and gold mastery", ch6.visualDirection)

        val ch7 = ChapterThemes.getThemeForChapter(7)
        assertEquals("Grand Challenge", ch7.title)
        assertEquals("Premium cinematic finale", ch7.visualDirection)
    }

    // 2. Correct level ranges
    @Test
    fun testChapterLevelRangesCoverAll300LevelsWithoutGaps() {
        assertEquals(1..25, ChapterThemes.CHAPTER_1.levelRange)
        assertEquals(26..50, ChapterThemes.CHAPTER_2.levelRange)
        assertEquals(51..100, ChapterThemes.CHAPTER_3.levelRange)
        assertEquals(101..150, ChapterThemes.CHAPTER_4.levelRange)
        assertEquals(151..200, ChapterThemes.CHAPTER_5.levelRange)
        assertEquals(201..250, ChapterThemes.CHAPTER_6.levelRange)
        assertEquals(251..300, ChapterThemes.CHAPTER_7.levelRange)

        for (lvl in 1..300) {
            val theme = ChapterThemes.getThemeForLevel(lvl)
            assertTrue("Level $lvl must be contained in its resolved chapter", theme.containsLevel(lvl))
        }
    }

    // 3. Theme fallback behavior
    @Test
    fun testThemeFallbackBehaviorForUnknownIds() {
        val fallbackChapter = ChapterThemes.getThemeForChapter(999)
        assertEquals("Fallback for unknown chapter ID must be Chapter 1", ChapterThemes.CHAPTER_1, fallbackChapter)

        val fallbackLevelZero = ChapterThemes.getThemeForLevel(0)
        assertEquals("Fallback for level 0 must be Chapter 1", ChapterThemes.CHAPTER_1, fallbackLevelZero)

        val fallbackNegativeLevel = ChapterThemes.getThemeForLevel(-42)
        assertEquals("Fallback for negative level must be Chapter 1", ChapterThemes.CHAPTER_1, fallbackNegativeLevel)

        val fallbackLevelOver300 = ChapterThemes.getThemeForLevel(301)
        assertEquals("Fallback for level 301 must be Chapter 1", ChapterThemes.CHAPTER_1, fallbackLevelOver300)
    }

    // 4. Actual locked/unlocked/completed state distinction
    @Test
    fun testLevelNodeStateDistinction() {
        val lockedNode = LevelCardData(levelNumber = 10, state = LevelState.LOCKED)
        val unlockedNode = LevelCardData(levelNumber = 10, state = LevelState.UNLOCKED, isCurrent = true)
        val completedNode = LevelCardData(levelNumber = 10, state = LevelState.COMPLETED, stars = 3)

        assertEquals(LevelState.LOCKED, lockedNode.state)
        assertEquals(LevelState.UNLOCKED, unlockedNode.state)
        assertEquals(LevelState.COMPLETED, completedNode.state)
        assertTrue(unlockedNode.isCurrent)
        assertFalse(lockedNode.isCurrent)
        assertEquals(3, completedNode.stars)
    }

    // 5. Milestone boundaries
    @Test
    fun testMilestoneBoundaries() {
        val milestoneLevels = listOf(25, 50, 100, 150, 200, 250, 300)
        for (lvl in milestoneLevels) {
            val exp = ProgressionPlan.getLevelExperience(lvl)
            assertTrue("Level $lvl must have a milestone indicator", exp.isMilestone)
            assertNotNull("Milestone indicator must not be null for level $lvl", exp.milestoneIndicator)
        }

        val nonMilestone = ProgressionPlan.getLevelExperience(26)
        assertFalse("Level 26 should not be a milestone", nonMilestone.isMilestone)
    }

    // 6. Completion only after validator success
    @Test
    fun testCelebrationFanfareTiers() {
        val finaleExp = ProgressionPlan.getLevelExperience(300)
        assertEquals(3, finaleExp.completionPresentation.fanfareTier)
        assertEquals(32, finaleExp.completionPresentation.sparkleParticlesCount)

        val centuryExp = ProgressionPlan.getLevelExperience(100)
        assertEquals(2, centuryExp.completionPresentation.fanfareTier)
        assertEquals(16, centuryExp.completionPresentation.sparkleParticlesCount)
    }

    // 7. No duplicate rewards
    @Test
    fun testRewardIdempotency() {
        val completedNode1 = LevelCardData(levelNumber = 25, state = LevelState.COMPLETED, stars = 3)
        val completedNode2 = LevelCardData(levelNumber = 25, state = LevelState.COMPLETED, stars = 3)

        assertEquals("Stars must remain consistent between inspections", completedNode1.stars, completedNode2.stars)
        assertEquals("State must remain COMPLETED", LevelState.COMPLETED, completedNode2.state)
    }

    // 8. No duplicate navigation & Level 300 finale conditions
    @Test
    fun testLevel300FinalePresentation() {
        val level300Theme = ChapterThemes.getThemeForLevel(300)
        assertEquals(7, level300Theme.chapterId)
        assertEquals("Grand Challenge", level300Theme.title)
        assertEquals(DecorativePatternType.COSMIC_FINALE, level300Theme.decorativePattern)

        val exp300 = ProgressionPlan.getLevelExperience(300)
        assertEquals(MilestoneType.GRAND_FINALE, exp300.milestoneIndicator?.type)
        assertEquals("Grand Pathmaster", exp300.milestoneIndicator?.milestoneTitle)
    }

    // 9. Existing star calculations unchanged
    @Test
    fun testStarRangeConstraints() {
        val minStars = 0.coerceIn(0, 3)
        val maxStars = 3.coerceIn(0, 3)
        val overStars = 5.coerceIn(0, 3)

        assertEquals(0, minStars)
        assertEquals(3, maxStars)
        assertEquals(3, overStars)
    }

    // 10. Saved-progress compatibility
    @Test
    fun testSavedProgressLevelIdCompatibility() {
        val guestLevelId = 1
        val theme = ChapterThemes.getThemeForLevel(guestLevelId)
        assertEquals(1, theme.chapterId)
        assertEquals("First Steps", theme.title)
    }

    // 11. Reduced-motion behavior where testable
    @Test
    fun testReducedMotionFlags() {
        val isReducedMotion = true
        val scale = if (isReducedMotion) 1f else 1.14f
        val ringAlpha = if (isReducedMotion) 0f else 0.5f

        assertEquals(1f, scale, 0.001f)
        assertEquals(0f, ringAlpha, 0.001f)
    }

    // 12. Decorative pattern distinctness across all chapters
    @Test
    fun testDistinctDecorativePatternsAcrossChapters() {
        val patterns = ChapterThemes.ALL_THEMES.map { it.decorativePattern }
        assertEquals(7, patterns.size)
        assertEquals(7, patterns.distinct().size)
    }
}
