package com.zynpath.game.feature.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.zynpath.game.R
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import com.zynpath.game.core.designsystem.animation.ZynpathAnimations.cardEntrance
import com.zynpath.game.core.designsystem.components.GameBottomNavItem
import com.zynpath.game.core.designsystem.components.GameBottomNavigation
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.layout.rememberZynpathWindowInfo
import com.zynpath.game.core.feedback.rememberZynpathFeedbackCoordinator
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.components.DailyChallengeCard
import com.zynpath.game.feature.home.components.FriendsArenaCard
import com.zynpath.game.feature.home.components.HomePlayerHeader
import com.zynpath.game.feature.home.components.ProgressionMilestonesPanel
import com.zynpath.game.feature.home.components.QuickDuelCard
import com.zynpath.game.feature.home.components.SoloCampaignCard
import com.zynpath.game.feature.navigation.Screen
import kotlinx.coroutines.launch

/**
 * Premium Zynpath Home Game Hub (Prompt 04/24).
 *
 * Implements an immersive mobile puzzle game hub featuring:
 * 1. Compact Player Header with cosmetic avatar frames, level rank, and star progression.
 * 2. Centerpiece Solo Campaign card with mini illustrated number pathway and resume capability.
 * 3. Competitive 1v1 Quick Duel card with lightning VS badge and real connectivity checking.
 * 4. Friends Arena preview card with 5 player slots and informative coming soon state.
 * 5. Daily Challenge card with streak, countdown timer, and dynamic completion action.
 * 6. Rewards & Progression panel with career stars, trophy counts, and next goal milestone.
 * 7. Docked arcade GameBottomNavigation with animated selection states.
 * 8. Cinematic fantasy puzzle environment with deep navy sky, mountain silhouettes,
 *    floating platforms, glowing cyan pathways, and star particles.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSoloPlay: () -> Unit,
    onNavigateToLevels: () -> Unit,
    onNavigateToTutorial: () -> Unit,
    onNavigateToQuickDuel: () -> Unit,
    onNavigateToFriendDuel: () -> Unit,
    onNavigateToMiniLeague: () -> Unit,
    onNavigateToDailyChallenge: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFriends: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToFriendsArena: () -> Unit = {},
    onNavigateToPlayLevel: (worldId: Int, levelId: Int) -> Unit = { _, _ -> onNavigateToSoloPlay() },
    onNavigateToResume: (worldId: Int, levelId: Int) -> Unit = { _, _ -> onNavigateToSoloPlay() },
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val windowInfo = rememberZynpathWindowInfo()
    val feedbackCoordinator = rememberZynpathFeedbackCoordinator()
    var showCoinDetailsSheet by remember { mutableStateOf(false) }
    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    val bottomSheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val recentTransactions by (viewModel.walletRepository?.observeRecentTransactions(20) ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .collectAsStateWithLifecycle(emptyList())

    LaunchedEffect(Unit) {
        feedbackCoordinator.onScreenOpened()
    }

    // Connection Notice Alert Dialog
    if (uiState.userNoticeMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearUserNotice() },
            title = {
                Text(
                    text = "Connection Required",
                    style = GameTypography.screenHeading.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                )
            },
            text = {
                Text(
                    text = uiState.userNoticeMessage ?: "",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = GameSecondaryText
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearUserNotice() }) {
                    Text(
                        text = "GOT IT",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        color = GameElectricCyan
                    )
                }
            },
            containerColor = GameMidnightBlue,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        )
    }

    // Main Game Screen Container with Atmospheric Fantasy Background
    GameScreenBackground(
        showCelestialParticles = true,
        isReducedMotion = uiState.isReducedMotion,
        applyStatusBarPadding = false,
        applyNavigationBarPadding = false
    ) {
        // Layered Cinematic Background Environment (Mountains, Islands, Sky Paths)
        HomeCinematicEnvironmentCanvas(
            isReducedMotion = uiState.isReducedMotion,
            modifier = Modifier.fillMaxSize()
        )

        // Main Vertical Layout: Header (Fixed Top) + Scrollable Cards Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top: Player Header
            HomePlayerHeader(
                uiState = uiState,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToLevels = onNavigateToLevels,
                onCoinClick = { showCoinDetailsSheet = true }
            )

            // Scrollable Hub Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (windowInfo.maxContentWidth != androidx.compose.ui.unit.Dp.Unspecified && !windowInfo.useSideBySideLayout) {
                                Modifier.widthIn(max = windowInfo.maxContentWidth)
                            } else {
                                Modifier
                            }
                        )
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // HERO ARTWORK & PLAY BUTTON (Reference Panel 03)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bg_home_hero),
                            contentDescription = "Zynpath Hero Floating Island",
                            contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Big Golden Yellow "PLAY" Pill Button
                        com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                            text = "▶  PLAY",
                            onClick = {
                                feedbackCoordinator.onButtonPressed()
                                val resumable = uiState.resumableSession
                                if (resumable != null) {
                                    onNavigateToResume(resumable.worldId, resumable.levelId)
                                } else {
                                    onNavigateToPlayLevel(uiState.nextPlayableWorldId, uiState.nextPlayableLevelId)
                                }
                            },
                            style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.GOLD,
                            height = 56.dp,
                            fontSize = 20,
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .padding(vertical = 4.dp)
                        )
                    }

                    // 3 SECONDARY GAME MODES ROW (Reference Panel 03)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. QUICK DUEL
                        HomeModeCard(
                            title = "QUICK\nDUEL",
                            icon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_mode_crossed_swords),
                                    contentDescription = "Quick Duel",
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            onClick = {
                                feedbackCoordinator.onButtonPressed()
                                viewModel.onOnlineModeSelected(isOnlineRequired = true, onNavigate = onNavigateToQuickDuel)
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // 2. FRIENDS ARENA
                        HomeModeCard(
                            title = "FRIENDS\nARENA",
                            icon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_mode_friends_group),
                                    contentDescription = "Friends Arena",
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            onClick = {
                                feedbackCoordinator.onButtonPressed()
                                onNavigateToFriendsArena()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        // 3. DAILY CHALLENGE
                        HomeModeCard(
                            title = "DAILY\nCHALLENGE",
                            icon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_mode_calendar_28),
                                    contentDescription = "Daily Challenge",
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            onClick = {
                                feedbackCoordinator.onButtonPressed()
                                onNavigateToDailyChallenge()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Bottom Spacing to ensure the cards comfortably scroll above docked bottom navigation
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }

        // Docked Bottom Navigation Bar (5 Tabs matching Reference Panel 03)
        val navItems = listOf(
            GameBottomNavItem(
                route = Screen.Home.route,
                label = "Home",
                iconRes = R.drawable.ic_nav_home
            ),
            GameBottomNavItem(
                route = Screen.WorldSelection.route,
                label = "Game",
                iconRes = R.drawable.ic_mode_crossed_swords
            ),
            GameBottomNavItem(
                route = Screen.DailyChallenge.route,
                label = "Rewards",
                iconRes = R.drawable.ic_nav_gift_box
            ),
            GameBottomNavItem(
                route = Screen.Friends.createRoute(),
                label = "Friends",
                iconRes = R.drawable.ic_mode_friends_group
            ),
            GameBottomNavItem(
                route = Screen.Profile.route,
                label = "Profile",
                icon = Icons.Filled.Person
            )
        )

        GameBottomNavigation(
            currentRoute = Screen.Home.route,
            onNavigate = { destination ->
                when (destination) {
                    Screen.Home.route -> {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    }
                    Screen.WorldSelection.route -> onNavigateToLevels()
                    Screen.DailyChallenge.route -> onNavigateToDailyChallenge()
                    Screen.Friends.createRoute() -> onNavigateToFriends()
                    Screen.Profile.route -> onNavigateToProfile()
                }
            },
            items = navItems,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showCoinDetailsSheet && viewModel.walletRepository != null && viewModel.coinRewardedAdManager != null) {
        val todayUtc = java.time.LocalDate.now(java.time.ZoneOffset.UTC).toString()
        var isDailyClaimed by remember { mutableStateOf(false) }
        var isDailyAdBonusClaimed by remember { mutableStateOf(false) }
        var remainingCoinAds by remember { mutableStateOf(2) }

        LaunchedEffect(Unit) {
            isDailyClaimed = viewModel.walletRepository.isDailyLoginClaimed(todayUtc)
            isDailyAdBonusClaimed = viewModel.walletRepository.isDailyLoginAdBonusClaimed(todayUtc)
            remainingCoinAds = viewModel.walletRepository.getRemainingDailyCoinAds(todayUtc)
        }

        com.zynpath.game.feature.wallet.CoinDetailsBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { showCoinDetailsSheet = false },
            currentBalance = uiState.coinBalance,
            isDailyClaimed = isDailyClaimed,
            isDailyAdBonusClaimed = isDailyAdBonusClaimed,
            remainingCoinAds = remainingCoinAds,
            recentTransactions = recentTransactions,
            walletRepository = viewModel.walletRepository,
            coinRewardedAdManager = viewModel.coinRewardedAdManager
        )
    }
}

@Composable
private fun HomeModeCard(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(108.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF032564),
                        Color(0xFF051B42)
                    )
                )
            )
            .border(1.5.dp, Color(0xFF02449B), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * Procedural Compose vector environment drawing:
 * - Golden atmospheric celestial nebula glow
 * - Distant royal blue mountain silhouettes with gradient depth
 * - Floating isometric puzzle platforms
 * - Glowing cyan sky pathways connecting celestial nodes
 */
@Composable
private fun HomeCinematicEnvironmentCanvas(
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AtmospherePulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AtmospherePulseAnim"
    )

    val activePulse = if (isReducedMotion) 1f else pulse

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Golden Celestial Lighting Accent at upper-right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GameGoldHighlight.copy(alpha = 0.10f * activePulse),
                    GameRoyalBlue.copy(alpha = 0.16f),
                    Color.Transparent
                ),
                center = Offset(w * 0.75f, h * 0.14f),
                radius = w * 0.75f
            )
        )

        // 2. Distant Royal Blue Atmospheric Mountains
        val mountainPath = Path().apply {
            moveTo(0f, h * 0.44f)
            lineTo(w * 0.22f, h * 0.36f)
            lineTo(w * 0.48f, h * 0.41f)
            lineTo(w * 0.72f, h * 0.33f)
            lineTo(w * 0.88f, h * 0.38f)
            lineTo(w, h * 0.34f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = mountainPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F224D).copy(alpha = 0.32f),
                    Color(0xFF060D1E).copy(alpha = 0.12f)
                ),
                startY = h * 0.33f,
                endY = h * 0.72f
            )
        )

        // Closer mountain ridge silhouette
        val ridgePath = Path().apply {
            moveTo(0f, h * 0.54f)
            lineTo(w * 0.35f, h * 0.47f)
            lineTo(w * 0.65f, h * 0.52f)
            lineTo(w, h * 0.45f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = ridgePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF07122B).copy(alpha = 0.42f),
                    Color(0xFF040814).copy(alpha = 0.22f)
                ),
                startY = h * 0.45f,
                endY = h * 0.88f
            )
        )

        // 3. Floating Puzzle Platforms (Isometric Diamond Silhouettes)
        fun drawFloatingPlatform(centerX: Float, centerY: Float, halfWidth: Float, halfHeight: Float) {
            val platformPath = Path().apply {
                moveTo(centerX, centerY - halfHeight)
                lineTo(centerX + halfWidth, centerY)
                lineTo(centerX, centerY + halfHeight)
                lineTo(centerX - halfWidth, centerY)
                close()
            }
            drawPath(
                path = platformPath,
                color = Color(0x33102454)
            )
            drawPath(
                path = platformPath,
                color = GameElectricCyan.copy(alpha = 0.22f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        // Left platform
        drawFloatingPlatform(w * 0.12f, h * 0.22f, 26.dp.toPx(), 13.dp.toPx())
        // Right platform
        drawFloatingPlatform(w * 0.88f, h * 0.26f, 32.dp.toPx(), 16.dp.toPx())

        // 4. Glowing Cyan Number Pathways connecting across the sky
        val skyPath = Path().apply {
            moveTo(w * 0.12f, h * 0.22f)
            cubicTo(
                w * 0.35f, h * 0.16f,
                w * 0.65f, h * 0.32f,
                w * 0.88f, h * 0.26f
            )
        }

        drawPath(
            path = skyPath,
            color = GameElectricCyan.copy(alpha = 0.16f * activePulse),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}
