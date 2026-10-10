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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * 1. Primary Gold Button:
 * Radiant Gold-to-Orange gradient, tactile 3D bevel shadow, soft mirror top sheen, centered text.
 */
@Composable
fun ZynpathPrimaryGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 52.dp,
    fontSize: Int = 16,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        val target = if (isPressed && enabled && !isLoading) 0.96f else 1f
        scale.animateTo(
            targetValue = target,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
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
            .then(
                if (enabled) {
                    Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = if (isPressed) {
                                    listOf(GameGold, GameOrangeAccent, Color(0xFFE06C10))
                                } else {
                                    listOf(GameGoldHighlight, GameGold, GameOrangeAccent)
                                }
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.verticalGradient(listOf(Color(0xFFFFF7D9), GameGoldHighlight)),
                            shape
                        )
                        .glossyTopHighlight(cornerRadius = 16.dp, highlightAlpha = 0.22f)
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
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
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
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text.uppercase(),
                    style = GameTypography.buttonPrimary.copy(
                        fontSize = fontSize.sp,
                        color = if (enabled) GameDeepNavy else Color(0xFF5A72A0),
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 2. Secondary Cyan Button:
 * Translucent royal-blue glass, electric cyan glowing border, top mirror shine, centered text.
 */
@Composable
fun ZynpathSecondaryCyanButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    height: Dp = 48.dp,
    fontSize: Int = 14,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        val target = if (isPressed && enabled && !isLoading) 0.97f else 1f
        scale.animateTo(
            targetValue = target,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
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
            .then(
                if (enabled) {
                    Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF14244E), Color(0xFF0C1735))
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.verticalGradient(listOf(GameElectricCyan, GameRoyalBlue)),
                            shape
                        )
                        .glossyTopHighlight(cornerRadius = 14.dp, highlightAlpha = 0.16f)
                } else {
                    Modifier
                        .background(Color(0xFF0F1A35))
                        .border(1.dp, Color(0xFF1B2E58), shape)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = GameElectricCyan,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                leadingIcon?.let {
                    it()
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    style = GameTypography.buttonSecondary.copy(
                        fontSize = fontSize.sp,
                        color = if (enabled) GameWhite else Color(0xFF5A72A0),
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 3. Neutral Outline Button:
 * Sleek translucent slate glass background with crisp outline and centered white text.
 */
@Composable
fun ZynpathNeutralOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 44.dp,
    fontSize: Int = 13,
    borderColor: Color = GameRoyalBlue.copy(alpha = 0.5f),
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(
                if (isPressed) Color(0x33102454) else Color(0x1A102454)
            )
            .border(1.dp, borderColor, shape)
            .glossyTopHighlight(cornerRadius = 12.dp, highlightAlpha = 0.12f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                style = GameTypography.labelMedium.copy(
                    fontSize = fontSize.sp,
                    color = if (enabled) GameWhite else GameSecondaryText,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 4. Compact Icon Button:
 * Circular button with accessible touch target (>=44dp) and glossy rim.
 */
@Composable
fun ZynpathCompactIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = GameWhite,
    borderColor: Color = GameRoyalBlue.copy(alpha = 0.6f),
    backgroundColor: Color = Color(0xCC101D3C),
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 44.dp, minHeight = 44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(1.2.dp, borderColor, CircleShape)
                .glossyTopHighlight(cornerRadius = (size.value / 2).dp, highlightAlpha = 0.20f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(size * 0.52f)
            )
        }
    }
}

/**
 * 5. Dialog Action Button:
 * Tailored specifically for compact modal dialogs (44dp height, centered single-line text).
 */
@Composable
fun ZynpathDialogActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    if (isPrimary) {
        ZynpathPrimaryGoldButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            isLoading = isLoading,
            height = 44.dp,
            fontSize = 14
        )
    } else {
        ZynpathSecondaryCyanButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            isLoading = isLoading,
            height = 44.dp,
            fontSize = 13
        )
    }
}

// ============================================================================
// Backward-compatible delegates
// ============================================================================

@Composable
fun ZynpathPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    GamePrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon
    )
}

@Composable
fun ZynpathSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    GameSecondaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF07142D)
@Composable
private fun ZynpathButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ZynpathPrimaryGoldButton(text = "Play Solo (Primary Gold)", onClick = {})
        ZynpathSecondaryCyanButton(text = "World Select (Secondary Cyan)", onClick = {})
        ZynpathNeutralOutlineButton(text = "Cancel (Neutral Outline)", onClick = {})
        ZynpathDialogActionButton(text = "CLAIM REWARD", onClick = {}, isPrimary = true)
        ZynpathDialogActionButton(text = "LATER", onClick = {}, isPrimary = false)
    }
}
