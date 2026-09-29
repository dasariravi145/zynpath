package com.zynpath.game.core.puzzle.experience

import androidx.compose.ui.graphics.Color

/**
 * Immutable difficulty classification tailored for player experience and progression pacing.
 */
enum class ExperienceDifficultyCategory(
    val displayName: String,
    val colorHex: Long
) {
    INTRODUCTORY("Introductory", 0xFF20D76B), // GameSuccessGreen
    CASUAL("Casual", 0xFF21D4FD),            // GameElectricCyan
    BALANCED("Balanced", 0xFF168BFF),        // GameBrightBlue
    STRATEGIC("Strategic", 0xFF7209B7),      // AccentPurple
    ADVANCED("Advanced", 0xFFFF8D32),        // GameOrangeAccent
    EXPERT("Expert", 0xFFFFC247),            // GameGold
    MASTER("Master", 0xFFFF4757);            // WallCrimson

    val color: Color get() = Color(colorHex)
}

/**
 * Presentation milestone tiers across the 300-level campaign.
 */
enum class MilestoneType {
    NONE,
    CHAPTER_START,
    MID_CHAPTER_CHECKPOINT,
    CHAPTER_CLIMAX,
    CENTURY_MARK,
    GRAND_FINALE
}

/**
 * Visual metadata for milestone celebrations.
 */
data class MilestoneIndicator(
    val type: MilestoneType,
    val milestoneTitle: String,
    val milestoneSubtitle: String,
    val badgeIconName: String,
    val celebrationTier: Int = 1
)

/**
 * Theme reference specifying visual accents for a chapter or level.
 */
data class ExperienceThemeReference(
    val themeId: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val backgroundTintHex: Long = 0xFF07142D
) {
    val primaryColor: Color get() = Color(primaryColorHex)
    val accentColor: Color get() = Color(accentColorHex)
    val backgroundTint: Color get() = Color(backgroundTintHex)
}

/**
 * Completion presentation styling to provide satisfying visual milestones upon victory.
 */
data class CompletionPresentationMetadata(
    val celebrationTitle: String = "Level Cleared!",
    val celebrationSubtitle: String = "Path Mastered",
    val fanfareTier: Int = 1,
    val sparkleParticlesCount: Int = 16,
    val badgeUnlockKey: String? = null
)

/**
 * Lightweight level experience metadata model.
 *
 * Implements Prompt 25 Task 3:
 * Complements existing [com.zynpath.game.core.puzzle.catalog.LevelDefinition] with rich
 * presentation, chapter context, and milestone indicators without modifying authoritative
 * Number Path puzzle rules.
 */
data class LevelExperienceMetadata(
    val levelId: Int,
    val chapterId: Int,
    val chapterTitle: String,
    val displayTitle: String,
    val difficultyCategory: ExperienceDifficultyCategory,
    val puzzleId: String,
    val worldId: Int,
    val themeReference: ExperienceThemeReference,
    val milestoneIndicator: MilestoneIndicator? = null,
    val defaultFreeHints: Int = 2,
    val completionPresentation: CompletionPresentationMetadata = CompletionPresentationMetadata()
) {
    val isMilestone: Boolean get() = milestoneIndicator != null && milestoneIndicator.type != MilestoneType.NONE

    init {
        require(levelId in 1..300) { "levelId must be in 1..300 (got $levelId)" }
        require(chapterId in 1..7) { "chapterId must be in 1..7 (got $chapterId)" }
        require(defaultFreeHints == 2) { "defaultFreeHints must be locked to 2 (got $defaultFreeHints)" }
    }
}
