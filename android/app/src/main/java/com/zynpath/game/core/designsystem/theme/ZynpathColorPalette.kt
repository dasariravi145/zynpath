package com.zynpath.game.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Semantic color palette defining visual appearance for Zynpath themes.
 *
 * Implements Prompt 28 Sections 13, 14, 15, 16:
 * - High contrast between checkpoints, walls, paths, and board backgrounds.
 * - Walls remain unmistakably visible on every theme.
 * - Numbers remain legible with WCAG AA compliance.
 */
data class ZynpathColorPalette(
    val id: String,
    val name: String,
    val isDark: Boolean = true,

    // App Surfaces & Layout
    val backgroundDark: Color,
    val backgroundSurface: Color,
    val backgroundCard: Color,
    val backgroundElevated: Color,

    // Material 3 Semantic Colors
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val tertiary: Color,

    // Puzzle Board Colors
    val boardBackground: Color,
    val boardCellBorder: Color,
    val cellCoveredTint: Color,
    val cellStartHalo: Color,

    // Checkpoint Tokens
    val checkpointDark: Color,
    val checkpointVisited: Color,
    val checkpointBorder: Color,
    val checkpointText: Color,
    val checkpointStart: Color,
    val checkpointFinal: Color,

    // Wall Tokens (Always high contrast)
    val wallColor: Color,
    val wallGlow: Color,

    // Path Drawing Tokens
    val pathCoreColor: Color,
    val pathGlowColor: Color,
    val pathHeadHalo: Color,

    // Text Tokens
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color
)

// 1. Classic Midnight (Free Default - Premium Cinematic Fantasy)
val ClassicMidnightPalette = ZynpathColorPalette(
    id = "theme_classic_midnight",
    name = "Classic Midnight",
    isDark = true,
    backgroundDark = GameDeepNavy,
    backgroundSurface = GameMidnightBlue,
    backgroundCard = Color(0xFF142348),
    backgroundElevated = Color(0xFF1A2F60),
    primary = GameElectricCyan,
    onPrimary = GameDeepNavy,
    primaryContainer = GameRoyalBlue,
    secondary = GameGold,
    tertiary = GameOrangeAccent,
    boardBackground = GameMidnightBlue,
    boardCellBorder = Color(0xFF1D356A),
    cellCoveredTint = Color(0x3321D4FD),
    cellStartHalo = Color(0x5521D4FD),
    checkpointDark = GameDeepNavy,
    checkpointVisited = GameMidnightBlue,
    checkpointBorder = GameElectricCyan,
    checkpointText = GameWhite,
    checkpointStart = GameElectricCyan,
    checkpointFinal = GameGold,
    wallColor = Color(0xFFFF4757),
    wallGlow = Color(0x66FF4757),
    pathCoreColor = GameElectricCyan,
    pathGlowColor = Color(0x66168BFF),
    pathHeadHalo = Color(0x5521D4FD),
    textPrimary = GameWhite,
    textSecondary = GameSecondaryText,
    textMuted = Color(0xFF788EB6)
)

// 2. Pure Dark / OLED Slate (Free Theme)
val PureDarkPalette = ZynpathColorPalette(
    id = "theme_pure_dark",
    name = "Pure Dark OLED",
    isDark = true,
    backgroundDark = Color(0xFF000000),
    backgroundSurface = Color(0xFF121212),
    backgroundCard = Color(0xFF1E1E1E),
    backgroundElevated = Color(0xFF2C2C2C),
    primary = Color(0xFF00E676),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF00E5FF),
    tertiary = Color(0xFFFFD600),
    boardBackground = Color(0xFF121212),
    boardCellBorder = Color(0xFF282828),
    cellCoveredTint = Color(0x3300E5FF),
    cellStartHalo = Color(0x5500E676),
    checkpointDark = Color(0xFF1E1E1E),
    checkpointVisited = Color(0xFF282828),
    checkpointBorder = Color(0xFF00E5FF),
    checkpointText = Color(0xFFFFFFFF),
    checkpointStart = Color(0xFF00E676),
    checkpointFinal = Color(0xFFFFD600),
    wallColor = Color(0xFFFF1744),
    wallGlow = Color(0x66FF1744),
    pathCoreColor = Color(0xFF00E5FF),
    pathGlowColor = Color(0x5500B0FF),
    pathHeadHalo = Color(0x4D00E5FF),
    textPrimary = Color(0xFFF5F5F5),
    textSecondary = Color(0xFFA0A0A0),
    textMuted = Color(0xFF707070)
)

// 3. Cyber Neon (Premium Theme)
val CyberNeonPalette = ZynpathColorPalette(
    id = "theme_cyber_neon",
    name = "Cyber Neon",
    isDark = true,
    backgroundDark = Color(0xFF0D0221),
    backgroundSurface = Color(0xFF190B38),
    backgroundCard = Color(0xFF261447),
    backgroundElevated = Color(0xFF351C5E),
    primary = Color(0xFFFF007F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF6B0F40),
    secondary = Color(0xFF00F0FF),
    tertiary = Color(0xFFFFE600),
    boardBackground = Color(0xFF190B38),
    boardCellBorder = Color(0xFF3A1F6E),
    cellCoveredTint = Color(0x3300F0FF),
    cellStartHalo = Color(0x55FF007F),
    checkpointDark = Color(0xFF261447),
    checkpointVisited = Color(0xFF351C5E),
    checkpointBorder = Color(0xFF00F0FF),
    checkpointText = Color(0xFFFFFFFF),
    checkpointStart = Color(0xFFFF007F),
    checkpointFinal = Color(0xFFFFE600),
    wallColor = Color(0xFFFF0055),
    wallGlow = Color(0x80FF0055),
    pathCoreColor = Color(0xFF00F0FF),
    pathGlowColor = Color(0x667000FF),
    pathHeadHalo = Color(0x6600F0FF),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFB8A2D8),
    textMuted = Color(0xFF7B669E)
)

// 4. Emerald Forest (Premium Theme)
val EmeraldForestPalette = ZynpathColorPalette(
    id = "theme_emerald_forest",
    name = "Emerald Forest",
    isDark = true,
    backgroundDark = Color(0xFF041C15),
    backgroundSurface = Color(0xFF0A2E23),
    backgroundCard = Color(0xFF124335),
    backgroundElevated = Color(0xFF1C5746),
    primary = Color(0xFF2EC4B6),
    onPrimary = Color(0xFF041C15),
    primaryContainer = Color(0xFF136F63),
    secondary = Color(0xFF80ED99),
    tertiary = Color(0xFFFFB703),
    boardBackground = Color(0xFF0A2E23),
    boardCellBorder = Color(0xFF1C5746),
    cellCoveredTint = Color(0x3380ED99),
    cellStartHalo = Color(0x552EC4B6),
    checkpointDark = Color(0xFF124335),
    checkpointVisited = Color(0xFF1C5746),
    checkpointBorder = Color(0xFF80ED99),
    checkpointText = Color(0xFFFFFFFF),
    checkpointStart = Color(0xFF2EC4B6),
    checkpointFinal = Color(0xFFFFB703),
    wallColor = Color(0xFFE76F51),
    wallGlow = Color(0x66E76F51),
    pathCoreColor = Color(0xFF80ED99),
    pathGlowColor = Color(0x5557CC99),
    pathHeadHalo = Color(0x4D80ED99),
    textPrimary = Color(0xFFF0FFF4),
    textSecondary = Color(0xFF98C1B0),
    textMuted = Color(0xFF6B9B88)
)

// 5. Solar Amber (Premium Theme)
val SolarAmberPalette = ZynpathColorPalette(
    id = "theme_solar_amber",
    name = "Solar Amber",
    isDark = true,
    backgroundDark = Color(0xFF14110E),
    backgroundSurface = Color(0xFF231F1A),
    backgroundCard = Color(0xFF332D26),
    backgroundElevated = Color(0xFF453D34),
    primary = Color(0xFFFFAA00),
    onPrimary = Color(0xFF14110E),
    primaryContainer = Color(0xFF7A4E00),
    secondary = Color(0xFFFFD166),
    tertiary = Color(0xFFEF476F),
    boardBackground = Color(0xFF231F1A),
    boardCellBorder = Color(0xFF483F35),
    cellCoveredTint = Color(0x33FFD166),
    cellStartHalo = Color(0x55FFAA00),
    checkpointDark = Color(0xFF332D26),
    checkpointVisited = Color(0xFF453D34),
    checkpointBorder = Color(0xFFFFD166),
    checkpointText = Color(0xFFFFFFFF),
    checkpointStart = Color(0xFFFFAA00),
    checkpointFinal = Color(0xFFFFE66D),
    wallColor = Color(0xFFE63946),
    wallGlow = Color(0x66E63946),
    pathCoreColor = Color(0xFFFFD166),
    pathGlowColor = Color(0x55F4A261),
    pathHeadHalo = Color(0x4DFFD166),
    textPrimary = Color(0xFFFFFDF5),
    textSecondary = Color(0xFFC4B8A5),
    textMuted = Color(0xFF8E8373)
)

/**
 * CompositionLocal providing access to the current active [ZynpathColorPalette].
 */
val LocalZynpathPalette = staticCompositionLocalOf { ClassicMidnightPalette }

object ZynpathPalettes {
    val ALL: List<ZynpathColorPalette> = listOf(
        ClassicMidnightPalette,
        PureDarkPalette,
        CyberNeonPalette,
        EmeraldForestPalette,
        SolarAmberPalette
    )

    fun getById(id: String): ZynpathColorPalette {
        return ALL.firstOrNull { it.id == id } ?: ClassicMidnightPalette
    }
}
