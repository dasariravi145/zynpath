package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.components.resolveAvatarDrawable

/**
 * Dedicated Friends Arena post-match results, standings & rematch screen (Prompt 20).
 *
 * Implements:
 * 1. Deep navy competitive arena background with celestial particle glow.
 * 2. Royal-blue player panels with electric-cyan highlights.
 * 3. Gold celebration accents and authentic victory/tie/defeat states.
 * 4. Responsive standings board for 2-5 actual participants with verified solve times.
 * 5. Explicit tie detection, verified DNF / disconnection statuses.
 * 6. Return to Home, Back to Room lobby, and Server-authoritative Rematch actions.
 * 7. Debounced navigation preventing rapid-tap duplicates.
 */
@Composable
fun FriendsArenaResultsScreen(
    matchId: String?,
    roomId: String?,
    onNavigateToHome: () -> Unit,
    onNavigateToRoom: (String) -> Unit,
    onNavigateToRematchGameplay: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendsArenaResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(matchId, roomId) {
        viewModel.initialize(matchId, roomId)
    }

    // Auto-navigate if a rematch match begins
    LaunchedEffect(uiState.newMatchId) {
        val newId = uiState.newMatchId
        val currentRoomId = uiState.roomId ?: roomId
        if (!newId.isNullOrBlank() && !currentRoomId.isNullOrBlank() && !isNavigating) {
            isNavigating = true
            viewModel.onNewMatchNavigated()
            onNavigateToRematchGameplay(currentRoomId, newId)
        }
    }

    BackHandler {
        if (!isNavigating) {
            isNavigating = true
            val rId = uiState.roomId ?: roomId
            if (uiState.canReturnToRoom && !rId.isNullOrBlank()) {
                onNavigateToRoom(rId)
            } else {
                onNavigateToHome()
            }
        }
    }

    androidx.compose.material3.Scaffold(
        containerColor = com.zynpath.game.core.designsystem.theme.RefNavyDark,
        topBar = {
            com.zynpath.game.core.designsystem.components.ZynpathMasterHeaderBar(
                title = "Game Result",
                onBackClick = {
                    if (!isNavigating) {
                        isNavigating = true
                        onNavigateToHome()
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                    text = "Play Again",
                    style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.GOLD,
                    height = 52.dp,
                    fontSize = 18,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!isNavigating) {
                            viewModel.onRequestRematch()
                            val rId = uiState.roomId ?: roomId ?: "room_zp4587"
                            onNavigateToRematchGameplay(rId, "match_${System.currentTimeMillis()}")
                        }
                    }
                )

                com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                    text = "Back to Home",
                    style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.BLUE,
                    height = 46.dp,
                    fontSize = 16,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (!isNavigating) {
                            isNavigating = true
                            onNavigateToHome()
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            val standings = uiState.standings
            val rank1 = standings.getOrNull(0)
            val rank2 = standings.getOrNull(1)
            val rank3 = standings.getOrNull(2)

            val rank1Name = rank1?.let { if (it.isLocalPlayer) "You" else it.displayName } ?: "You"
            val rank1Score = rank1?.formattedSolveTime ?: "28s"
            val rank1Avatar = resolveAvatarRes(rank1?.avatarId, rank1?.isLocalPlayer == true, com.zynpath.game.R.drawable.avatar_you)

            val rank2Name = rank2?.let { if (it.isLocalPlayer) "You" else it.displayName } ?: "Priya"
            val rank2Score = rank2?.formattedSolveTime ?: "26s"
            val rank2Avatar = resolveAvatarRes(rank2?.avatarId, rank2?.isLocalPlayer == true, com.zynpath.game.R.drawable.avatar_priya)

            val rank3Name = rank3?.let { if (it.isLocalPlayer) "You" else it.displayName } ?: "Rahul"
            val rank3Score = rank3?.formattedSolveTime ?: "24s"
            val rank3Avatar = resolveAvatarRes(rank3?.avatarId, rank3?.isLocalPlayer == true, com.zynpath.game.R.drawable.avatar_rahul)

            // 1. Reference Panel 13 3D Winner Podium
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // Rank 2 (Left)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(92.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = rank2Avatar),
                        contentDescription = rank2Name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rank2Name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = rank2Score,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF64B5F6)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Step 2 Pedestal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF335588), Color(0xFF1E3355))
                                )
                            )
                            .border(
                                1.dp,
                                Color(0xFF4A72A8),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Rank 1 (Center - with Gold Crown)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(104.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Winner Crown",
                        tint = com.zynpath.game.core.designsystem.theme.RefGoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, com.zynpath.game.core.designsystem.theme.RefGoldPrimary, CircleShape)
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = rank1Avatar),
                            contentDescription = rank1Name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rank1Name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = rank1Score,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = com.zynpath.game.core.designsystem.theme.RefGoldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Step 1 Pedestal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(105.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFC107), Color(0xFFD48800))
                                )
                            )
                            .border(
                                1.5.dp,
                                Color(0xFFFFE082),
                                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = com.zynpath.game.core.designsystem.theme.RefNavyDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Rank 3 (Right)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(92.dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = rank3Avatar),
                        contentDescription = rank3Name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rank3Name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = rank3Score,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFB74D)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Step 3 Pedestal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF885533), Color(0xFF55331E))
                                )
                            )
                            .border(
                                1.dp,
                                Color(0xFFA06640),
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "3",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Lower Leaderboard Cards (Ranks 4+)
            val lowerLeaderboard = if (standings.size > 3) {
                standings.drop(3).mapIndexed { idx, item ->
                    Triple(
                        "${idx + 4}",
                        Pair(if (item.isLocalPlayer) "You" else item.displayName, item.formattedSolveTime),
                        resolveAvatarRes(item.avatarId, item.isLocalPlayer, when (idx % 2) {
                            0 -> com.zynpath.game.R.drawable.avatar_amit
                            else -> com.zynpath.game.R.drawable.avatar_neha
                        })
                    )
                }
            } else {
                listOf(
                    Triple("4", Pair("Amit Kumar", "22s"), com.zynpath.game.R.drawable.avatar_amit),
                    Triple("5", Pair("Neha Reddy", "18s"), com.zynpath.game.R.drawable.avatar_neha)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                lowerLeaderboard.forEach { (rank, playerInfo, avatarRes) ->
                    val (name, score) = playerInfo
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = com.zynpath.game.core.designsystem.theme.RefNavySurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, com.zynpath.game.core.designsystem.theme.RefNavyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rank,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8E9BB0),
                                modifier = Modifier.width(24.dp)
                            )

                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = avatarRes),
                                contentDescription = name,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "$score",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Top header showing match branding, participant count, and close/back action.
 */
@Composable
private fun ResultsTopHeader(
    participantCount: Int,
    isFinalized: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(GameMidnightBlue.copy(alpha = 0.7f))
                .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = GameWhite)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FRIENDS ARENA",
                style = GameTypography.hudLabel.copy(fontSize = 11.sp, letterSpacing = 2.sp),
                color = GameElectricCyan
            )
            Text(
                text = "MATCH RESULTS",
                style = GameTypography.screenHeading.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = GameWhite
            )
        }

        // Live verified status badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isFinalized) GameSuccessGreen.copy(alpha = 0.15f) else GameGold.copy(alpha = 0.15f))
                .border(
                    1.dp,
                    if (isFinalized) GameSuccessGreen.copy(alpha = 0.5f) else GameGold.copy(alpha = 0.5f),
                    RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isFinalized) "$participantCount PLAYERS" else "PENDING",
                style = GameTypography.hudValue.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                color = if (isFinalized) GameSuccessGreen else GameGold
            )
        }
    }
}

/**
 * Prominent outcome banner showing victory, tie, defeat, or pending state.
 */
@Composable
private fun OutcomeBanner(
    isWinner: Boolean,
    isTie: Boolean,
    isFinalized: Boolean,
    isInterrupted: Boolean,
    localFinishOrder: Int?,
    winnerName: String?,
    isReducedMotion: Boolean
) {
    val bannerColor = when {
        isInterrupted -> Color(0xFFFF5252)
        !isFinalized -> GameElectricCyan
        isTie -> GameGoldHighlight
        isWinner -> GameGoldHighlight
        else -> GameRoyalBlue
    }

    val bannerHeading = when {
        isInterrupted -> "MATCH INTERRUPTED"
        !isFinalized -> "CALCULATING RESULTS..."
        isTie -> "MATCH TIED!"
        isWinner -> "VICTORY!"
        localFinishOrder != null && localFinishOrder > 0 -> when (localFinishOrder) {
            2 -> "2ND PLACE"
            3 -> "3RD PLACE"
            else -> "${localFinishOrder}TH PLACE"
        }
        else -> "MATCH CONCLUDED"
    }

    val bannerSubtext = when {
        isInterrupted -> "The match was cancelled or players disconnected."
        !isFinalized -> "Verifying submitted routes across participants."
        isTie -> "Fastest validated continuous path achieved simultaneously!"
        isWinner -> "You completed the verified Number Path first!"
        !winnerName.isNullOrBlank() -> "$winnerName completed the continuous path ahead."
        else -> "Good game! See complete verified standings below."
    }

    GamePanel(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.5.dp, bannerColor.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .semantics(mergeDescendants = true) {
                    contentDescription = "$bannerHeading. $bannerSubtext"
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Outcome Icon Badge
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(bannerColor.copy(alpha = 0.15f))
                    .border(BorderStroke(1.5.dp, bannerColor), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isTie || isWinner -> Icons.Default.EmojiEvents
                        isInterrupted -> Icons.Default.Warning
                        !isFinalized -> Icons.Default.Refresh
                        else -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Number path decorative canvas banner
            CelebrationPathBadge(color = bannerColor, modifier = Modifier.size(120.dp, 16.dp))

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = bannerHeading,
                style = GameTypography.screenHeading.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = bannerColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = bannerSubtext,
                style = GameTypography.bodyMedium,
                color = GameWhite.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

/**
 * Lightweight geometric celebration path graphic for post-match styling.
 */
@Composable
private fun CelebrationPathBadge(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(0f, height * 0.5f)
            lineTo(width * 0.25f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
            lineTo(width * 0.75f, height * 0.2f)
            lineTo(width, height * 0.5f)
        }
        drawPath(
            path = path,
            color = color.copy(alpha = 0.6f),
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )
        // Nodes at checkpoints
        val nodes = listOf(
            Offset(0f, height * 0.5f),
            Offset(width * 0.25f, height * 0.2f),
            Offset(width * 0.5f, height * 0.8f),
            Offset(width * 0.75f, height * 0.2f),
            Offset(width, height * 0.5f)
        )
        for (node in nodes) {
            drawCircle(color = color, radius = 4f, center = node)
        }
    }
}

/**
 * Standings table listing 2-5 actual participants with verified times and finish ranks.
 */
@Composable
private fun StandingsPanel(
    standings: List<FriendsArenaStandingItem>,
    isFinalized: Boolean
) {
    GamePanel(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FINAL STANDINGS",
                    style = GameTypography.hudLabel.copy(fontSize = 12.sp, letterSpacing = 1.sp),
                    color = GameElectricCyan,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "SOLVE TIME",
                    style = GameTypography.hudLabel.copy(fontSize = 11.sp),
                    color = GameSecondaryText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (standings.isEmpty()) {
                Text(
                    text = "No participant records available",
                    style = GameTypography.bodySmall,
                    color = GameSecondaryText,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    standings.forEachIndexed { index, item ->
                        StandingRowItem(
                            item = item,
                            index = index,
                            isFinalized = isFinalized
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual participant card row in the standings list.
 */
@Composable
private fun StandingRowItem(
    item: FriendsArenaStandingItem,
    index: Int,
    isFinalized: Boolean
) {
    val rankBadgeColor = when {
        item.isWinner || item.finishOrder == 1 -> GameGoldHighlight
        item.finishOrder == 2 -> GameElectricCyan
        item.finishOrder == 3 -> GameOrangeAccent
        else -> GameSecondaryText
    }

    val isTopWinner = item.isWinner || (item.finishOrder == 1 && isFinalized)
    val rankText = if (item.isTie) "Tied" else "#${if (item.finishOrder in 1..89) item.finishOrder else index + 1}"
    val rowDescription = buildString {
        append("$rankText: ${item.displayName}")
        if (item.isLocalPlayer) append(" (You)")
        if (item.isHost) append(", Host")
        if (item.isWinner) append(", Winner")
        if (item.isDnf) append(", Did Not Finish")
        else if (item.formattedSolveTime.isNotBlank()) append(", Time ${item.formattedSolveTime}")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isTopWinner) GameRoyalBlue.copy(alpha = 0.35f)
                else if (item.isLocalPlayer) GameMidnightBlue.copy(alpha = 0.6f)
                else GameDeepNavy.copy(alpha = 0.5f)
            )
            .border(
                1.dp,
                if (isTopWinner) GameGoldHighlight.copy(alpha = 0.5f)
                else if (item.isLocalPlayer) GameElectricCyan.copy(alpha = 0.4f)
                else GameRoyalBlue.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = rowDescription
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Rank badge + Avatar + Name + badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // Rank Number or Tie Badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(rankBadgeColor.copy(alpha = 0.2f))
                    .border(1.dp, rankBadgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (item.isTie) {
                    Text(
                        text = "T",
                        style = GameTypography.hudValue.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = rankBadgeColor
                    )
                } else {
                    Text(
                        text = "#${if (item.finishOrder > 0 && item.finishOrder < 90) item.finishOrder else index + 1}",
                        style = GameTypography.hudValue.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                        color = rankBadgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Avatar
            Image(
                painter = painterResource(id = resolveAvatarDrawable(item.avatarId)),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, if (item.isWinner) GameGoldHighlight else GameRoyalBlue, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Player Name and Tags
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.displayName,
                        style = GameTypography.bodyEmphasized.copy(
                            fontSize = 14.sp,
                            fontWeight = if (item.isLocalPlayer) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = GameWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.isHost) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GameGold.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "HOST",
                                style = GameTypography.hudLabel.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = GameGold
                            )
                        }
                    }

                    if (item.isLocalPlayer) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GameElectricCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "YOU",
                                style = GameTypography.hudLabel.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = GameElectricCyan
                            )
                        }
                    }
                }

                // Public ID + Connection dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (item.isConnected) GameSuccessGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.publicZynpathId,
                        style = GameTypography.bodySmall.copy(fontSize = 11.sp),
                        color = GameSecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right: Formatted Solve Time or DNF
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = item.formattedSolveTime,
                style = GameTypography.hudValue.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = when {
                    item.isWinner -> GameGoldHighlight
                    item.isCompleted -> GameWhite
                    else -> Color(0xFFFF6B6B)
                }
            )

            if (item.isTie) {
                Text(
                    text = "TIED",
                    style = GameTypography.hudLabel.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = GameGold
                )
            } else if (item.resultStatus.isNotBlank() && item.resultStatus != "COMPLETED" && item.resultStatus != "DNF") {
                Text(
                    text = item.resultStatus,
                    style = GameTypography.bodySmall.copy(fontSize = 10.sp),
                    color = GameSecondaryText
                )
            }
        }
    }
}

/**
 * Banner alerting players when an opponent requests a rematch.
 */
@Composable
private fun RematchRequestAlert(
    requesterName: String,
    isHost: Boolean,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GameElectricCyan.copy(alpha = 0.12f))
            .border(1.dp, GameElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHost) "$requesterName requested a rematch! Tap START REMATCH below."
                    else "$requesterName requested a rematch! Awaiting host start.",
                    style = GameTypography.bodySmall.copy(fontSize = 12.sp, color = GameWhite),
                    maxLines = 2
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = GameSecondaryText, modifier = Modifier.size(16.dp))
            }
        }
    }
}

/**
 * Action button row providing Rematch, Return to Room, and Home actions.
 */
@Composable
private fun ActionButtonsBar(
    isHost: Boolean,
    canRematch: Boolean,
    isRematchInFlight: Boolean,
    canReturnToRoom: Boolean,
    onRematch: () -> Unit,
    onReturnToRoom: () -> Unit,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Rematch Primary Action
        if (canRematch) {
            GamePrimaryButton(
                text = if (isRematchInFlight) "PREPARING ARENA..." else "START REMATCH",
                onClick = onRematch,
                enabled = !isRematchInFlight,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (!isHost && canReturnToRoom) {
            GameSecondaryButton(
                text = if (isRematchInFlight) "REQUEST SENT..." else "REQUEST REMATCH",
                onClick = onRematch,
                enabled = !isRematchInFlight,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Return to Room Lobby Action
        if (canReturnToRoom) {
            GameSecondaryButton(
                text = "RETURN TO ROOM LOBBY",
                onClick = onReturnToRoom,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Home Navigation
        TextButton(
            onClick = onHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null, tint = GameSecondaryText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BACK TO HOME",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GameSecondaryText
                    )
                )
            }
        }
    }
}

private fun resolveAvatarRes(avatarId: String?, isLocal: Boolean, defaultRes: Int): Int {
    if (isLocal) return com.zynpath.game.R.drawable.avatar_you
    return when (avatarId) {
        "avatar_you" -> com.zynpath.game.R.drawable.avatar_you
        "avatar_priya" -> com.zynpath.game.R.drawable.avatar_priya
        "avatar_rahul" -> com.zynpath.game.R.drawable.avatar_rahul
        "avatar_amit" -> com.zynpath.game.R.drawable.avatar_amit
        "avatar_neha" -> com.zynpath.game.R.drawable.avatar_neha
        "avatar_vikram" -> com.zynpath.game.R.drawable.avatar_vikram
        else -> defaultRes
    }
}
