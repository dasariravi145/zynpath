package com.zynpath.game.core.designsystem.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Zynpath Premium Game Surface, Gradient & Shape Tokens (Prompt 01/24)
 * Provides cohesive visual styling across arcade panels, tactile buttons, glowing tiles, and cards.
 */
object GameBrushes {
    /**
     * Signature primary action button gradient: Radiant Gold to Vibrant Orange.
     */
    val primaryButton: Brush = Brush.verticalGradient(
        colors = listOf(GameGoldHighlight, GameGold, GameOrangeAccent)
    )

    /**
     * Primary button pressed state gradient.
     */
    val primaryButtonPressed: Brush = Brush.verticalGradient(
        colors = listOf(GameGold, GameOrangeAccent, Color(0xFFE06C10))
    )

    /**
     * Primary button border highlight.
     */
    val primaryButtonBorder: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF7D9), GameGoldHighlight)
    )

    /**
     * Secondary action button subtle glassmorphic fill.
     */
    val secondaryButton: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF14244E), Color(0xFF0C1735))
    )

    /**
     * Secondary button border highlight with electric cyan touch.
     */
    val secondaryButtonBorder: Brush = Brush.verticalGradient(
        colors = listOf(GameElectricCyan, GameRoyalBlue)
    )

    /**
     * Atmospheric full-screen background gradient.
     */
    val screenBackground: Brush = Brush.verticalGradient(
        colors = listOf(
            GameMidnightBlue,
            GameDeepNavy,
            Color(0xFF040A18)
        )
    )

    /**
     * Ambient celestial glow centered on upper screen / hero areas.
     */
    val ambientRoyalGlow: Brush = Brush.radialGradient(
        colors = listOf(
            GameRoyalBlue.copy(alpha = 0.40f),
            Color.Transparent
        ),
        radius = 800f
    )

    /**
     * High-energy cyan path and highlight glow.
     */
    val cyanGlow: Brush = Brush.horizontalGradient(
        colors = listOf(GameElectricCyan, GameBrightBlue)
    )

    /**
     * Golden achievement & currency glow.
     */
    val goldGlow: Brush = Brush.horizontalGradient(
        colors = listOf(GameGoldHighlight, GameGold)
    )

    /**
     * Premium glassmorphism panel background with translucent deep navy.
     */
    val panelGlass: Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xEE101D3C),
            Color(0xF807142D)
        )
    )

    /**
     * Delicate glowing panel border.
     */
    val panelBorder: Brush = Brush.verticalGradient(
        colors = listOf(
            GameElectricCyan.copy(alpha = 0.55f),
            GameRoyalBlue.copy(alpha = 0.40f),
            GameElectricCyan.copy(alpha = 0.20f)
        )
    )

    /**
     * Default unvisited number tile background.
     */
    val tileUnvisited: Brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF16254E), Color(0xFF0B142E))
    )

    /**
     * Active / Start checkpoint number tile gradient.
     */
    val tileActive: Brush = Brush.verticalGradient(
        colors = listOf(GameElectricCyan, GameBrightBlue)
    )

    /**
     * Final checkpoint number tile gradient.
     */
    val tileFinal: Brush = Brush.verticalGradient(
        colors = listOf(GameGoldHighlight, GameGold)
    )

    /**
     * Progress bar fill gradient.
     */
    val progressCyan: Brush = Brush.horizontalGradient(
        colors = listOf(GameElectricCyan, GameBrightBlue)
    )

    /**
     * Gold progress bar fill gradient.
     */
    val progressGold: Brush = Brush.horizontalGradient(
        colors = listOf(GameGoldHighlight, GameOrangeAccent)
    )
}

/**
 * Shape tokens tailored for modern mobile game UI.
 */
object GameShapes {
    val card: CornerBasedShape = RoundedCornerShape(20.dp)
    val panel: CornerBasedShape = RoundedCornerShape(22.dp)
    val dialog: CornerBasedShape = RoundedCornerShape(24.dp)
    val buttonPrimary: CornerBasedShape = RoundedCornerShape(16.dp)
    val buttonSecondary: CornerBasedShape = RoundedCornerShape(16.dp)
    val numberTile: CornerBasedShape = RoundedCornerShape(14.dp)
    val levelCard: CornerBasedShape = RoundedCornerShape(18.dp)
    val badge: CornerBasedShape = RoundedCornerShape(10.dp)
    val pill: CornerBasedShape = RoundedCornerShape(999.dp)
    val avatarFrame: CornerBasedShape = CircleShape
}

/**
 * Game border tokens.
 */
object GameBorders {
    val primaryButton: BorderStroke = BorderStroke(1.5.dp, GameBrushes.primaryButtonBorder)
    val secondaryButton: BorderStroke = BorderStroke(1.5.dp, GameBrushes.secondaryButtonBorder)
    val panel: BorderStroke = BorderStroke(1.2.dp, GameBrushes.panelBorder)
    val cardSubtle: BorderStroke = BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.35f))
    val tileUnvisited: BorderStroke = BorderStroke(1.2.dp, GameRoyalBlue.copy(alpha = 0.5f))
    val tileActive: BorderStroke = BorderStroke(2.dp, GameElectricCyan)
    val tileFinal: BorderStroke = BorderStroke(2.dp, GameGoldHighlight)
    val avatarGold: BorderStroke = BorderStroke(2.5.dp, GameBrushes.goldGlow)
    val avatarCyan: BorderStroke = BorderStroke(2.5.dp, GameBrushes.cyanGlow)
}
