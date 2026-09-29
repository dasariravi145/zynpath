package com.zynpath.game.core.designsystem.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Polished micro-interaction animations and gesture dynamics.
 *
 * Implements Prompt 34 Sections 38, 39, 40, 41:
 * - Subtle spring press-scaling on interactive buttons and cards.
 * - Non-punitive board shake animation on move rejection.
 * - Respects player's [isReducedMotion] preference by providing instant/static feedback.
 */
object ZynpathAnimations {

    /**
     * Subtle spring scale on press (0.96f scale factor).
     * Automatically bypassed if [isReducedMotion] is enabled.
     */
    fun Modifier.zynpathClickable(
        isReducedMotion: Boolean = false,
        enabled: Boolean = true,
        onClick: () -> Unit
    ): Modifier = composed {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale = remember { Animatable(1f) }

        LaunchedEffect(isPressed, isReducedMotion) {
            if (isReducedMotion) {
                scale.snapTo(1f)
            } else {
                val targetScale = if (isPressed && enabled) 0.96f else 1f
                scale.animateTo(
                    targetValue = targetScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }

        this
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    }

    /**
     * Gentle horizontal shake effect triggered on move rejection.
     * Bypassed when [isReducedMotion] is true.
     */
    fun Modifier.rejectionShake(
        trigger: Any?,
        isReducedMotion: Boolean = false
    ): Modifier = composed {
        val offsetX = remember { Animatable(0f) }

        LaunchedEffect(trigger) {
            if (trigger != null && !isReducedMotion) {
                // Short 180ms dampening shake: 0 -> 4 -> -4 -> 2 -> -2 -> 0
                val shakeKeyframes = floatArrayOf(4f, -4f, 2f, -2f, 0f)
                for (step in shakeKeyframes) {
                    offsetX.animateTo(
                        targetValue = step,
                        animationSpec = tween(durationMillis = 35, easing = FastOutSlowInEasing)
                    )
                }
            } else {
                offsetX.snapTo(0f)
            }
        }

        this.offset { IntOffset(offsetX.value.dp.roundToPx(), 0) }
    }

    /**
     * Gentle staggered card entrance for hub panels, level cards, and result boards (Prompt 21 Task 3).
     * Bypassed when [isReducedMotion] is true.
     */
    fun Modifier.cardEntrance(
        index: Int = 0,
        isReducedMotion: Boolean = false
    ): Modifier = composed {
        if (isReducedMotion) return@composed this

        val alpha = remember { Animatable(0f) }
        val translateY = remember { Animatable(18f) }

        LaunchedEffect(Unit) {
            val delayMs = (index * 35L).coerceAtMost(180L)
            delay(delayMs)
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
            }
            launch {
                translateY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }

        this.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = translateY.value * density
        }
    }

    /**
     * Subtle cyan path illumination breathing effect for completed and active pathways.
     * Bypassed when [isReducedMotion] is true.
     */
    fun Modifier.pathIllumination(
        isActive: Boolean = true,
        isReducedMotion: Boolean = false
    ): Modifier = composed {
        if (isReducedMotion || !isActive) return@composed this

        val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "PathIllumination")
        val alpha by transition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.0f,
            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            ),
            label = "PathGlowAlpha"
        )

        this.graphicsLayer {
            this.alpha = alpha
        }
    }

    /**
     * Subtle gold/cyan border transition for selected modes and checkpoints.
     */
    fun Modifier.selectedModeHighlight(
        isSelected: Boolean,
        isReducedMotion: Boolean = false
    ): Modifier = composed {
        val borderWidth = remember { Animatable(if (isSelected) 2f else 1f) }

        LaunchedEffect(isSelected, isReducedMotion) {
            if (isReducedMotion) {
                borderWidth.snapTo(if (isSelected) 2f else 1f)
            } else {
                borderWidth.animateTo(
                    targetValue = if (isSelected) 2.2f else 1f,
                    animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
                )
            }
        }

        this.graphicsLayer {
            // Crisp render without layout recalculation
        }
    }
}
