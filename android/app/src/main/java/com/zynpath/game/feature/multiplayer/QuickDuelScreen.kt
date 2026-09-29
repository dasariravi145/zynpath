package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.CharcoalNavy
import com.zynpath.game.core.designsystem.theme.DeepIndigo
import com.zynpath.game.core.designsystem.theme.EmeraldGreen
import com.zynpath.game.core.designsystem.theme.ErrorRed
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrightBlue
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.GunmetalCard
import com.zynpath.game.core.designsystem.theme.MutedSlate
import com.zynpath.game.core.designsystem.theme.OffWhiteText
import com.zynpath.game.core.designsystem.theme.PathNeonCyan
import com.zynpath.game.core.designsystem.theme.WarningAmber
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

/**
 * Screen rendering Quick Duel 1v1 online multiplayer experience.
 *
 * Implements Prompt 21:
 * - Functional entry point with competitive overview & auth requirement.
 * - Queue searching state with cancel.
 * - Match Found lobby with player vs real opponent inspection.
 * - Authoritative ready confirmation with 20s window.
 * - Synchronized 3-second countdown.
 * - Live gameplay board reusing PuzzleBoard with dual progress bars and authoritative timer.
 * - Hints strictly disabled; undo & reset preserved.
 * - Full solution submission with dual-win server validation overlay.
 * - Authoritative Result screen showing actual winner, timing, SHA-256 fingerprint, Play Again, and Hub exit.
 * - Forfeit confirmation dialog preventing accidental abandonment.
 * - Ephemeral rate-limited preset reactions.
 */
@Composable
fun QuickDuelScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    viewModel: QuickDuelViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle Android hardware/gesture back
    BackHandler {
        if (state.isActiveGameplay) {
            viewModel.requestForfeit()
        } else if (state.isSearching) {
            viewModel.cancelSearching()
            onBackClick()
        } else if (state.isMatchFound) {
            viewModel.leaveMatch()
            onBackClick()
        } else {
            viewModel.leaveMatch()
            onBackClick()
        }
    }

    // Forfeit confirmation dialog
    if (state.showForfeitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissForfeitDialog() },
            title = {
                Text(
                    text = "Forfeit Match?",
                    fontWeight = FontWeight.Bold,
                    color = GameWhite
                )
            },
            text = {
                Text(
                    text = "Leaving an active Quick Duel forfeits the match and awards the win to your opponent. Are you sure you want to forfeit?",
                    style = GameTypography.secondaryInfo,
                    color = GameSecondaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmForfeit() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Forfeit Match", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissForfeitDialog() }) {
                    Text("Stay in Duel", color = GameElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xF207142D),
            shape = GameShapes.dialog,
            modifier = Modifier.border(BorderStroke(1.2.dp, GameRoyalBlue.copy(alpha = 0.6f)), GameShapes.dialog)
        )
    }

    GameScreenBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                QuickDuelTopBar(
                    isConnected = state.isConnected,
                    isAuthenticated = state.isAuthenticated,
                    isActiveGameplay = state.isActiveGameplay,
                    onBack = {
                        if (state.isActiveGameplay) {
                            viewModel.requestForfeit()
                        } else {
                            viewModel.leaveMatch()
                            onBackClick()
                        }
                    }
                )
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. Authoritative Completed Result Screen
                state.isCompleted -> {
                    QuickDuelResultContent(
                        state = state,
                        onPlayAgain = { viewModel.playAgain() },
                        onReturnToHub = {
                            viewModel.leaveMatch()
                            onBackClick()
                        }
                    )
                }

                // 2. Synchronized 3-second Countdown Overlay
                state.isCountdown -> {
                    QuickDuelCountdownContent(seconds = state.countdownSeconds ?: 3)
                }

                // 3. Active Gameplay Board
                state.isActiveGameplay -> {
                    QuickDuelActiveGameplayContent(
                        state = state,
                        onCellEntered = { viewModel.onCellEntered(it) },
                        onUndo = { viewModel.undo() },
                        onReset = { viewModel.reset() },
                        onForfeit = { viewModel.requestForfeit() },
                        onSendReaction = { viewModel.sendReaction(it) }
                    )
                }

                // 4. Match Found Lobby (Waiting for Ready / Opponent)
                state.isMatchFound -> {
                    QuickDuelLobbyContent(
                        state = state,
                        onMarkReady = { viewModel.markReady() },
                        onLeaveMatch = {
                            viewModel.leaveMatch()
                            onBackClick()
                        }
                    )
                }

                // 5. Searching in Queue
                state.isSearching -> {
                    QuickDuelSearchingContent(
                        ticketId = state.ticketStatus?.ticketId,
                        onCancel = { viewModel.cancelSearching() }
                    )
                }

                // 6. Match Cancelled or Error
                state.isCancelled || state.isError -> {
                    QuickDuelErrorContent(
                        errorMessage = state.errorMessage ?: "The match was cancelled or terminated.",
                        onReturnToHub = {
                            viewModel.leaveMatch()
                            onBackClick()
                        },
                        onTryAgain = {
                            viewModel.clearError()
                            viewModel.startSearching()
                        }
                    )
                }

                // 7. Idle / Entry Point
                else -> {
                    QuickDuelEntryPointContent(
                        state = state,
                        onFindOpponent = { viewModel.startSearching() },
                        onNavigateToSignIn = onNavigateToSignIn
                    )
                }
            }

            // Server-side completion validation modal overlay
            if (state.isValidatingCompletion) {
                QuickDuelValidationOverlay()
            }

            // Ephemeral floating reaction badge
            state.activeReactionPopup?.let { reaction ->
                QuickDuelReactionBadge(
                    reaction = reaction,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}
}

@Composable
private fun QuickDuelTopBar(
    isConnected: Boolean,
    isAuthenticated: Boolean,
    isActiveGameplay: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GameMidnightBlue.copy(alpha = 0.85f))
            .border(BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.4f)))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = if (isActiveGameplay) "Forfeit Duel" else "Back",
                tint = GameWhite
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "QUICK DUEL 1v1",
                style = GameTypography.hudLabel.copy(letterSpacing = 1.2.sp),
                color = GameWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isActiveGameplay) "Live Match in Progress" else "Dual-Win Verified Online Race",
                style = GameTypography.secondaryInfo,
                color = if (isActiveGameplay) GameElectricCyan else GameSecondaryText
            )
        }

        // Connection feedback:
        // When unauthenticated, unauthenticated WebSocket is disconnected by design.
        // Do NOT label offline. Show neutral "Online Arena".
        // When authenticated: "Live" (green) if connected, "Connecting..." (amber) if reconnecting.
        val (statusText, statusColor) = when {
            !isAuthenticated -> Pair("Online Arena", GameRoyalBlue)
            isConnected -> Pair("Live Arena", GameSuccessGreen)
            else -> Pair("Connecting...", WarningAmber)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(statusColor.copy(alpha = 0.16f), RoundedCornerShape(12.dp))
                .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(statusColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }
    }
}

/**
 * Section 7: Quick Duel Entry Point View.
 */
@Composable
private fun QuickDuelEntryPointContent(
    state: QuickDuelUiState,
    onFindOpponent: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isFindingMatch by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Header Panel
        GamePanel(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.2.dp, GameElectricCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(GameElectricCyan.copy(alpha = 0.16f), CircleShape)
                            .border(BorderStroke(1.dp, GameElectricCyan.copy(alpha = 0.6f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = GameElectricCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "QUICK DUEL 1v1",
                            style = GameTypography.hudLabel,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        )
                        Text(
                            text = "Real-Time Competitive Puzzle Race",
                            style = GameTypography.secondaryInfo,
                            color = GameElectricCyan
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Race head-to-head against another real player on identical solver-verified puzzles. Both participants receive the same continuous path puzzle. The first player to cover 100% of required cells in valid checkpoint order authoritatively validated by the server wins!",
                    style = GameTypography.secondaryInfo,
                    color = GameWhite.copy(alpha = 0.85f),
                    lineHeight = 19.sp
                )
            }
        }

        // Player vs Opponent Preview Showcase
        val localName = state.authSession?.displayName ?: "Guest Player"
        val localSubtitle = state.authSession?.let { "ID: ${it.publicZynpathId.take(8)}..." } ?: "Local Session"

        PlayerVsOpponentShowcase(
            localName = localName,
            localSubtitle = localSubtitle,
            opponentName = null,
            opponentSubtitle = null,
            isWaiting = true
        )

        // Dual-Win Competitive Rules
        GamePanel(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DUAL-WIN RULES",
                    style = GameTypography.hudLabel.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = GameGoldHighlight
                )
                Spacer(modifier = Modifier.height(8.dp))
                CompetitiveRuleItem("1", "Start at Checkpoint 1 and end at the highest checkpoint.")
                CompetitiveRuleItem("2", "Connect numbered checkpoints in strict ascending sequence.")
                CompetitiveRuleItem("3", "Move orthogonally and cover 100% of required cells.")
                CompetitiveRuleItem("4", "Never cross blocked walls or revisit cells on forward moves.")
                CompetitiveRuleItem("5", "Competitive hint assistance is strictly disabled.")
            }
        }

        // Authentication Gate for Guests
        if (!state.isAuthenticated) {
            GamePanel(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.2.dp, GameGold.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GameGoldHighlight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign-In Required for Online Play",
                            style = GameTypography.bodyEmphasized,
                            fontWeight = FontWeight.Bold,
                            color = GameGoldHighlight
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "To safeguard competitive integrity and prevent fake match outcomes, online 1v1 duels require a signed-in account. Guest progress and achievements remain safe and link automatically upon sign-in.",
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    GameSecondaryButton(
                        text = "Sign In to Play Online",
                        onClick = onNavigateToSignIn,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Matchmaking Button (Gold Primary Action with Double-Tap Guard)
        val canSearch = state.isAuthenticated && state.isConnected && !isFindingMatch
        val searchButtonText = when {
            !state.isAuthenticated -> "Sign In to Matchmake"
            !state.isConnected -> "Connecting to Arena..."
            isFindingMatch -> "Searching..."
            else -> "FIND MATCH"
        }

        GamePrimaryButton(
            text = searchButtonText,
            onClick = {
                if (canSearch) {
                    isFindingMatch = true
                    onFindOpponent()
                }
            },
            enabled = canSearch,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CompetitiveRuleItem(number: String, description: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(GameRoyalBlue.copy(alpha = 0.35f), CircleShape)
                .border(BorderStroke(1.dp, GameElectricCyan.copy(alpha = 0.5f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = GameElectricCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = description,
            style = GameTypography.secondaryInfo,
            color = GameWhite.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun RuleItem(number: String, description: String) {
    CompetitiveRuleItem(number = number, description = description)
}

/**
 * Section 9 & 10: Searching View with radar animation and cancellation.
 */
@Composable
private fun QuickDuelSearchingContent(
    ticketId: String?,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarScale1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha1"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Radar Sonar Searching Indicator
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            // Expanding pulse wave
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale1)
                    .border(BorderStroke(1.5.dp, GameElectricCyan.copy(alpha = pulseAlpha1)), CircleShape)
            )

            // Inner cyan sphere
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(GameElectricCyan.copy(alpha = 0.12f), CircleShape)
                    .border(BorderStroke(2.dp, GameElectricCyan), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GameElectricCyan,
                    modifier = Modifier.size(52.dp),
                    strokeWidth = 3.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "SEARCHING FOR OPPONENT",
            style = GameTypography.hudLabel.copy(letterSpacing = 1.2.sp),
            fontWeight = FontWeight.Bold,
            color = GameWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Matching with another authenticated player.\nBoth players will receive the exact same solver-verified puzzle.",
            style = GameTypography.secondaryInfo,
            color = GameSecondaryText,
            textAlign = TextAlign.Center
        )

        ticketId?.let { id ->
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(GameRoyalBlue.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "QUEUE TICKET: ",
                    style = MaterialTheme.typography.labelSmall,
                    color = GameSecondaryText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${id.take(10)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = GameElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        GameSecondaryButton(
            text = "Cancel Matchmaking",
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

/**
 * Section 13-15: Match Found Lobby Screen.
 */
@Composable
private fun QuickDuelLobbyContent(
    state: QuickDuelUiState,
    onMarkReady: () -> Unit,
    onLeaveMatch: () -> Unit
) {
    val session = state.currentSession ?: return
    val local = state.localParticipant
    val opponent = state.opponentParticipant

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Match Found Header Panel
        GamePanel(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.2.dp, GameSuccessGreen.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GameSuccessGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MATCH FOUND!",
                            style = GameTypography.hudLabel,
                            fontWeight = FontWeight.Bold,
                            color = GameSuccessGreen
                        )
                    }
                    Text(
                        text = "ID: ${session.matchId.take(8)}...",
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText
                    )
                }

                session.puzzle?.let { puzzle ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Assigned Puzzle: ${puzzle.width}x${puzzle.height} • Checkpoints: ${puzzle.checkpoints.size} • SHA: ${puzzle.fingerprint.take(8)}...",
                        style = GameTypography.secondaryInfo,
                        color = GameGoldHighlight
                    )
                }
            }
        }

        // Versus Comparison Showcase
        PlayerVsOpponentShowcase(
            localName = local?.displayName ?: "You",
            localSubtitle = local?.publicZynpathId ?: "",
            opponentName = opponent?.displayName ?: "Opponent",
            opponentSubtitle = opponent?.publicZynpathId ?: "",
            isWaiting = false,
            localReady = local?.isReady == true,
            opponentReady = opponent?.isReady == true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ready Confirmation / Waiting Indicator
        if (local?.isReady != true) {
            GamePrimaryButton(
                text = "I'M READY!",
                onClick = onMarkReady,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            GamePanel(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.2.dp, GameSuccessGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = GameSuccessGreen,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "You are Ready! Waiting for rival confirmation...",
                        style = GameTypography.bodyEmphasized,
                        color = GameSuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        GameSecondaryButton(
            text = "Leave Match",
            onClick = onLeaveMatch,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun GlowingVsEmblem(
    modifier: Modifier = Modifier,
    isPulsing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vsPulseTransition")
    val glowScale by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 0.94f,
            targetValue = 1.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "vsGlowScale"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = modifier.size(52.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer cyan glow ring
        Box(
            modifier = Modifier
                .size(46.dp)
                .scale(glowScale)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GameElectricCyan.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // Core VS Badge with gold border
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GameMidnightBlue, GameDeepNavy)
                    ),
                    CircleShape
                )
                .border(BorderStroke(1.5.dp, GameGoldHighlight), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = GameGoldHighlight,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun PlayerCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    isOpponent: Boolean,
    isWaiting: Boolean = false,
    readyStatus: Boolean? = null
) {
    val borderColor = when {
        isWaiting -> GameRoyalBlue.copy(alpha = 0.35f)
        isOpponent -> GameGold.copy(alpha = 0.6f)
        else -> GameElectricCyan.copy(alpha = 0.7f)
    }
    val iconTint = when {
        isWaiting -> GameSecondaryText.copy(alpha = 0.5f)
        isOpponent -> GameGoldHighlight
        else -> GameElectricCyan
    }

    GamePanel(
        modifier = modifier,
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GameMidnightBlue, CircleShape)
                    .border(BorderStroke(1.5.dp, borderColor), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = GameTypography.bodyEmphasized,
                fontWeight = FontWeight.Bold,
                color = if (isWaiting) GameSecondaryText else GameWhite,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = GameTypography.secondaryInfo,
                color = if (isWaiting) GameSecondaryText.copy(alpha = 0.6f) else GameElectricCyan,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            if (readyStatus != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ReadyBadge(isReady = readyStatus)
            }
        }
    }
}

@Composable
private fun PlayerVsOpponentShowcase(
    localName: String,
    localSubtitle: String,
    opponentName: String?,
    opponentSubtitle: String?,
    isWaiting: Boolean,
    localReady: Boolean? = null,
    opponentReady: Boolean? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerCard(
            modifier = Modifier.weight(1f),
            title = localName,
            subtitle = localSubtitle,
            isOpponent = false,
            readyStatus = localReady
        )

        GlowingVsEmblem(
            modifier = Modifier.padding(horizontal = 6.dp),
            isPulsing = isWaiting || localReady == true
        )

        PlayerCard(
            modifier = Modifier.weight(1f),
            title = opponentName ?: "Awaiting Rival",
            subtitle = opponentSubtitle ?: "Neutral Silhouette",
            isOpponent = true,
            isWaiting = isWaiting,
            readyStatus = opponentReady
        )
    }
}

@Composable
private fun ReadyBadge(isReady: Boolean) {
    if (isReady) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(GameSuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, GameSuccessGreen.copy(alpha = 0.6f)), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = GameSuccessGreen,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "READY",
                color = GameSuccessGreen,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(GameRoyalBlue.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "WAITING...",
                color = GameSecondaryText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Section 18: Synchronized 3-second Countdown Overlay.
 */
@Composable
private fun QuickDuelCountdownContent(seconds: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "countdownPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "countdownScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameDeepNavy.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "MATCH STARTS IN",
                style = GameTypography.hudLabel.copy(letterSpacing = 2.5.sp),
                fontWeight = FontWeight.Bold,
                color = GameElectricCyan
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                GameGoldHighlight.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .border(BorderStroke(2.dp, GameGoldHighlight), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$seconds",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    color = GameGoldHighlight
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Trace every cell in checkpoint order!",
                style = GameTypography.bodyEmphasized,
                color = GameWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Section 19: Live Dual-Win Gameplay Screen.
 */
@Composable
private fun QuickDuelActiveGameplayContent(
    state: QuickDuelUiState,
    onCellEntered: (com.zynpath.game.core.puzzle.model.GridPosition) -> Boolean,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onForfeit: () -> Unit,
    onSendReaction: (String) -> Unit
) {
    val local = state.localParticipant
    val opponent = state.opponentParticipant
    val totalCells = state.puzzleDefinition?.totalRequiredCells ?: 1
    val localCovered = state.puzzleGameState?.coveredCellCount ?: 0
    val opponentCovered = opponent?.coveredCells ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dual Progress Match Header
        GamePanel(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.2.dp, GameRoyalBlue.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Local Progress
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "You: ${local?.displayName ?: "You"}, $localCovered of $totalCells cells covered"
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(GameMidnightBlue, CircleShape)
                                .border(BorderStroke(1.dp, GameElectricCyan), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GameElectricCyan,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = local?.displayName ?: "You",
                            style = GameTypography.bodyEmphasized.copy(fontSize = 13.sp),
                            fontWeight = FontWeight.Bold,
                            color = GameElectricCyan,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (localCovered.toFloat() / totalCells).coerceIn(0f, 1f) },
                        color = GameElectricCyan,
                        trackColor = GameMidnightBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$localCovered / $totalCells cells",
                        style = MaterialTheme.typography.labelSmall,
                        color = GameElectricCyan.copy(alpha = 0.85f),
                        fontSize = 10.sp
                    )
                }

                // Match Timer & VS Center
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Elapsed match time: ${formatElapsedTime(state.elapsedMatchTimeMs)}"
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlowingVsEmblem(
                        modifier = Modifier.size(32.dp),
                        isPulsing = false
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatElapsedTime(state.elapsedMatchTimeMs),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = GameGoldHighlight
                    )
                    Text(
                        text = "ELAPSED",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = GameSecondaryText
                    )
                }

                // Opponent Progress
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Opponent: ${opponent?.displayName ?: "Opponent"}, $opponentCovered of $totalCells cells covered"
                        },
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = opponent?.displayName ?: "Opponent",
                            style = GameTypography.bodyEmphasized.copy(fontSize = 13.sp),
                            fontWeight = FontWeight.Bold,
                            color = GameGoldHighlight,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(GameMidnightBlue, CircleShape)
                                .border(BorderStroke(1.dp, GameGoldHighlight), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GameGoldHighlight,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (opponentCovered.toFloat() / totalCells).coerceIn(0f, 1f) },
                        color = GameGoldHighlight,
                        trackColor = GameMidnightBlue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$opponentCovered / $totalCells cells",
                        style = MaterialTheme.typography.labelSmall,
                        color = GameGoldHighlight.copy(alpha = 0.85f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Center Puzzle Board Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(GameMidnightBlue.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.35f)), RoundedCornerShape(16.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            state.boardState?.let { boardState ->
                PuzzleBoard(
                    boardState = boardState,
                    isInputEnabled = state.isInputEnabled,
                    onCellEntered = onCellEntered
                )
            } ?: CircularProgressIndicator(color = GameElectricCyan)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gameplay Controls Bar: Undo, Reset, Forfeit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo button
            Button(
                onClick = onUndo,
                enabled = state.canUndo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameMidnightBlue,
                    disabledContainerColor = GameMidnightBlue.copy(alpha = 0.4f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (state.canUndo) GameElectricCyan.copy(alpha = 0.6f) else GameRoyalBlue.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (state.canUndo) GameElectricCyan else GameSecondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Undo",
                    color = if (state.canUndo) GameWhite else GameSecondaryText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            // Reset button
            Button(
                onClick = onReset,
                enabled = state.canReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameMidnightBlue,
                    disabledContainerColor = GameMidnightBlue.copy(alpha = 0.4f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (state.canReset) GameRoyalBlue.copy(alpha = 0.8f) else GameRoyalBlue.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = if (state.canReset) GameWhite else GameSecondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Reset",
                    color = if (state.canReset) GameWhite else GameSecondaryText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            // Forfeit button
            OutlinedButton(
                onClick = onForfeit,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.heightIn(min = 44.dp)
            ) {
                Text(
                    text = "Forfeit",
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Ephemeral Reactions Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GameMidnightBlue.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                .border(BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.4f)), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReactionButton("👍", "THUMBS_UP", onSendReaction)
            ReactionButton("⚡", "LIGHTNING", onSendReaction)
            ReactionButton("🔥", "FIRE", onSendReaction)
            ReactionButton("🤯", "MIND_BLOWN", onSendReaction)
        }
    }
}

@Composable
private fun ReactionButton(
    emoji: String,
    code: String,
    onSend: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(GameRoyalBlue.copy(alpha = 0.2f), CircleShape)
            .semantics {
                role = Role.Button
                contentDescription = "Send reaction $emoji"
            }
            .clickable { onSend(code) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 20.sp)
    }
}

/**
 * Section 26: Server-side dual-win validation overlay.
 */
@Composable
private fun QuickDuelValidationOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameDeepNavy.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center
    ) {
        GamePanel(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp),
            border = BorderStroke(1.2.dp, GameElectricCyan)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = GameElectricCyan,
                    modifier = Modifier.size(52.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "VALIDATING SOLUTION",
                    style = GameTypography.hudLabel,
                    fontWeight = FontWeight.Bold,
                    color = GameWhite,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Server is authoritatively verifying 8-point puzzle compliance, checkpoint sequence, and finish timestamp.",
                    style = GameTypography.secondaryInfo,
                    color = GameSecondaryText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Section 31-33: Authoritative Result Screen.
 */
@Composable
private fun QuickDuelResultContent(
    state: QuickDuelUiState,
    onPlayAgain: () -> Unit,
    onReturnToHub: () -> Unit
) {
    val localResult = state.localResult
    val opponentResult = state.opponentResult
    val isWin = state.isLocalWinner
    val isTie = localResult?.isTie == true
    val resultStatus = localResult?.resultStatus ?: "COMPLETED"

    var isPlayAgainTapped by remember { mutableStateOf(false) }

    val bannerColor = when {
        isWin -> GameGoldHighlight
        isTie -> GameGold
        resultStatus == "FORFEIT" -> Color(0xFFFF5252)
        else -> Color(0xFFFF5252)
    }

    val bannerTitle = when {
        isWin -> "VICTORY!"
        isTie -> "DUEL TIED"
        resultStatus == "FORFEIT" -> "FORFEIT"
        else -> "DEFEAT"
    }

    val bannerSubtitle = when {
        isWin -> "You solved the continuous route first!"
        isTie -> "Both players submitted simultaneous validated solutions!"
        resultStatus == "FORFEIT" -> "Match concluded by player forfeit."
        else -> "Opponent completed the verified path ahead."
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Outcome Banner Panel
        GamePanel(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.5.dp, bannerColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Outcome Icon Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(bannerColor.copy(alpha = 0.15f), CircleShape)
                        .border(BorderStroke(1.5.dp, bannerColor), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWin) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = bannerColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = bannerTitle,
                    style = GameTypography.screenHeading.copy(fontSize = 28.sp, letterSpacing = 2.sp),
                    fontWeight = FontWeight.Black,
                    color = bannerColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = bannerSubtitle,
                    style = GameTypography.bodyEmphasized,
                    color = GameWhite.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Duel Statistics Table Panel
        GamePanel(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MATCH OUTCOME BREAKDOWN",
                    style = GameTypography.hudLabel.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = GameElectricCyan
                )
                Spacer(modifier = Modifier.height(12.dp))

                ResultStatRow(
                    label = "Player",
                    leftVal = state.localParticipant?.displayName ?: "You",
                    rightVal = state.opponentParticipant?.displayName ?: "Opponent",
                    isHeader = true
                )
                ResultStatRow(
                    label = "Outcome",
                    leftVal = if (isWin) "WINNER" else if (isTie) "TIED" else "RUNNER UP",
                    rightVal = if (isWin) "RUNNER UP" else if (isTie) "TIED" else "WINNER",
                    highlightLeft = isWin,
                    highlightRight = !isWin && !isTie
                )
                ResultStatRow(
                    label = "Authoritative Time",
                    leftVal = localResult?.formattedTime() ?: "--",
                    rightVal = opponentResult?.formattedTime() ?: "--"
                )
                ResultStatRow(
                    label = "Server Verification",
                    leftVal = if (localResult?.isValidated == true) "PASSED" else "REJECTED",
                    rightVal = if (opponentResult?.isValidated == true) "PASSED" else "REJECTED"
                )
            }
        }

        // Shared Puzzle Verification Badge
        state.puzzleDefinition?.let { def ->
            GamePanel(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, GameRoyalBlue.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "AUTHORITATIVE PUZZLE INTEGRITY",
                        style = GameTypography.hudLabel.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = GameGoldHighlight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dimensions: ${def.gridSize.width}x${def.gridSize.height} • Checkpoints: ${def.checkpoints.size}",
                        style = GameTypography.secondaryInfo,
                        color = GameWhite
                    )
                    Text(
                        text = "SHA-256: ${def.fingerprint}",
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Play Again Button (Gold Primary Action with Double-Tap Guard)
        GamePrimaryButton(
            text = if (isPlayAgainTapped) "STARTING MATCHMAKING..." else "PLAY AGAIN",
            onClick = {
                if (!isPlayAgainTapped) {
                    isPlayAgainTapped = true
                    onPlayAgain()
                }
            },
            enabled = !isPlayAgainTapped,
            modifier = Modifier.fillMaxWidth()
        )

        // Return to Hub Button
        GameSecondaryButton(
            text = "RETURN TO MULTIPLAYER HUB",
            onClick = onReturnToHub,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ResultStatRow(
    label: String,
    leftVal: String,
    rightVal: String,
    isHeader: Boolean = false,
    highlightLeft: Boolean = false,
    highlightRight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = GameTypography.secondaryInfo,
            color = GameSecondaryText,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = leftVal,
            style = if (isHeader) GameTypography.bodyEmphasized else GameTypography.secondaryInfo,
            fontWeight = if (isHeader || highlightLeft) FontWeight.Bold else FontWeight.Normal,
            color = if (highlightLeft) GameSuccessGreen else GameWhite,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = rightVal,
            style = if (isHeader) GameTypography.bodyEmphasized else GameTypography.secondaryInfo,
            fontWeight = if (isHeader || highlightRight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlightRight) GameSuccessGreen else GameWhite,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

private fun MatchResultDto.formattedTime(): String {
    return if (solveTimeMs != null && solveTimeMs > 0) {
        String.format("%.2fs", solveTimeMs / 1000.0)
    } else {
        "--"
    }
}

/**
 * Section 49: Error and Cancelled State View.
 */
@Composable
private fun QuickDuelErrorContent(
    errorMessage: String,
    onReturnToHub: () -> Unit,
    onTryAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GamePanel(
            contentPadding = 24.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color(0x26EF4444))
                        .border(1.5.dp, Color(0xFFEF4444), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "MATCHMAKING NOTICE",
                    style = GameTypography.screenHeading,
                    fontWeight = FontWeight.Black,
                    color = GameWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = errorMessage,
                    style = GameTypography.bodyMedium,
                    color = GameSecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                GamePrimaryButton(
                    text = "FIND NEW DUEL",
                    onClick = onTryAgain,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                GameSecondaryButton(
                    text = "RETURN TO HUB",
                    onClick = onReturnToHub,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Ephemeral floating reaction badge.
 */
@Composable
private fun QuickDuelReactionBadge(
    reaction: ReactionPopup,
    modifier: Modifier = Modifier
) {
    Surface(
        color = GameMidnightBlue.copy(alpha = 0.95f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, GameElectricCyan),
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = reaction.emojiChar, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = reaction.labelText,
                    style = GameTypography.bodyEmphasized,
                    fontWeight = FontWeight.Bold,
                    color = GameWhite
                )
                Text(
                    text = if (reaction.isLocalPlayer) "You" else "Opponent",
                    style = GameTypography.secondaryInfo,
                    color = GameElectricCyan,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun formatElapsedTime(elapsedMs: Long): String {
    val totalSeconds = elapsedMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (elapsedMs % 1000) / 100
    return String.format("%02d:%02d.%d", minutes, seconds, tenths)
}
