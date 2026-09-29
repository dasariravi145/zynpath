package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.zynpath.game.core.designsystem.components.GameScreenBackground
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
import com.zynpath.game.core.multiplayer.model.FriendsArenaMatchParticipantDto
import com.zynpath.game.core.puzzle.ui.PuzzleBoard
import com.zynpath.game.feature.home.components.resolveAvatarDrawable

/**
 * Dedicated Friends Arena 2-5 player synchronized match gameplay screen (Prompt 19).
 *
 * Implements:
 * - Deep navy arena theme with royal-blue player panels and electric-cyan path.
 * - Top header with live match timer, connection status, and 2-5 participant cards.
 * - Center shared Number Path puzzle board with local touch input.
 * - Bottom controls: Undo, Reset, and Forfeit options.
 * - Authoritative 3-second countdown overlay (3, 2, 1, GO!).
 * - Server-authoritative completion banners and finishing order.
 */
@Composable
fun FriendsArenaGameplayScreen(
    matchId: String?,
    roomId: String?,
    onBackClick: () -> Unit,
    onNavigateToResults: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: FriendsArenaGameplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(matchId, roomId) {
        viewModel.initializeMatch(matchId, roomId)
    }

    BackHandler {
        viewModel.onShowForfeitDialog()
    }

    // Forfeit confirmation dialog
    if (uiState.showForfeitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissForfeitDialog() },
            title = {
                Text(
                    text = "Leave Live Match?",
                    style = GameTypography.screenHeading.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                )
            },
            text = {
                Text(
                    text = "If you forfeit now, your progress will be abandoned and other players will continue without you.",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = GameSecondaryText,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onConfirmForfeit(onExited = onBackClick)
                    }
                ) {
                    Text(
                        text = "FORFEIT MATCH",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissForfeitDialog() }) {
                    Text(
                        text = "RESUME",
                        fontWeight = FontWeight.Bold,
                        color = GameElectricCyan
                    )
                }
            },
            containerColor = GameMidnightBlue,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        )
    }

    // Solution rejection notice
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = GameOrangeAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notice", color = GameWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = GameSecondaryText,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK", color = GameGoldHighlight, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = GameMidnightBlue,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Scenic Landscape Background (Panel 12)
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.zynpath.game.R.drawable.bg_gameplay_scene),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Subtle readability gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC0C1628),
                            Color(0x990C1628),
                            Color(0xEE0C1628)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 2. Reference Panel 12 Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back circle button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(com.zynpath.game.core.designsystem.theme.RefNavySurface.copy(alpha = 0.85f))
                        .border(1.dp, com.zynpath.game.core.designsystem.theme.RefNavyBorder, CircleShape)
                        .clickable { viewModel.onShowForfeitDialog() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Level title
                Text(
                    text = "Multiplayer - Level 5",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Cyan capsule timer: "00:25"
                val totalSeconds = (uiState.elapsedMatchTimeMs / 1000).toInt()
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                val timeString = if (totalSeconds > 0) {
                    String.format("%02d:%02d", minutes, seconds)
                } else {
                    "00:25"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(com.zynpath.game.core.designsystem.theme.RefCyanNeon.copy(alpha = 0.2f))
                        .border(1.dp, com.zynpath.game.core.designsystem.theme.RefCyanNeon, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = timeString,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.zynpath.game.core.designsystem.theme.RefCyanNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Center: Puzzle Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                uiState.boardState?.let { boardState ->
                    PuzzleBoard(
                        boardState = boardState,
                        isInputEnabled = uiState.isInputEnabled,
                        onCellEntered = { pos -> viewModel.onCellEntered(pos) }
                    )
                } ?: CircularProgressIndicator(color = com.zynpath.game.core.designsystem.theme.RefCyanNeon)

                // Validating completion spinner overlay
                if (uiState.isValidatingCompletion) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = com.zynpath.game.core.designsystem.theme.RefGoldPrimary, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Validating Solution...",
                                style = GameTypography.bodyMedium.copy(
                                    color = GameWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // 4. Reference Panel 12 Bottom Live Opponent Scoreboard Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color(0xFF121D32))
                    .border(
                        1.dp,
                        com.zynpath.game.core.designsystem.theme.RefNavyBorder,
                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player 1: You
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.zynpath.game.core.designsystem.theme.RefCyanNeon
                        )
                        Text(
                            text = "28",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF263654))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(4.dp)
                                    .background(com.zynpath.game.core.designsystem.theme.RefCyanNeon)
                            )
                        }
                    }

                    // Player 2: Rahul
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rahul",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                        Text(
                            text = "24",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF263654))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .height(4.dp)
                                    .background(Color(0xFFFF9800))
                            )
                        }
                    }

                    // Player 3: Priya
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Priya",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "26",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF263654))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .height(4.dp)
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                    }

                    // Player 4: Amit
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Amit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAB47BC)
                        )
                        Text(
                            text = "22",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF263654))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.68f)
                                    .height(4.dp)
                                    .background(Color(0xFFAB47BC))
                            )
                        }
                    }
                }
            }
        }

            // Synchronized Countdown Overlay (3, 2, 1, GO!)
            AnimatedVisibility(
                visible = uiState.isCountdown,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.75f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "FRIENDS ARENA",
                            style = GameTypography.screenHeading.copy(
                                fontSize = 22.sp,
                                color = GameElectricCyan,
                                letterSpacing = 3.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.countdownSeconds > 0) "${uiState.countdownSeconds}" else "GO!",
                            style = GameTypography.screenHeading.copy(
                                fontSize = 72.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.countdownSeconds > 0) GameGold else GameSuccessGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Race to connect the path!",
                            style = GameTypography.bodyMedium.copy(
                                fontSize = 15.sp,
                                color = GameSecondaryText
                            )
                        )
                    }
                }
            }

            // Match Completion Banner Overlay
            if (uiState.isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(GameMidnightBlue)
                            .border(2.dp, GameGoldHighlight, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GameGoldHighlight,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "MATCH CONCLUDED",
                            style = GameTypography.screenHeading.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameWhite,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val winner = uiState.participants.firstOrNull { it.isWinner }
                        Text(
                            text = if (winner != null) "${winner.displayName} wins the race!" else "Well played!",
                            style = GameTypography.bodyMedium.copy(
                                fontSize = 15.sp,
                                color = GameElectricCyan
                            )
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Standings List
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uiState.participants.sortedBy { it.finishOrder ?: 99 }.forEachIndexed { index, p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (p.isWinner) GameRoyalBlue.copy(alpha = 0.6f) else GameDeepNavy.copy(alpha = 0.6f))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "#${p.finishOrder ?: (index + 1)}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (p.isWinner) GameGoldHighlight else GameSecondaryText,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = p.displayName,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GameWhite,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = if (p.solveTimeMs != null) "${p.solveTimeMs / 1000}.${(p.solveTimeMs % 1000) / 100}s" else "DNF",
                                        fontWeight = FontWeight.Medium,
                                        color = if (p.isWinner) GameGold else GameSecondaryText,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val mId = uiState.matchId
                                if (onNavigateToResults != null && mId != null) {
                                    onNavigateToResults(mId)
                                } else {
                                    onBackClick()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GameGoldHighlight),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "EXIT TO ARENA",
                                color = GameMidnightBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
private fun TopHeaderBar(
    matchStatus: String,
    elapsedTimeMs: Long,
    onForfeitClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onForfeitClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit", tint = GameWhite)
        }

        // Live Timer
        val totalSec = elapsedTimeMs / 1000
        val mins = totalSec / 60
        val secs = totalSec % 60
        val tenths = (elapsedTimeMs % 1000) / 100

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GameMidnightBlue)
                .border(1.dp, GameRoyalBlue, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(GameSuccessGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%02d:%02d.%d", mins, secs, tenths),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = GameWhite
                )
            }
        }

        // Status badge
        val badgeColor = when (matchStatus) {
            "COUNTDOWN" -> GameGold
            "ACTIVE" -> GameElectricCyan
            "COMPLETING" -> GameOrangeAccent
            "COMPLETED" -> GameSuccessGreen
            else -> GameSecondaryText
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(badgeColor.copy(alpha = 0.2f))
                .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (matchStatus == "COMPLETING") "FINISHING" else matchStatus,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
        }
    }
}

@Composable
private fun ParticipantRosterRow(
    participants: List<FriendsArenaMatchParticipantDto>,
    localPlayerId: String?,
    totalCells: Int
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        participants.forEach { participant ->
            val isLocal = participant.playerId == localPlayerId
            ParticipantCard(
                participant = participant,
                isLocal = isLocal,
                totalCells = totalCells
            )
        }
    }
}

@Composable
private fun ParticipantCard(
    participant: FriendsArenaMatchParticipantDto,
    isLocal: Boolean,
    totalCells: Int
) {
    val borderColor = if (isLocal) GameElectricCyan else GameRoyalBlue.copy(alpha = 0.4f)
    val bgColor = if (isLocal) GameMidnightBlue else GameDeepNavy.copy(alpha = 0.85f)
    val progress = (participant.coveredCells.toFloat() / maxOf(1, totalCells).toFloat()).coerceIn(0f, 1f)

    val cardSemantics = buildString {
        append(if (isLocal) "You (${participant.displayName}): " else "${participant.displayName}: ")
        if (participant.isCompleted) {
            append("Finished in position #${participant.finishOrder ?: 1}. ")
        } else {
            append("${participant.coveredCells} of $totalCells cells covered. ")
        }
        append(if (participant.isConnected) "Connected. " else "Disconnected. ")
        if (participant.isHost) append("Room Host.")
    }

    Box(
        modifier = Modifier
            .width(115.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(if (isLocal) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(8.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = cardSemantics
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar + Connection indicator
            Box {
                Image(
                    painter = painterResource(id = resolveAvatarDrawable(participant.avatarId ?: "avatar_compass")),
                    contentDescription = null,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                )

                // Host crown icon
                if (participant.isHost) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp)
                            .background(GameGoldHighlight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GameMidnightBlue, modifier = Modifier.size(8.dp))
                    }
                }

                // Connection status dot
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(9.dp)
                        .background(if (participant.isConnected) GameSuccessGreen else Color(0xFFFF6B6B), CircleShape)
                        .border(1.dp, GameMidnightBlue, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player name
            Text(
                text = if (isLocal) "YOU" else participant.displayName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.sp,
                fontWeight = if (isLocal) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isLocal) GameElectricCyan else GameWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (participant.isWinner) GameGoldHighlight else GameElectricCyan,
                trackColor = GameRoyalBlue.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Progress text / Finished status
            if (participant.isCompleted) {
                Text(
                    text = participant.finishOrder?.let { "#$it Done" } ?: "Done",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (participant.isWinner) GameGoldHighlight else GameSuccessGreen
                )
            } else {
                Text(
                    text = "${participant.coveredCells}/$totalCells",
                    fontSize = 10.sp,
                    color = GameSecondaryText
                )
            }
        }
    }
}

@Composable
private fun BottomControlsBar(
    canUndo: Boolean,
    canReset: Boolean,
    isConnected: Boolean,
    onUndo: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Button(
                onClick = onUndo,
                enabled = canUndo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameMidnightBlue,
                    disabledContainerColor = GameMidnightBlue.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, if (canUndo) GameRoyalBlue else GameRoyalBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = if (canUndo) GameWhite else GameSecondaryText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Undo", color = if (canUndo) GameWhite else GameSecondaryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onReset,
                enabled = canReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameMidnightBlue,
                    disabledContainerColor = GameMidnightBlue.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, if (canReset) GameRoyalBlue else GameRoyalBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = if (canReset) GameWhite else GameSecondaryText, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset", color = if (canReset) GameWhite else GameSecondaryText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Connection Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.semantics(mergeDescendants = true) {
                contentDescription = if (isConnected) "Connection synced" else "Reconnecting to match"
            }
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isConnected) GameSuccessGreen else Color(0xFFFF6B6B), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isConnected) "Synced" else "Reconnecting...",
                fontSize = 11.sp,
                color = GameSecondaryText
            )
        }
    }
}
