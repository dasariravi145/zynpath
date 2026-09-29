package com.zynpath.game.feature.achievement

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.achievement.AchievementCategory
import com.zynpath.game.core.achievement.AchievementProgress
import com.zynpath.game.core.designsystem.components.GameLoadingIndicator
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GameProgressBar
import com.zynpath.game.core.designsystem.components.GameRewardBadge
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.components.ProgressBarStyle
import com.zynpath.game.core.designsystem.components.RewardTier
import com.zynpath.game.core.designsystem.components.RewardType
import com.zynpath.game.core.designsystem.components.StarCounter
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.layout.rememberZynpathWindowInfo
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium Rewards & Achievements Screen presenting authoritative player milestones,
 * unlocked trophies, star progression, and verified game statistics.
 *
 * Implements Prompt 01 Design System, Prompt 17 Section 24, and Prompt 09/24:
 * - Deep navy fantasy background with star dust
 * - Royal-blue game panels with subtle glowing borders
 * - Electric-cyan and radiant gold reward accents
 * - Original lightweight Compose trophy illustration
 * - Authoritative repository-backed values (stars, milestones)
 * - Clear distinction between earned, in-progress, and locked achievements
 * - Responsive phone layout and reduced-motion support
 */
@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowInfo = rememberZynpathWindowInfo()
    var selectedDetailItem by remember { mutableStateOf<AchievementProgress?>(null) }

    if (selectedDetailItem != null) {
        AchievementDetailDialog(
            item = selectedDetailItem!!,
            isReducedMotion = uiState.isReducedMotion,
            onDismiss = { selectedDetailItem = null }
        )
    }

    GameScreenBackground(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                GameTopBar(
                    title = "REWARDS & ACHIEVEMENTS",
                    subtitle = "${uiState.unlockedCount} of ${uiState.totalCount} Unlocked",
                    onBackClick = onBackClick,
                    actions = {
                        StarCounter(currentStars = uiState.totalStars)
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GameLoadingIndicator(message = "Loading verified achievements...")
                    }
                } else {
                    val maxContentWidth = windowInfo.maxContentWidth
                    val isBoundedWidth = maxContentWidth != androidx.compose.ui.unit.Dp.Unspecified && !windowInfo.useSideBySideLayout

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (isBoundedWidth) Modifier.widthIn(max = maxContentWidth)
                                else Modifier
                            )
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Hero Section: Trophy Illustration & Overall Progress
                        item(key = "hero_section") {
                            RewardsHeroHeader(
                                unlockedCount = uiState.unlockedCount,
                                totalCount = uiState.totalCount,
                                totalStars = uiState.totalStars,
                                completionPercentage = uiState.completionPercentage,
                                isReducedMotion = uiState.isReducedMotion
                            )
                        }

                        // 2. Authoritative Reward Highlights
                        item(key = "reward_badges") {
                            RewardHighlightsRow(
                                unlockedCount = uiState.unlockedCount,
                                totalStars = uiState.totalStars,
                                completionPercentage = uiState.completionPercentage
                            )
                        }

                        // 3. Category Filter Pills
                        item(key = "category_tabs") {
                            CategoryFilterBar(
                                selectedCategory = uiState.selectedCategory,
                                onSelectCategory = { viewModel.selectCategory(it) }
                            )
                        }

                        // 4. Achievement Cards or Empty State
                        if (uiState.achievements.isEmpty()) {
                            item(key = "empty_achievements") {
                                EmptyAchievementsPanel(selectedCategory = uiState.selectedCategory)
                            }
                        } else {
                            items(
                                items = uiState.achievements,
                                key = { it.definition.id }
                            ) { item ->
                                PremiumAchievementCard(
                                    item = item,
                                    isReducedMotion = uiState.isReducedMotion,
                                    onClick = { selectedDetailItem = item }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Hero Header featuring an original lightweight Compose trophy illustration
 * and authoritative milestone completion progress.
 */
@Composable
private fun RewardsHeroHeader(
    unlockedCount: Int,
    totalCount: Int,
    totalStars: Int,
    completionPercentage: Float,
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 18.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Trophy Artwork
            TrophyIllustration(
                isReducedMotion = isReducedMotion,
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 6.dp)
            )

            Text(
                text = "PATHFINDER TROPHIES",
                style = GameTypography.screenHeading.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = GameGoldHighlight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Conquer levels, perfect paths, and unlock verified milestones",
                style = GameTypography.secondaryInfo.copy(fontSize = 12.sp),
                color = GameSecondaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Progress",
                    style = GameTypography.labelMedium.copy(
                        color = GameWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "${(completionPercentage * 100).toInt()}% COMPLETE",
                    style = GameTypography.labelMedium.copy(
                        color = AccentGold,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            GameProgressBar(
                progress = completionPercentage,
                height = 12.dp,
                style = ProgressBarStyle.GOLD,
                isReducedMotion = isReducedMotion
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$unlockedCount of $totalCount Milestones",
                    style = GameTypography.secondaryInfo.copy(color = TextMuted)
                )

                Text(
                    text = "$totalStars Stars Earned",
                    style = GameTypography.secondaryInfo.copy(
                        color = GameElectricCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * Original lightweight Compose graphic illustration of a glowing gold trophy
 * with orbital radial glow and star dust accents.
 */
@Composable
private fun TrophyIllustration(
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(isReducedMotion) {
        if (!isReducedMotion) {
            rotationAnim.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 18000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Compose Canvas Rays and Glowing Rings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Radiant background circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x44FFC247),
                        Color(0x1521D4FD),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )

            // Outer dashed/orbital ring
            drawCircle(
                color = Color(0x33FFC247),
                radius = radius * 0.88f,
                center = center,
                style = Stroke(width = 2f)
            )

            // Inner glowing rim
            drawCircle(
                color = Color(0x5521D4FD),
                radius = radius * 0.76f,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // Radiant star rays
            val rayCount = 8
            val currentRotation = rotationAnim.value
            for (i in 0 until rayCount) {
                val angleDeg = currentRotation + (i * 360f / rayCount)
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val start = Offset(
                    x = (center.x + (radius * 0.65f) * cos(angleRad)).toFloat(),
                    y = (center.y + (radius * 0.65f) * sin(angleRad)).toFloat()
                )
                val end = Offset(
                    x = (center.x + (radius * 0.88f) * cos(angleRad)).toFloat(),
                    y = (center.y + (radius * 0.88f) * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = Color(0x40FFD54F),
                    start = start,
                    end = end,
                    strokeWidth = 2f
                )
            }
        }

        // Center Trophy Emblem
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xEE1A2B55),
                            Color(0xEE0D1733)
                        )
                    )
                )
                .border(2.dp, GameGoldHighlight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_game_trophy),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

/**
 * Authoritative summary row using existing GameRewardBadge components.
 */
@Composable
private fun RewardHighlightsRow(
    unlockedCount: Int,
    totalStars: Int,
    completionPercentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GameRewardBadge(
            type = RewardType.TROPHY,
            title = "Trophies",
            value = "$unlockedCount Unlocked",
            tier = RewardTier.GOLD,
            modifier = Modifier.weight(1f)
        )

        GameRewardBadge(
            type = RewardType.STAR,
            title = "Solo Stars",
            value = "$totalStars Earned",
            tier = RewardTier.DIAMOND,
            modifier = Modifier.weight(1f)
        )

        GameRewardBadge(
            type = RewardType.CROWN,
            title = "Mastery",
            value = "${(completionPercentage * 100).toInt()}% Done",
            tier = RewardTier.SILVER,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Category Filter Bar organized into modern arcade tabs.
 */
@Composable
private fun CategoryFilterBar(
    selectedCategory: AchievementCategory,
    onSelectCategory: (AchievementCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AchievementCategory.entries) { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(GameShapes.pill)
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF1D4ED8),
                                        Color(0xFF0F3A99)
                                    )
                                )
                            )
                        } else {
                            Modifier.background(Color(0xEE0B1736))
                        }
                    )
                    .border(
                        width = 1.2.dp,
                        color = if (isSelected) GameElectricCyan else Color(0x3321D4FD),
                        shape = GameShapes.pill
                    )
                    .clickable { onSelectCategory(category) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.displayName,
                    style = GameTypography.labelMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) GameWhite else TextSecondary
                    )
                )
            }
        }
    }
}

/**
 * Premium Game Achievement Card presenting authoritative progress,
 * with three visual states: Earned, In Progress, and Locked.
 */
@Composable
private fun PremiumAchievementCard(
    item: AchievementProgress,
    isReducedMotion: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val def = item.definition
    val isUnlocked = item.isUnlocked
    val isInProgress = !isUnlocked && item.currentProgress > 0

    val dateStr = remember(item.unlockedAt) {
        item.unlockedAt?.let {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
        }
    }

    val stateText = when {
        isUnlocked -> "Unlocked"
        isInProgress -> "In progress"
        else -> "Locked"
    }

    val a11yDescription = buildString {
        append(def.title)
        append(". ")
        append(stateText)
        if (isUnlocked && dateStr != null) {
            append(" on ")
            append(dateStr)
        }
        append(". ")
        append(def.description)
        append(". Progress: ")
        append(item.formattedProgress)
    }

    // Border and background styling based on state
    val containerBackground = when {
        isUnlocked -> Color(0xEE102046)
        isInProgress -> Color(0xDD0E1B3D)
        else -> Color(0xCC091226)
    }

    val borderColor = when {
        isUnlocked -> GameGoldHighlight.copy(alpha = 0.65f)
        isInProgress -> GameElectricCyan.copy(alpha = 0.45f)
        else -> Color(0xFF162544)
    }

    val iconBorderColor = when {
        isUnlocked -> GameGoldHighlight
        isInProgress -> GameElectricCyan
        else -> TextMuted.copy(alpha = 0.3f)
    }

    val iconBgColor = when {
        isUnlocked -> GameGold.copy(alpha = 0.15f)
        isInProgress -> GameElectricCyan.copy(alpha = 0.12f)
        else -> Color(0xFF070E1E)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(GameShapes.panel)
            .background(containerBackground)
            .border(1.2.dp, borderColor, GameShapes.panel)
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = a11yDescription
            }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Icon Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBgColor)
                    .border(1.2.dp, iconBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_game_trophy),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(26.dp)
                    )
                } else if (isInProgress) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_game_star),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text Info & Progress
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = def.title,
                        style = GameTypography.labelMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) GameWhite else if (isInProgress) GameWhite else TextMuted
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isUnlocked) {
                        StatusBadge(text = "UNLOCKED", color = GameGold)
                    } else if (isInProgress) {
                        Text(
                            text = item.formattedProgress,
                            style = GameTypography.secondaryInfo.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameElectricCyan
                            )
                        )
                    } else {
                        Text(
                            text = "LOCKED",
                            style = GameTypography.secondaryInfo.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = def.description,
                    style = GameTypography.secondaryInfo.copy(
                        fontSize = 12.sp,
                        color = if (isUnlocked) GameSecondaryText else TextMuted,
                        lineHeight = 16.sp
                    )
                )

                // Multi-step progress bar if in progress
                if (isInProgress && def.targetValue > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GameProgressBar(
                        progress = item.progressFraction,
                        height = 6.dp,
                        style = ProgressBarStyle.CYAN,
                        isReducedMotion = isReducedMotion
                    )
                }

                // Unlock date tag
                if (isUnlocked && dateStr != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Unlocked $dateStr",
                        style = GameTypography.secondaryInfo.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ForestMint
                        )
                    )
                }
            }
        }
    }
}

/**
 * Empty Category State with game-style graphics and informative message.
 */
@Composable
private fun EmptyAchievementsPanel(
    selectedCategory: AchievementCategory,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0A1428))
                    .border(1.2.dp, Color(0x3321D4FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "No Achievements in ${selectedCategory.displayName}",
                style = GameTypography.screenHeading.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = GameWhite,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Solve puzzles in Solo mode, complete canonical worlds, and participate in Daily Challenges to earn verified achievements.",
                style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, lineHeight = 18.sp),
                color = GameSecondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Accessible achievement detail modal presenting complete unlock criteria,
 * progress bar, and timestamp verification.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementDetailDialog(
    item: AchievementProgress,
    isReducedMotion: Boolean,
    onDismiss: () -> Unit
) {
    val def = item.definition
    val isUnlocked = item.isUnlocked
    val isInProgress = !isUnlocked && item.currentProgress > 0

    val dateStr = remember(item.unlockedAt) {
        item.unlockedAt?.let {
            SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(it))
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(GameShapes.panel)
                .background(Color(0xFA0B1633))
                .border(1.5.dp, if (isUnlocked) GameGoldHighlight else GameElectricCyan, GameShapes.panel)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) GameGold.copy(alpha = 0.18f)
                            else if (isInProgress) GameElectricCyan.copy(alpha = 0.15f)
                            else Color(0xFF091224)
                        )
                        .border(
                            1.5.dp,
                            if (isUnlocked) GameGoldHighlight else if (isInProgress) GameElectricCyan else TextMuted,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUnlocked) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_game_trophy),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(34.dp)
                        )
                    } else if (isInProgress) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_game_star),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = def.title,
                    style = GameTypography.screenHeading.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GameWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                StatusBadge(
                    text = def.category.displayName,
                    color = if (isUnlocked) ForestMint else PathCyanGlow
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = def.description,
                    style = GameTypography.secondaryInfo.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = GameSecondaryText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Progress Info Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF081024))
                        .border(1.dp, Color(0xFF162544), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status",
                            style = GameTypography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        )
                        Text(
                            text = if (isUnlocked) "UNLOCKED" else if (isInProgress) "IN PROGRESS" else "LOCKED",
                            style = GameTypography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) GameGold else if (isInProgress) GameElectricCyan else TextMuted
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Progress",
                            style = GameTypography.secondaryInfo.copy(color = TextMuted)
                        )
                        Text(
                            text = item.formattedProgress,
                            style = GameTypography.secondaryInfo.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = PathCyanGlow
                            )
                        )
                    }

                    if (def.targetValue > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        GameProgressBar(
                            progress = item.progressFraction,
                            height = 6.dp,
                            style = if (isUnlocked) ProgressBarStyle.GOLD else ProgressBarStyle.CYAN,
                            isReducedMotion = isReducedMotion
                        )
                    }

                    if (isUnlocked && dateStr != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Unlocked On",
                                style = GameTypography.secondaryInfo.copy(color = TextMuted)
                            )
                            Text(
                                text = dateStr,
                                style = GameTypography.secondaryInfo.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = ForestMint
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                GameSecondaryButton(
                    text = "CLOSE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
