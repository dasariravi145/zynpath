package com.zynpath.game.feature.multiplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.CharcoalNavy
import com.zynpath.game.core.designsystem.theme.DeepIndigo
import com.zynpath.game.core.designsystem.theme.EmeraldGreen
import com.zynpath.game.core.designsystem.theme.GunmetalCard
import com.zynpath.game.core.designsystem.theme.MutedSlate
import com.zynpath.game.core.designsystem.theme.OffWhiteText
import com.zynpath.game.core.designsystem.theme.PathNeonCyan
import com.zynpath.game.core.multiplayer.model.ClientMatchState
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import kotlin.math.roundToInt

@Composable
fun MultiplayerHubScreen(
    initialMode: GameMode = GameMode.QUICK_DUEL,
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToQuickDuel: () -> Unit = {},
    onNavigateToFriendDuel: (String?) -> Unit = {},
    onNavigateToMiniLeague: () -> Unit = {},
    viewModel: MultiplayerHubViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MultiplayerTopBar(
                isConnected = state.isConnected,
                onBackClick = onBackClick
            )
        },
        containerColor = DeepIndigo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. Countdown Overlay
                state.isCountdown -> {
                    CountdownContent(seconds = state.countdownSeconds ?: 3)
                }
                // 2. In Lobby (Match found, waiting for ready or active)
                state.isInLobby || state.isActiveMatch -> {
                    MatchLobbyContent(
                        state = state,
                        onMarkReady = { viewModel.markReady() },
                        onLeaveMatch = { viewModel.leaveMatch() }
                    )
                }
                // 3. Searching for Quick Duel
                state.isSearching -> {
                    MatchWaitingContent(
                        ticket = state.ticketStatus,
                        onCancel = { viewModel.cancelMatchmaking() }
                    )
                }
                // 4. Default Hub Home
                else -> {
                    MultiplayerHomeContent(
                        state = state,
                        onStartQuickDuel = {
                            onNavigateToQuickDuel()
                        },
                        onFriendTargetIdChanged = { viewModel.onFriendTargetIdChanged(it) },
                        onCreateFriendDuel = {
                            onNavigateToFriendDuel(state.friendTargetIdInput.takeIf { it.isNotBlank() })
                        },
                        onMiniLeagueRoomNameChanged = { viewModel.onMiniLeagueRoomNameChanged(it) },
                        onMiniLeagueMaxParticipantsChanged = { viewModel.onMiniLeagueMaxParticipantsChanged(it) },
                        onCreateMiniLeague = { onNavigateToMiniLeague() },
                        onNavigateToSignIn = onNavigateToSignIn,
                        onClearError = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MultiplayerTopBar(
    isConnected: Boolean,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CharcoalNavy)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OffWhiteText
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Online Multiplayer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            Text(
                text = "Dual-Win Verified Matches",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate
            )
        }

        // Connection indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(if (isConnected) EmeraldGreen.copy(alpha = 0.2f) else CharcoalNavy, RoundedCornerShape(12.dp))
                .border(1.dp, if (isConnected) EmeraldGreen else MutedSlate.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isConnected) EmeraldGreen else Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isConnected) "Live" else "Offline",
                style = MaterialTheme.typography.labelSmall,
                color = if (isConnected) EmeraldGreen else MutedSlate
            )
        }
    }
}

@Composable
private fun MultiplayerHomeContent(
    state: MultiplayerHubUiState,
    onStartQuickDuel: () -> Unit,
    onFriendTargetIdChanged: (String) -> Unit,
    onCreateFriendDuel: () -> Unit,
    onMiniLeagueRoomNameChanged: (String) -> Unit,
    onMiniLeagueMaxParticipantsChanged: (Int) -> Unit,
    onCreateMiniLeague: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onClearError: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Error banner
        state.errorMessage?.let { error ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1F24)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF6B6B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = Color(0xFFFFD1D1),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClearError, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color(0xFFFFD1D1))
                    }
                }
            }
        }

        // Authentication status card
        if (!state.isAuthenticated) {
            GuestNoticeCard(onNavigateToSignIn = onNavigateToSignIn)
        } else {
            PlayerProfileHeader(session = state.authSession!!)
        }

        // Mode 1: Quick Duel (1v1 Matchmaking)
        QuickDuelCard(
            enabled = state.isAuthenticated,
            onStartQuickDuel = onStartQuickDuel
        )

        // Mode 2: Friend Duel (Invited 1v1)
        FriendDuelCard(
            enabled = state.isAuthenticated,
            targetId = state.friendTargetIdInput,
            onTargetIdChanged = onFriendTargetIdChanged,
            onChallenge = onCreateFriendDuel
        )

        // Mode 3: Mini League (2-5 Players Room)
        MiniLeagueCard(
            enabled = state.isAuthenticated,
            roomName = state.miniLeagueRoomNameInput,
            maxParticipants = state.miniLeagueMaxParticipants,
            onRoomNameChanged = onMiniLeagueRoomNameChanged,
            onMaxParticipantsChanged = onMiniLeagueMaxParticipantsChanged,
            onCreateRoom = onCreateMiniLeague
        )
    }
}

@Composable
private fun GuestNoticeCard(onNavigateToSignIn: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = AccentGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Account Sign-In Required",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Online multiplayer matches require an authenticated Zynpath account to safeguard competitive fairness. Solo Play and Daily Challenges remain 100% offline.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onNavigateToSignIn,
                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In / Create Account", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PlayerProfileHeader(session: com.zynpath.game.core.auth.model.AuthSession) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(DeepIndigo, CircleShape)
                    .border(2.dp, PathNeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PathNeonCyan)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Text(
                    text = "ID: ${session.publicZynpathId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PathNeonCyan
                )
            }
        }
    }
}

@Composable
private fun QuickDuelCard(
    enabled: Boolean,
    onStartQuickDuel: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PathNeonCyan.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PathNeonCyan)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Quick Duel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OffWhiteText
                    )
                    Text(
                        text = "Automatic 1v1 Matchmaking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Get matched instantly with an opponent. Both players receive the exact same solver-verified continuous puzzle. First to complete the dual-win route wins!",
                style = MaterialTheme.typography.bodySmall,
                color = OffWhiteText.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onStartQuickDuel,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Find Quick Duel Match", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FriendDuelCard(
    enabled: Boolean,
    targetId: String,
    onTargetIdChanged: (String) -> Unit,
    onChallenge: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(EmeraldGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldGreen)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Friend Duel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OffWhiteText
                    )
                    Text(
                        text = "Direct Invited 1v1 Challenge",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = targetId,
                onValueChange = onTargetIdChanged,
                label = { Text("Friend's Public Zynpath ID (e.g. ZYN-8492)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = enabled
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onChallenge,
                enabled = enabled && targetId.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Challenge Friend", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MiniLeagueCard(
    enabled: Boolean,
    roomName: String,
    maxParticipants: Int,
    onRoomNameChanged: (String) -> Unit,
    onMaxParticipantsChanged: (Int) -> Unit,
    onCreateRoom: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AccentGold.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = AccentGold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Mini League",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OffWhiteText
                    )
                    Text(
                        text = "2 to 5 Total Participants Room",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = roomName,
                onValueChange = onRoomNameChanged,
                label = { Text("Room Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = enabled
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Capacity: $maxParticipants Players Total (Including Host)",
                style = MaterialTheme.typography.bodySmall,
                color = OffWhiteText
            )
            Slider(
                value = maxParticipants.toFloat(),
                onValueChange = { onMaxParticipantsChanged(it.roundToInt()) },
                valueRange = 2f..5f,
                steps = 2,
                enabled = enabled
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onCreateRoom,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Mini League Rooms & Lobby", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MatchWaitingContent(
    ticket: com.zynpath.game.core.multiplayer.model.MatchmakingTicketStatus?,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(PathNeonCyan.copy(alpha = 0.15f), CircleShape)
                .border(2.dp, PathNeonCyan, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = PathNeonCyan,
                modifier = Modifier.size(60.dp),
                strokeWidth = 3.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Searching for Opponent...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = OffWhiteText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Matching with an active player in Quick Duel queue.\nBoth players will receive identical puzzle seeds.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedSlate,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onCancel,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Cancel Matchmaking")
        }
    }
}

@Composable
private fun CountdownContent(seconds: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepIndigo.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MATCH STARTS IN",
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = PathNeonCyan
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$seconds",
                fontSize = 96.sp,
                fontWeight = FontWeight.Black,
                color = AccentGold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Get ready to trace your path!",
                style = MaterialTheme.typography.bodyMedium,
                color = OffWhiteText
            )
        }
    }
}

@Composable
private fun MatchLobbyContent(
    state: MultiplayerHubUiState,
    onMarkReady: () -> Unit,
    onLeaveMatch: () -> Unit
) {
    val session = state.currentSession ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Lobby Header
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = session.gameMode.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PathNeonCyan
                )
                Text(
                    text = "Match ID: ${session.matchId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate
                )
                session.puzzle?.let { p ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Puzzle: ${p.width}x${p.height} • SHA: ${p.fingerprint.take(8)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentGold
                    )
                }
            }
        }

        // Participant list
        Text(
            text = "Participants (${session.participants.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OffWhiteText
        )

        session.participants.forEach { participant ->
            ParticipantRow(participant = participant)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        val myId = state.authSession?.playerId
        val me = session.participants.firstOrNull { it.playerId == myId }
        val isReady = me?.isReady == true

        if (!isReady && !state.isActiveMatch) {
            Button(
                onClick = onMarkReady,
                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I'm Ready!", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        } else if (state.isActiveMatch) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Match is ACTIVE!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "Real-time transport and authoritative puzzle verified. Full interactive gameplay board integration will activate in Prompt 21.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhiteText
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onLeaveMatch,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Leave Match")
        }
    }
}

@Composable
private fun ParticipantRow(participant: MatchParticipantDto) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(DeepIndigo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PathNeonCyan)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = participant.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Text(
                    text = participant.publicZynpathId,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate
                )
            }
            if (participant.isReady) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ready", color = EmeraldGreen, style = MaterialTheme.typography.labelMedium)
                }
            } else {
                Text("Waiting", color = MutedSlate, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
