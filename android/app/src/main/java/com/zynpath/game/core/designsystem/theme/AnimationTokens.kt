package com.zynpath.game.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing

/**
 * Zynpath animation duration and easing tokens.
 * Supports smooth, restrained motion that respects reduced-motion preferences.
 */
object AnimationTokens {
    const val durationInstant: Int = 0
    const val durationFast: Int = 150
    const val durationNormal: Int = 300
    const val durationSlow: Int = 450
    const val durationTutorialStep: Int = 400
    const val durationPathSegment: Int = 80

    val standardEasing: Easing = FastOutSlowInEasing
    val linearEasing: Easing = LinearEasing
    val overshootEasing: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
}
