package com.zynpath.game.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Zynpath standard spacing and touch target tokens.
 * Consistent layout margins, paddings, and accessible touch target sizes.
 */
object Spacing {
    val none: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val xxxl: Dp = 48.dp

    // Accessibility standard touch targets
    val minTouchTarget: Dp = 48.dp
    val comfortableTouchTarget: Dp = 56.dp

    // Screen padding
    val screenHorizontal: Dp = 16.dp
    val screenVertical: Dp = 16.dp
    val cardPadding: Dp = 16.dp
}
