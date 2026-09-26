package com.zynpath.game.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Zynpath elevation and surface hierarchy tokens.
 */
object Elevation {
    val level0: Dp = 0.dp
    val level1: Dp = 2.dp
    val level2: Dp = 4.dp
    val level3: Dp = 8.dp
    val level4: Dp = 12.dp
    val level5: Dp = 16.dp

    // Component standard elevations
    val card: Dp = level1
    val elevatedCard: Dp = level2
    val modalDialog: Dp = level4
    val bottomSheet: Dp = level5
}
