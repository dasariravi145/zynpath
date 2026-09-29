package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography

/**
 * Authoritative Zynpath primary action button featuring radiant Gold-to-Orange gradients,
 * a tactile 3D beveled rim, energetic spring press-scaling, and accessible screen-reader semantics.
 *
 * @param text The button action label.
 * @param onClick Triggered upon button activation.
 * @param modifier Custom modifier for layout/sizing.
 * @param enabled Whether interaction is active.
 * @param isLoading When true, displays a high-contrast circular progress indicator.
 * @param height Height of the button (minimum 48dp for accessibility, defaults to 54dp).
 * @param leadingIcon Optional leading composable slot.
 * @param trailingIcon Optional trailing composable slot.
 */
@Composable
fun GamePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isReducedMotion: Boolean = false,
    height: Dp = 54.dp,
    fillMaxWidth: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed, isReducedMotion) {
        if (isReducedMotion) {
            scale.snapTo(1f)
        } else {
            val target = if (isPressed && enabled && !isLoading) 0.96f else 1f
            scale.animateTo(
                targetValue = target,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val shape = GameShapes.buttonPrimary

    Box(
        modifier = modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier.wrapContentWidth())
            .height(height)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .semantics {
                role = Role.Button
                contentDescription = if (isLoading) "$text, loading" else text
            }
            .clip(shape)
            // 3D Bottom Lip / Shadow bevel
            .drawBehind {
                if (enabled) {
                    val shadowHeight = 4.dp.toPx()
                    drawRoundRect(
                        color = Color(0xFFC75D00), // Deep burnt orange base shadow
                        topLeft = Offset(0f, size.height - shadowHeight),
                        size = Size(size.width, shadowHeight),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                }
            }
            .then(
                if (enabled) {
                    Modifier
                        .background(if (isPressed) GameBrushes.primaryButtonPressed else GameBrushes.primaryButton)
                        .border(GameBorders.primaryButton, shape)
                } else {
                    Modifier
                        .background(Color(0xFF142344))
                        .border(1.dp, Color(0xFF1D356A), shape)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = GameDeepNavy,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                leadingIcon?.let {
                    it()
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = text.uppercase(),
                    style = GameTypography.buttonPrimary.copy(
                        color = if (enabled) GameDeepNavy else Color(0xFF5A72A0)
                    )
                )

                trailingIcon?.let {
                    Spacer(modifier = Modifier.width(10.dp))
                    it()
                }
            }
        }
    }
}
