package com.zynpath.game.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Decorative pattern identifiers for chapter themes on World Map and board ambient backdrops.
 */
enum class DecorativePatternType {
    CALM_STARS,       // Chapter 1 (1–25): Calm blue discovery
    CYAN_CIRCUITS,    // Chapter 2 (26–50): Cyan pathways
    LUMINOUS_CRYSTAL, // Chapter 3 (51–100): Luminous exploration
    GEOMETRIC_TRAILS, // Chapter 4 (101–150): Deep-blue geometric trails
    ELECTRIC_AURORA,  // Chapter 5 (151–200): Refined electric-blue atmosphere
    ROYAL_CREST,      // Chapter 6 (201–250): Royal-blue and gold mastery
    COSMIC_FINALE     // Chapter 7 (251–300): Premium cinematic finale
}

/**
 * Reusable Chapter Theme model providing visual identity across the 300-level Solo journey.
 *
 * Implements Prompt 30 Tasks 4 & 5:
 * Preserves the established original Zynpath brand (deep navy background, royal-blue surfaces,
 * electric-cyan puzzle connections, gold primary actions) while providing distinct chapter
 * atmosphere, node styling, milestone presentation, and celebration effects.
 */
data class ChapterTheme(
    val chapterId: Int,
    val title: String,
    val subtitle: String,
    val visualDirection: String,
    val startLevel: Int,
    val endLevel: Int,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val backgroundBrush: Brush,
    val surfaceBrush: Brush,
    val ambientGlowColor: Color,
    val nodeBorderBrush: Brush,
    val nodeCompletedGlow: Color,
    val nodeCurrentRingColor: Color,
    val mapPathGlow: Color,
    val milestoneBadgeBrush: Brush,
    val milestoneGlow: Color,
    val celebrationSparkles: List<Color>,
    val celebrationBannerBrush: Brush,
    val decorativePattern: DecorativePatternType
) {
    val levelRange: IntRange get() = startLevel..endLevel

    fun containsLevel(levelId: Int): Boolean = levelId in levelRange
}

/**
 * Authoritative registry of all seven Zynpath Solo journey chapter visual themes.
 */
object ChapterThemes {

    // Chapter 1: First Steps (Levels 1–25) — Calm blue discovery
    val CHAPTER_1 = ChapterTheme(
        chapterId = 1,
        title = "First Steps",
        subtitle = "Awakening the Flow",
        visualDirection = "Calm blue discovery",
        startLevel = 1,
        endLevel = 25,
        primaryAccent = Color(0xFF20D76B), // Tranquil Emerald
        secondaryAccent = GameElectricCyan,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF071B2E),
                GameDeepNavy,
                Color(0xFF030A16)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE0E2442),
                Color(0xF808162A)
            )
        ),
        ambientGlowColor = Color(0x2E20D76B),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(Color(0xFF20D76B), GameElectricCyan)
        ),
        nodeCompletedGlow = Color(0xFF20D76B),
        nodeCurrentRingColor = Color(0xFF20D76B),
        mapPathGlow = Color(0x3320D76B),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(Color(0xFF20D76B), GameElectricCyan)
        ),
        milestoneGlow = Color(0x6620D76B),
        celebrationSparkles = listOf(
            Color(0xFF20D76B),
            GameElectricCyan,
            GameGoldHighlight,
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF0E3D30), Color(0xFF0F2C4B))
        ),
        decorativePattern = DecorativePatternType.CALM_STARS
    )

    // Chapter 2: Smart Turns (Levels 26–50) — Cyan pathways
    val CHAPTER_2 = ChapterTheme(
        chapterId = 2,
        title = "Smart Turns",
        subtitle = "Geometry in Motion",
        visualDirection = "Cyan pathways",
        startLevel = 26,
        endLevel = 50,
        primaryAccent = GameElectricCyan,
        secondaryAccent = Color(0xFF168BFF),
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF061E3B),
                GameDeepNavy,
                Color(0xFF040E1E)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE0F2A52),
                Color(0xF8081A34)
            )
        ),
        ambientGlowColor = Color(0x2E21D4FD),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(GameElectricCyan, Color(0xFF168BFF))
        ),
        nodeCompletedGlow = GameElectricCyan,
        nodeCurrentRingColor = GameElectricCyan,
        mapPathGlow = Color(0x4021D4FD),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(GameElectricCyan, Color(0xFF168BFF))
        ),
        milestoneGlow = Color(0x6621D4FD),
        celebrationSparkles = listOf(
            GameElectricCyan,
            Color(0xFF168BFF),
            GameGoldHighlight,
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF0C2B54), Color(0xFF124378))
        ),
        decorativePattern = DecorativePatternType.CYAN_CIRCUITS
    )

    // Chapter 3: Path Explorer (Levels 51–100) — Luminous exploration
    val CHAPTER_3 = ChapterTheme(
        chapterId = 3,
        title = "Path Explorer",
        subtitle = "Labyrinthian Corridors",
        visualDirection = "Luminous exploration",
        startLevel = 51,
        endLevel = 100,
        primaryAccent = Color(0xFFFF5252), // Glowing Coral / Wall Accent
        secondaryAccent = GameGold,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A122E),
                GameDeepNavy,
                Color(0xFF050918)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE201B42),
                Color(0xF80D112B)
            )
        ),
        ambientGlowColor = Color(0x2EFF5252),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(Color(0xFFFF5252), GameGoldHighlight)
        ),
        nodeCompletedGlow = GameGold,
        nodeCurrentRingColor = Color(0xFFFF5252),
        mapPathGlow = Color(0x33FF5252),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(Color(0xFFFF5252), GameGold)
        ),
        milestoneGlow = Color(0x66FF5252),
        celebrationSparkles = listOf(
            GameGoldHighlight,
            Color(0xFFFF5252),
            GameElectricCyan,
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF38142C), Color(0xFF1F2252))
        ),
        decorativePattern = DecorativePatternType.LUMINOUS_CRYSTAL
    )

    // Chapter 4: Strategic Paths (Levels 101–150) — Deep-blue geometric trails
    val CHAPTER_4 = ChapterTheme(
        chapterId = 4,
        title = "Strategic Paths",
        subtitle = "Long-Range Planning",
        visualDirection = "Deep-blue geometric trails",
        startLevel = 101,
        endLevel = 150,
        primaryAccent = Color(0xFF7209B7), // Royal Amethyst
        secondaryAccent = GameElectricCyan,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF140D30),
                GameDeepNavy,
                Color(0xFF03071A)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE1A1647),
                Color(0xF80A0E2A)
            )
        ),
        ambientGlowColor = Color(0x337209B7),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(Color(0xFF7209B7), GameElectricCyan)
        ),
        nodeCompletedGlow = Color(0xFF7209B7),
        nodeCurrentRingColor = Color(0xFF7209B7),
        mapPathGlow = Color(0x337209B7),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(Color(0xFF7209B7), GameElectricCyan)
        ),
        milestoneGlow = Color(0x667209B7),
        celebrationSparkles = listOf(
            Color(0xFF7209B7),
            GameElectricCyan,
            Color(0xFF4CC9F0),
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF261047), Color(0xFF102859))
        ),
        decorativePattern = DecorativePatternType.GEOMETRIC_TRAILS
    )

    // Chapter 5: Expert Journey (Levels 151–200) — Refined electric-blue atmosphere
    val CHAPTER_5 = ChapterTheme(
        chapterId = 5,
        title = "Expert Journey",
        subtitle = "Labyrinth of Logic",
        visualDirection = "Refined electric-blue atmosphere",
        startLevel = 151,
        endLevel = 200,
        primaryAccent = Color(0xFFFF8D32), // Vibrant Amber Fire
        secondaryAccent = GameGoldHighlight,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1C1322),
                GameDeepNavy,
                Color(0xFF040B18)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE261B36),
                Color(0xF80F162E)
            )
        ),
        ambientGlowColor = Color(0x2EFF8D32),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(Color(0xFFFF8D32), GameGoldHighlight)
        ),
        nodeCompletedGlow = Color(0xFFFF8D32),
        nodeCurrentRingColor = Color(0xFFFF8D32),
        mapPathGlow = Color(0x33FF8D32),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(Color(0xFFFF8D32), GameGoldHighlight)
        ),
        milestoneGlow = Color(0x66FF8D32),
        celebrationSparkles = listOf(
            Color(0xFFFF8D32),
            GameGoldHighlight,
            GameElectricCyan,
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF381D14), Color(0xFF1A264F))
        ),
        decorativePattern = DecorativePatternType.ELECTRIC_AURORA
    )

    // Chapter 6: Master Trails (Levels 201–250) — Royal-blue and gold mastery
    val CHAPTER_6 = ChapterTheme(
        chapterId = 6,
        title = "Master Trails",
        subtitle = "The Grand Tapestry",
        visualDirection = "Royal-blue and gold mastery",
        startLevel = 201,
        endLevel = 250,
        primaryAccent = GameGold,
        secondaryAccent = Color(0xFF168BFF),
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1C180E),
                GameDeepNavy,
                Color(0xFF030A1A)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE262217),
                Color(0xF80C1733)
            )
        ),
        ambientGlowColor = Color(0x33FFC247),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(GameGoldHighlight, GameGold, Color(0xFF168BFF))
        ),
        nodeCompletedGlow = GameGold,
        nodeCurrentRingColor = GameGoldHighlight,
        mapPathGlow = Color(0x40FFC247),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(GameGoldHighlight, GameGold)
        ),
        milestoneGlow = Color(0x80FFC247),
        celebrationSparkles = listOf(
            GameGoldHighlight,
            GameGold,
            Color(0xFF168BFF),
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF3B2F0D), Color(0xFF122C5C))
        ),
        decorativePattern = DecorativePatternType.ROYAL_CREST
    )

    // Chapter 7: Grand Challenge (Levels 251–300) — Premium cinematic finale
    val CHAPTER_7 = ChapterTheme(
        chapterId = 7,
        title = "Grand Challenge",
        subtitle = "Zenith of Pathfinding",
        visualDirection = "Premium cinematic finale",
        startLevel = 251,
        endLevel = 300,
        primaryAccent = GameElectricCyan,
        secondaryAccent = GameGoldHighlight,
        backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF07243D),
                Color(0xFF0C1736),
                Color(0xFF020612)
            )
        ),
        surfaceBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xEE123055),
                Color(0xF80A1A36)
            )
        ),
        ambientGlowColor = Color(0x3D21D4FD),
        nodeBorderBrush = Brush.verticalGradient(
            listOf(GameGoldHighlight, GameElectricCyan, Color(0xFF168BFF))
        ),
        nodeCompletedGlow = GameGoldHighlight,
        nodeCurrentRingColor = GameGoldHighlight,
        mapPathGlow = Color(0x5521D4FD),
        milestoneBadgeBrush = Brush.linearGradient(
            listOf(GameGoldHighlight, GameElectricCyan)
        ),
        milestoneGlow = Color(0x8021D4FD),
        celebrationSparkles = listOf(
            GameGoldHighlight,
            GameElectricCyan,
            Color(0xFFFFD700),
            Color(0xFF00F0FF),
            GameWhite
        ),
        celebrationBannerBrush = Brush.horizontalGradient(
            listOf(Color(0xFF0E385C), Color(0xFF38290B))
        ),
        decorativePattern = DecorativePatternType.COSMIC_FINALE
    )

    val ALL_THEMES: List<ChapterTheme> = listOf(
        CHAPTER_1, CHAPTER_2, CHAPTER_3, CHAPTER_4, CHAPTER_5, CHAPTER_6, CHAPTER_7
    )

    /**
     * Resolves the [ChapterTheme] for a given chapter ID (1..7).
     * Provides graceful fallback to [CHAPTER_1] for unknown IDs.
     */
    fun getThemeForChapter(chapterId: Int): ChapterTheme {
        return ALL_THEMES.firstOrNull { it.chapterId == chapterId } ?: CHAPTER_1
    }

    /**
     * Resolves the [ChapterTheme] for any level in 1..300.
     * Provides graceful fallback to [CHAPTER_1] for out-of-range levels.
     */
    fun getThemeForLevel(levelId: Int): ChapterTheme {
        return ALL_THEMES.firstOrNull { it.containsLevel(levelId) } ?: CHAPTER_1
    }
}

/**
 * CompositionLocal providing access to the current chapter theme context.
 */
val LocalChapterTheme = staticCompositionLocalOf { ChapterThemes.CHAPTER_1 }
