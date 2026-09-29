package com.zynpath.game.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.AvatarFrameType
import com.zynpath.game.core.designsystem.components.AvatarSize
import com.zynpath.game.core.designsystem.components.NumberTileState
import com.zynpath.game.core.designsystem.components.ProgressBarStyle
import com.zynpath.game.core.designsystem.components.RewardTier
import com.zynpath.game.core.designsystem.components.RewardType
import com.zynpath.game.core.designsystem.theme.ClassicMidnightPalette
import com.zynpath.game.core.designsystem.theme.GameBrightBlue
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests validating Prompt 01/24 Game Design System tokens, palette conformance,
 * typography hierarchy, and component enums.
 */
class GameDesignSystemTest {

    @Test
    fun testApprovedColorPaletteValues() {
        assertEquals("Deep Navy must match #07142D", Color(0xFF07142D), GameDeepNavy)
        assertEquals("Midnight Blue must match #101D3C", Color(0xFF101D3C), GameMidnightBlue)
        assertEquals("Royal Blue must match #154A98", Color(0xFF154A98), GameRoyalBlue)
        assertEquals("Electric Cyan must match #21D4FD", Color(0xFF21D4FD), GameElectricCyan)
        assertEquals("Bright Blue must match #168BFF", Color(0xFF168BFF), GameBrightBlue)
        assertEquals("Gold must match #FFC247", Color(0xFFFFC247), GameGold)
        assertEquals("Gold Highlight must match #FFE27A", Color(0xFFFFE27A), GameGoldHighlight)
        assertEquals("Orange Accent must match #FF8D32", Color(0xFFFF8D32), GameOrangeAccent)
        assertEquals("Success Green must match #20D76B", Color(0xFF20D76B), GameSuccessGreen)
        assertEquals("White must match #FFFFFF", Color(0xFFFFFFFF), GameWhite)
        assertEquals("Secondary Text must match #B8C9E8", Color(0xFFB8C9E8), GameSecondaryText)
    }

    @Test
    fun testClassicMidnightPaletteUsesApprovedTokens() {
        assertEquals(GameDeepNavy, ClassicMidnightPalette.backgroundDark)
        assertEquals(GameMidnightBlue, ClassicMidnightPalette.backgroundSurface)
        assertEquals(GameElectricCyan, ClassicMidnightPalette.primary)
        assertEquals(GameDeepNavy, ClassicMidnightPalette.onPrimary)
        assertEquals(GameRoyalBlue, ClassicMidnightPalette.primaryContainer)
        assertEquals(GameGold, ClassicMidnightPalette.secondary)
        assertEquals(GameOrangeAccent, ClassicMidnightPalette.tertiary)
        assertEquals(GameWhite, ClassicMidnightPalette.textPrimary)
        assertEquals(GameSecondaryText, ClassicMidnightPalette.textSecondary)
        assertEquals(GameElectricCyan, ClassicMidnightPalette.pathCoreColor)
        assertEquals(GameElectricCyan, ClassicMidnightPalette.checkpointStart)
        assertEquals(GameGold, ClassicMidnightPalette.checkpointFinal)
    }

    @Test
    fun testGameTypographyHierarchy() {
        assertEquals(32.sp, GameTypography.gameTitle.fontSize)
        assertEquals(FontWeight.ExtraBold, GameTypography.gameTitle.fontWeight)

        assertEquals(24.sp, GameTypography.screenHeading.fontSize)
        assertEquals(FontWeight.Bold, GameTypography.screenHeading.fontWeight)

        assertEquals(28.sp, GameTypography.levelNumber.fontSize)
        assertEquals(FontWeight.Black, GameTypography.levelNumber.fontWeight)

        assertEquals(24.sp, GameTypography.scoreTimer.fontSize)
        assertEquals(FontWeight.ExtraBold, GameTypography.scoreTimer.fontWeight)

        assertEquals(17.sp, GameTypography.buttonPrimary.fontSize)
        assertEquals(FontWeight.Black, GameTypography.buttonPrimary.fontWeight)

        assertEquals(15.sp, GameTypography.buttonSecondary.fontSize)
        assertEquals(FontWeight.Bold, GameTypography.buttonSecondary.fontWeight)

        assertEquals(14.sp, GameTypography.labelMedium.fontSize)
        assertEquals(12.sp, GameTypography.secondaryInfo.fontSize)
        assertEquals(11.sp, GameTypography.badgeText.fontSize)
    }

    @Test
    fun testGameShapesTokens() {
        assertNotNull(GameShapes.card)
        assertNotNull(GameShapes.panel)
        assertNotNull(GameShapes.buttonPrimary)
        assertNotNull(GameShapes.buttonSecondary)
        assertNotNull(GameShapes.numberTile)
        assertNotNull(GameShapes.levelCard)
        assertNotNull(GameShapes.badge)
        assertNotNull(GameShapes.pill)
        assertNotNull(GameShapes.avatarFrame)
    }

    @Test
    fun testComponentEnums() {
        assertEquals(5, NumberTileState.values().size)
        assertTrue(NumberTileState.values().contains(NumberTileState.START))
        assertTrue(NumberTileState.values().contains(NumberTileState.FINAL))

        assertEquals(4, AvatarSize.values().size)
        assertEquals(36.dp, AvatarSize.SMALL.dp)
        assertEquals(48.dp, AvatarSize.MEDIUM.dp)
        assertEquals(64.dp, AvatarSize.LARGE.dp)
        assertEquals(80.dp, AvatarSize.HERO.dp)

        assertEquals(4, AvatarFrameType.values().size)
        assertEquals(6, RewardType.values().size)
        assertEquals(4, RewardTier.values().size)
        assertEquals(2, ProgressBarStyle.values().size)
    }
}
