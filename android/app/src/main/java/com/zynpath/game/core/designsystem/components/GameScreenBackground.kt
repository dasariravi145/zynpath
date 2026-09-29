package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import kotlin.random.Random

private data class StarParticle(
    val relX: Float,
    val relY: Float,
    val radius: Float,
    val color: Color,
    val baseAlpha: Float,
    val blinkPhase: Float
)

/**
 * Atmospheric game screen background with deep cinematic navy & royal blue gradients,
 * an ambient celestial glow, and shimmering procedural starfield particles.
 *
 * @param modifier Custom modifier applied to the root container.
 * @param showCelestialParticles If true, renders lightweight ambient stars in the background.
 * @param isReducedMotion If true, pauses twinkling animations for accessibility.
 * @param applyStatusBarPadding If true, applies window insets status bar padding.
 * @param applyNavigationBarPadding If true, applies window insets navigation bar padding.
 * @param content Slot for screen contents.
 */
@Composable
fun GameScreenBackground(
    modifier: Modifier = Modifier,
    showCelestialParticles: Boolean = true,
    isReducedMotion: Boolean = false,
    applyStatusBarPadding: Boolean = false,
    applyNavigationBarPadding: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StarBlink")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarPulse"
    )

    // Pre-generate a deterministic set of 30 subtle celestial particles
    val particles = remember {
        val rand = Random(42)
        List(30) {
            val isCyan = rand.nextBoolean()
            StarParticle(
                relX = rand.nextFloat(),
                relY = rand.nextFloat(),
                radius = rand.nextFloat() * 1.8f + 0.8f,
                color = if (isCyan) GameElectricCyan else GameGoldHighlight,
                baseAlpha = rand.nextFloat() * 0.4f + 0.15f,
                blinkPhase = rand.nextFloat()
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GameBrushes.screenBackground)
    ) {
        // Celestial ambient background canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Upper hero ambient glow
            drawCircle(
                brush = GameBrushes.ambientRoyalGlow,
                radius = size.width * 0.95f,
                center = Offset(size.width * 0.5f, size.height * 0.15f)
            )

            // Soft mid-lower cyan ambient glow for atmospheric depth
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(GameElectricCyan.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.65f),
                    radius = size.width * 0.85f
                ),
                radius = size.width * 0.85f,
                center = Offset(size.width * 0.5f, size.height * 0.65f)
            )

            // Subtle fantasy mountain silhouette ridges in deep navy/royal tones
            val ridgeFar = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, size.height * 0.82f)
                cubicTo(
                    size.width * 0.25f, size.height * 0.77f,
                    size.width * 0.45f, size.height * 0.85f,
                    size.width * 0.7f, size.height * 0.79f
                )
                cubicTo(
                    size.width * 0.85f, size.height * 0.75f,
                    size.width * 0.95f, size.height * 0.81f,
                    size.width, size.height * 0.80f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = ridgeFar,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0x22132A58), Color(0x110B1736)),
                    startY = size.height * 0.75f,
                    endY = size.height
                )
            )

            val ridgeNear = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, size.height * 0.89f)
                cubicTo(
                    size.width * 0.2f, size.height * 0.85f,
                    size.width * 0.42f, size.height * 0.91f,
                    size.width * 0.62f, size.height * 0.86f
                )
                cubicTo(
                    size.width * 0.8f, size.height * 0.83f,
                    size.width * 0.92f, size.height * 0.88f,
                    size.width, size.height * 0.87f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = ridgeNear,
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0x380A1832), Color(0x20070F22)),
                    startY = size.height * 0.83f,
                    endY = size.height
                )
            )

            if (showCelestialParticles) {
                particles.forEach { particle ->
                    val animatedAlpha = if (isReducedMotion) {
                        particle.baseAlpha
                    } else {
                        // Twinkle calculation
                        val wave = (pulse + particle.blinkPhase) % 1f
                        (particle.baseAlpha * (0.6f + 0.4f * wave)).coerceIn(0.05f, 0.85f)
                    }

                    drawCircle(
                        color = particle.color.copy(alpha = animatedAlpha),
                        radius = particle.radius,
                        center = Offset(
                            x = particle.relX * size.width,
                            y = particle.relY * size.height
                        )
                    )
                }
            }
        }

        // Content layer with optional inset handling
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (applyStatusBarPadding) Modifier.statusBarsPadding() else Modifier)
                .then(if (applyNavigationBarPadding) Modifier.navigationBarsPadding() else Modifier),
            content = content
        )
    }
}
