package com.zynpath.game.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.theme.RefCyanGlow
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefFacebookBlue
import com.zynpath.game.core.designsystem.theme.RefGoldGradientEnd
import com.zynpath.game.core.designsystem.theme.RefGoldGradientStart
import com.zynpath.game.core.designsystem.theme.RefGoldPrimary
import com.zynpath.game.core.designsystem.theme.RefGreenClaim
import com.zynpath.game.core.designsystem.theme.RefGreenReady
import com.zynpath.game.core.designsystem.theme.RefNavyBorder
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavyElevated
import com.zynpath.game.core.designsystem.theme.RefNavySurface
import com.zynpath.game.core.designsystem.theme.RefRedBadge
import com.zynpath.game.core.designsystem.theme.RefSilverPill
import com.zynpath.game.core.designsystem.theme.RefStarGold
import com.zynpath.game.core.designsystem.theme.RefTextMuted

/**
 * Master Pill Button variants matching the 1536x1024 composite reference.
 */
enum class ZynpathPillStyle {
    GOLD,       // "PLAY", "Create Room", "Send Invitation (2)", "Play Again"
    GREEN,      // "Claim"
    FACEBOOK,   // "Continue with Facebook", "Connect Facebook"
    SILVER,     // "Play as Guest"
    BLUE        // "Back to Home", "Invite"
}

@Composable
fun ZynpathMasterPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ZynpathPillStyle = ZynpathPillStyle.GOLD,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    fontSize: Int = 18
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "buttonScale")

    val bgBrush = when (style) {
        ZynpathPillStyle.GOLD -> Brush.verticalGradient(
            colors = listOf(RefGoldGradientStart, RefGoldGradientEnd)
        )
        ZynpathPillStyle.GREEN -> Brush.verticalGradient(
            colors = listOf(Color(0xFF26E074), RefGreenClaim)
        )
        ZynpathPillStyle.FACEBOOK -> Brush.verticalGradient(
            colors = listOf(Color(0xFF2988FF), RefFacebookBlue)
        )
        ZynpathPillStyle.SILVER -> Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), RefSilverPill)
        )
        ZynpathPillStyle.BLUE -> Brush.verticalGradient(
            colors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
        )
    }

    val textColor = when (style) {
        ZynpathPillStyle.GOLD -> Color(0xFF1A0E00)
        ZynpathPillStyle.SILVER -> Color(0xFF0F1E36)
        else -> Color.White
    }

    val shadowColor = when (style) {
        ZynpathPillStyle.GOLD -> RefGoldPrimary.copy(alpha = 0.4f)
        ZynpathPillStyle.GREEN -> RefGreenClaim.copy(alpha = 0.4f)
        ZynpathPillStyle.FACEBOOK -> RefFacebookBlue.copy(alpha = 0.4f)
        ZynpathPillStyle.SILVER -> Color.Black.copy(alpha = 0.25f)
        ZynpathPillStyle.BLUE -> Color(0xFF1565C0).copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(height / 2), spotColor = shadowColor)
            .clip(RoundedCornerShape(height / 2))
            .background(if (enabled) bgBrush else Brush.verticalGradient(listOf(Color(0xFF4A5568), Color(0xFF2D3748))))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .height(height)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = if (enabled) textColor else Color(0xFFA0AEC0),
                fontSize = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Currency Capsule (Coins / Stars) as seen in Home and Rewards screens.
 */
@Composable
fun ZynpathMasterCurrencyPill(
    iconPainter: Painter,
    amount: String,
    modifier: Modifier = Modifier,
    iconTint: Color? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RefNavySurface)
            .border(1.2.dp, RefNavyBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (iconTint != null) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Image(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = amount,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ZynpathMasterCurrencyPill(
    iconRes: Int,
    amount: String,
    modifier: Modifier = Modifier,
    iconTint: Color? = null
) {
    ZynpathMasterCurrencyPill(
        iconPainter = painterResource(id = iconRes),
        amount = amount,
        modifier = modifier,
        iconTint = iconTint
    )
}

/**
 * Master Header Bar matching reference panels with circular back button and clean title.
 */
@Composable
fun ZynpathMasterHeaderBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(RefNavySurface)
                .border(1.2.dp, RefNavyBorder, CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            textAlign = TextAlign.Center
        )

        if (trailingAction != null) {
            trailingAction()
        } else {
            Spacer(modifier = Modifier.size(38.dp))
        }
    }
}

/**
 * Segmented Tab Selector (World 1, World 2, World 3 / Daily, Missions, Achievements / Facebook Friends, Invitations).
 */
@Composable
fun ZynpathMasterSegmentedTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(RefNavySurface)
            .border(1.dp, RefNavyBorder, RoundedCornerShape(22.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            val bgModifier = if (isSelected) {
                Modifier.background(
                    Brush.verticalGradient(listOf(RefGoldGradientStart, RefGoldGradientEnd)),
                    RoundedCornerShape(18.dp)
                )
            } else {
                Modifier.background(Color.Transparent)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .then(bgModifier)
                    .clickable { onTabSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isSelected) Color(0xFF1A0E00) else Color.White,
                    fontSize = if (tabs.size > 4) 10.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Action Toolbar Button with Notification Badge (Hint, Shuffle, Undo).
 */
@Composable
fun ZynpathMasterActionBadgeButton(
    iconPainter: Painter,
    label: String,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = RefCyanNeon
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(RefNavyElevated)
                .border(1.5.dp, RefNavyBorder, RoundedCornerShape(16.dp))
                .clickable { onClick() }
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center)
            )

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(RefRedBadge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ZynpathMasterActionBadgeButton(
    icon: ImageVector,
    label: String,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = RefCyanNeon
) {
    ZynpathMasterActionBadgeButton(
        iconPainter = rememberVectorPainter(icon),
        label = label,
        badgeCount = badgeCount,
        onClick = onClick,
        modifier = modifier,
        iconTint = iconTint
    )
}

