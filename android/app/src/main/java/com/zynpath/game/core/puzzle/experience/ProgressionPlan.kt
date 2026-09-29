package com.zynpath.game.core.puzzle.experience

import com.zynpath.game.core.puzzle.model.WorldConfiguration

/**
 * Definition of a presentation chapter spanning the 300-level solo campaign.
 *
 * Implements Prompt 25 Task 4:
 * Seven structured progression chapters providing narrative framing, pacing,
 * and aesthetic identity while preserving existing authoritative Number Path puzzle rules.
 */
data class ChapterDefinition(
    val chapterId: Int,
    val title: String,
    val subtitle: String,
    val startLevel: Int,
    val endLevel: Int,
    val theme: ExperienceThemeReference,
    val primaryDifficulty: ExperienceDifficultyCategory,
    val description: String
) {
    val totalLevels: Int get() = (endLevel - startLevel) + 1
    val levelRange: IntRange get() = startLevel..endLevel

    fun containsLevel(levelId: Int): Boolean = levelId in levelRange
}

/**
 * Authoritative progression planner mapping all 300 levels to the seven experience chapters.
 */
object ProgressionPlan {

    val CHAPTER_1 = ChapterDefinition(
        chapterId = 1,
        title = "First Steps",
        subtitle = "Awakening the Flow",
        startLevel = 1,
        endLevel = 25,
        theme = ExperienceThemeReference(
            themeId = "first_steps",
            primaryColorHex = 0xFF20D76B, // Emerald / SuccessGreen
            accentColorHex = 0xFF21D4FD   // ElectricCyan
        ),
        primaryDifficulty = ExperienceDifficultyCategory.INTRODUCTORY,
        description = "Master the core flow. Connect numbers in ascending sequence, filling every single cell."
    )

    val CHAPTER_2 = ChapterDefinition(
        chapterId = 2,
        title = "Smart Turns",
        subtitle = "Geometry in Motion",
        startLevel = 26,
        endLevel = 50,
        theme = ExperienceThemeReference(
            themeId = "smart_turns",
            primaryColorHex = 0xFF168BFF, // BrightBlue
            accentColorHex = 0xFF21D4FD
        ),
        primaryDifficulty = ExperienceDifficultyCategory.CASUAL,
        description = "Discover deliberate direction changes. 5x5 boards that reward foresight and clean corner routing."
    )

    val CHAPTER_3 = ChapterDefinition(
        chapterId = 3,
        title = "Path Explorer",
        subtitle = "Labyrinthian Corridors",
        startLevel = 51,
        endLevel = 100,
        theme = ExperienceThemeReference(
            themeId = "path_explorer",
            primaryColorHex = 0xFFFF4757, // WallCrimson
            accentColorHex = 0xFFFFC247  // Gold
        ),
        primaryDifficulty = ExperienceDifficultyCategory.BALANCED,
        description = "Walls reshape the battlefield. Navigate around blocked boundaries to find the sole Hamiltonian trail."
    )

    val CHAPTER_4 = ChapterDefinition(
        chapterId = 4,
        title = "Strategic Paths",
        subtitle = "Long-Range Planning",
        startLevel = 101,
        endLevel = 150,
        theme = ExperienceThemeReference(
            themeId = "strategic_paths",
            primaryColorHex = 0xFF7209B7, // AccentPurple
            accentColorHex = 0xFF21D4FD
        ),
        primaryDifficulty = ExperienceDifficultyCategory.STRATEGIC,
        description = "Expansive 6x6 topologies with widely spaced checkpoints requiring multi-move forward planning."
    )

    val CHAPTER_5 = ChapterDefinition(
        chapterId = 5,
        title = "Expert Journey",
        subtitle = "Labyrinth of Logic",
        startLevel = 151,
        endLevel = 200,
        theme = ExperienceThemeReference(
            themeId = "expert_journey",
            primaryColorHex = 0xFFFF8D32, // OrangeAccent
            accentColorHex = 0xFFFFC247
        ),
        primaryDifficulty = ExperienceDifficultyCategory.ADVANCED,
        description = "Dense 7x7 grids with strategic wall chokepoints. Every single cell must be accounted for before turning."
    )

    val CHAPTER_6 = ChapterDefinition(
        chapterId = 6,
        title = "Master Trails",
        subtitle = "The Grand Tapestry",
        startLevel = 201,
        endLevel = 250,
        theme = ExperienceThemeReference(
            themeId = "master_trails",
            primaryColorHex = 0xFFFFC247, // Gold
            accentColorHex = 0xFF168BFF
        ),
        primaryDifficulty = ExperienceDifficultyCategory.EXPERT,
        description = "Grand 8x8 canvases where deceptive shortcuts trap the unwary. Only true spatial masters advance."
    )

    val CHAPTER_7 = ChapterDefinition(
        chapterId = 7,
        title = "Grand Challenge",
        subtitle = "Zenith of Pathfinding",
        startLevel = 251,
        endLevel = 300,
        theme = ExperienceThemeReference(
            themeId = "grand_challenge",
            primaryColorHex = 0xFF21D4FD, // ElectricCyan
            accentColorHex = 0xFFFFC247
        ),
        primaryDifficulty = ExperienceDifficultyCategory.MASTER,
        description = "The ultimate test of precision and endurance. 8x8 boards with maximum wall density and zero margin for error."
    )

    val ALL_CHAPTERS: List<ChapterDefinition> = listOf(
        CHAPTER_1, CHAPTER_2, CHAPTER_3, CHAPTER_4, CHAPTER_5, CHAPTER_6, CHAPTER_7
    )

    /**
     * Resolves the presentation chapter for any level in 1..300.
     */
    fun getChapterForLevel(levelId: Int): ChapterDefinition {
        return ALL_CHAPTERS.firstOrNull { it.containsLevel(levelId) } ?: CHAPTER_1
    }

    /**
     * Resolves chapter by its ID in 1..7.
     */
    fun getChapter(chapterId: Int): ChapterDefinition {
        return ALL_CHAPTERS.firstOrNull { it.chapterId == chapterId } ?: CHAPTER_1
    }

    /**
     * Generates rich presentation metadata for any level.
     */
    fun getLevelExperience(levelId: Int): LevelExperienceMetadata {
        val chapter = getChapterForLevel(levelId)
        val world = WorldConfiguration.getWorldForLevel(levelId)
        val milestone = resolveMilestone(levelId, chapter)

        val difficulty = when {
            levelId <= 15 -> ExperienceDifficultyCategory.INTRODUCTORY
            levelId <= 40 -> ExperienceDifficultyCategory.CASUAL
            levelId <= 80 -> ExperienceDifficultyCategory.BALANCED
            levelId <= 140 -> ExperienceDifficultyCategory.STRATEGIC
            levelId <= 190 -> ExperienceDifficultyCategory.ADVANCED
            levelId <= 250 -> ExperienceDifficultyCategory.EXPERT
            else -> ExperienceDifficultyCategory.MASTER
        }

        return LevelExperienceMetadata(
            levelId = levelId,
            chapterId = chapter.chapterId,
            chapterTitle = chapter.title,
            displayTitle = "Level $levelId",
            difficultyCategory = difficulty,
            puzzleId = "w${world.worldId}_lvl$levelId",
            worldId = world.worldId,
            themeReference = chapter.theme,
            milestoneIndicator = milestone,
            defaultFreeHints = 2,
            completionPresentation = CompletionPresentationMetadata(
                celebrationTitle = if (milestone != null) milestone.milestoneTitle else "Level $levelId Cleared!",
                celebrationSubtitle = if (milestone != null) milestone.milestoneSubtitle else "${chapter.title} • Completed",
                fanfareTier = when (milestone?.type) {
                    MilestoneType.GRAND_FINALE -> 3
                    MilestoneType.CHAPTER_CLIMAX, MilestoneType.CENTURY_MARK -> 2
                    else -> 1
                },
                sparkleParticlesCount = when (milestone?.type) {
                    MilestoneType.GRAND_FINALE -> 32
                    MilestoneType.CHAPTER_CLIMAX -> 24
                    else -> 16
                }
            )
        )
    }

    private fun resolveMilestone(levelId: Int, chapter: ChapterDefinition): MilestoneIndicator? {
        return when (levelId) {
            1 -> MilestoneIndicator(
                type = MilestoneType.CHAPTER_START,
                milestoneTitle = "Journey Begins",
                milestoneSubtitle = "Welcome to Zynpath",
                badgeIconName = "ic_sparkle",
                celebrationTier = 1
            )
            25 -> MilestoneIndicator(
                type = MilestoneType.CHAPTER_CLIMAX,
                milestoneTitle = "First Steps Mastered",
                milestoneSubtitle = "Chapter 1 Completed",
                badgeIconName = "ic_trophy",
                celebrationTier = 2
            )
            50 -> MilestoneIndicator(
                type = MilestoneType.CHAPTER_CLIMAX,
                milestoneTitle = "Sharp Thinker",
                milestoneSubtitle = "Chapter 2 Completed",
                badgeIconName = "ic_crown",
                celebrationTier = 2
            )
            100 -> MilestoneIndicator(
                type = MilestoneType.CENTURY_MARK,
                milestoneTitle = "Path Explorer Mastered",
                milestoneSubtitle = "Chapter 3 Completed • 100 Puzzles Solved",
                badgeIconName = "ic_century",
                celebrationTier = 2
            )
            150 -> MilestoneIndicator(
                type = MilestoneType.CHAPTER_CLIMAX,
                milestoneTitle = "Strategic Paths Mastered",
                milestoneSubtitle = "Chapter 4 Completed • 150 Puzzles Solved",
                badgeIconName = "ic_trophy",
                celebrationTier = 2
            )
            200 -> MilestoneIndicator(
                type = MilestoneType.CENTURY_MARK,
                milestoneTitle = "Expert Journey Mastered",
                milestoneSubtitle = "Chapter 5 Completed • 200 Puzzles Solved",
                badgeIconName = "ic_crown",
                celebrationTier = 2
            )
            250 -> MilestoneIndicator(
                type = MilestoneType.CHAPTER_CLIMAX,
                milestoneTitle = "Master Trails Mastered",
                milestoneSubtitle = "Chapter 6 Completed • 250 Puzzles Solved",
                badgeIconName = "ic_trophy",
                celebrationTier = 2
            )
            300 -> MilestoneIndicator(
                type = MilestoneType.GRAND_FINALE,
                milestoneTitle = "Grand Pathmaster",
                milestoneSubtitle = "Grand Challenge Completed • All 300 Levels Conquered",
                badgeIconName = "ic_grand_star",
                celebrationTier = 3
            )
            // Mid-chapter milestones for engaging progression pacing
            13, 38, 75, 125, 175, 225, 275 -> MilestoneIndicator(
                type = MilestoneType.MID_CHAPTER_CHECKPOINT,
                milestoneTitle = "Chapter Checkpoint",
                milestoneSubtitle = "Halfway through ${chapter.title}",
                badgeIconName = "ic_star",
                celebrationTier = 1
            )
            else -> null
        }
    }
}
