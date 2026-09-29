package com.zynpath.game.feature.multiplayer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.CharcoalNavy
import com.zynpath.game.core.designsystem.theme.DeepIndigo
import com.zynpath.game.core.designsystem.theme.EmeraldGreen
import com.zynpath.game.core.designsystem.theme.ErrorRed
import com.zynpath.game.core.designsystem.theme.GunmetalCard
import com.zynpath.game.core.designsystem.theme.MutedSlate
import com.zynpath.game.core.designsystem.theme.OffWhiteText
import com.zynpath.game.core.designsystem.theme.PathNeonCyan
import com.zynpath.game.core.designsystem.theme.WarningAmber
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.multiplayer.model.MiniLeagueInvitation
import com.zynpath.game.core.multiplayer.model.MiniLeagueParticipant
import com.zynpath.game.core.multiplayer.model.MiniLeagueRoom
import com.zynpath.game.core.puzzle.ui.PuzzleBoard
import com.zynpath.game.core.social.model.FriendItem
import kotlin.math.roundToInt

/**
 * Screen rendering the complete Mini League 2-5 player experience (Prompt 23).
 */
@Composable
fun MiniLeagueScreen(
    initialRoomCode: String? = null,
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToFriends: () -> Unit,
    viewModel: MiniLeagueViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(initialRoomCode) {
        if (!initialRoomCode.isNullOrBlank()) {
            viewModel.onRoomCodeInputChange(initialRoomCode)
            viewModel.onTabSelected(MiniLeagueEntryTab.JOIN_CODE)
            viewModel.onJoinRoomByCode(initialRoomCode)
        }
    }

    BackHandler {
        if (state.isActiveGameplay) {
            viewModel.onShowExitDialog(true)
        } else if (state.isInRoom) {
            viewModel.onLeaveRoom()
        } else {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            MiniLeagueTopBar(
                state = state,
                onBackClick = {
                    if (state.isActiveGameplay) {
                        viewModel.onShowExitDialog(true)
                    } else if (state.isInRoom) {
                        viewModel.onLeaveRoom()
                    } else {
                        onBackClick()
                    }
                }
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
                !state.isAuthenticated -> {
                    UnauthenticatedMiniLeagueView(onNavigateToSignIn = onNavigateToSignIn)
                }
                state.isCompleted -> {
                    MiniLeagueResultView(
                        state = state,
                        onPlayAgain = { viewModel.leaveMatch() },
                        onReturnToMultiplayer = onBackClick
                    )
                }
                state.isActiveGameplay || state.isValidatingCompletion -> {
                    MiniLeagueLiveGameView(
                        state = state,
                        viewModel = viewModel
                    )
                }
                state.isCountdown -> {
                    MiniLeagueCountdownView(seconds = state.countdownSeconds ?: 3)
                }
                state.isInRoom -> {
                    MiniLeagueLobbyView(
                        state = state,
                        context = context,
                        viewModel = viewModel
                    )
                }
                else -> {
                    MiniLeagueEntryView(
                        state = state,
                        viewModel = viewModel,
                        onNavigateToFriends = onNavigateToFriends
                    )
                }
            }

            // Error snackbar / alert banner
            state.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                        }
                    }
                }
            }

            // Exit confirmation dialog
            if (state.showExitDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.onShowExitDialog(false) },
                    title = { Text("Leave Mini League Match?") },
                    text = {
                        Text("Exiting now will forfeit the match and record an Unfinished result for this room.")
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onForfeit() },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                        ) {
                            Text("Forfeit & Leave")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.onShowExitDialog(false) }) {
                            Text("Cancel", color = OffWhiteText)
                        }
                    },
                    containerColor = CharcoalNavy,
                    titleContentColor = OffWhiteText,
                    textContentColor = MutedSlate
                )
            }

            // Invite friend dialog
            if (state.showInviteDialog && state.isInRoom) {
                MiniLeagueInviteFriendsDialog(
                    friends = state.acceptedFriends,
                    roomParticipants = state.currentRoom?.participants ?: emptyList(),
                    isSendingInvite = state.isSendingInvite,
                    onDismiss = { viewModel.onShowInviteDialog(false) },
                    onInvite = { friendId -> viewModel.onInviteFriend(friendId) },
                    onNavigateToFriends = onNavigateToFriends
                )
            }
        }
    }
}

@Composable
private fun MiniLeagueTopBar(
    state: MiniLeagueUiState,
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OffWhiteText)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mini League",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            val subtitle = when {
                state.isActiveGameplay -> "Live Match • ${formatDuration(state.elapsedMatchTimeMs)}"
                state.isInRoom -> "Room Lobby • Code: ${state.currentRoom?.roomCode ?: "..."}"
                else -> "2 to 5 Player Private Rooms"
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (state.isActiveGameplay) PathNeonCyan else MutedSlate
            )
        }
        if (state.isInRoom) {
            val count = state.currentRoom?.currentParticipants ?: 0
            val max = state.currentRoom?.maxParticipants ?: 5
            Card(
                colors = CardDefaults.cardColors(containerColor = GunmetalCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$count/$max",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniLeagueEntryView(
    state: MiniLeagueUiState,
    viewModel: MiniLeagueViewModel,
    onNavigateToFriends: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = state.selectedEntryTab.ordinal,
            containerColor = CharcoalNavy,
            contentColor = AccentGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedEntryTab.ordinal]),
                    color = AccentGold
                )
            }
        ) {
            Tab(
                selected = state.selectedEntryTab == MiniLeagueEntryTab.CREATE,
                onClick = { viewModel.onTabSelected(MiniLeagueEntryTab.CREATE) },
                text = { Text("Create Room", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = state.selectedEntryTab == MiniLeagueEntryTab.JOIN_CODE,
                onClick = { viewModel.onTabSelected(MiniLeagueEntryTab.JOIN_CODE) },
                text = { Text("Join Code", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = state.selectedEntryTab == MiniLeagueEntryTab.INVITATIONS,
                onClick = {
                    viewModel.onTabSelected(MiniLeagueEntryTab.INVITATIONS)
                    viewModel.refreshInvitationsAndFriends()
                },
                text = {
                    val count = state.incomingInvitations.size
                    Text(
                        text = if (count > 0) "Invites ($count)" else "Invites",
                        fontWeight = FontWeight.Bold,
                        color = if (count > 0) AccentGold else OffWhiteText
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (state.selectedEntryTab) {
            MiniLeagueEntryTab.CREATE -> {
                CreateRoomTabContent(
                    isCreating = state.isCreatingRoom,
                    onCreateRoom = { maxP -> viewModel.onCreateRoom(maxP) }
                )
            }
            MiniLeagueEntryTab.JOIN_CODE -> {
                JoinCodeTabContent(
                    roomCode = state.roomCodeInput,
                    isJoining = state.isJoiningRoom,
                    onCodeChange = { viewModel.onRoomCodeInputChange(it) },
                    onJoinClick = { viewModel.onJoinRoomByCode() }
                )
            }
            MiniLeagueEntryTab.INVITATIONS -> {
                InvitationsTabContent(
                    invitations = state.incomingInvitations,
                    onAccept = { id -> viewModel.onRespondInvitation(id, accept = true) },
                    onDecline = { id -> viewModel.onRespondInvitation(id, accept = false) },
                    onRefresh = { viewModel.refreshInvitationsAndFriends() }
                )
            }
        }
    }
}

@Composable
private fun CreateRoomTabContent(
    isCreating: Boolean,
    onCreateRoom: (Int) -> Unit
) {
    var capacity by remember { mutableFloatStateOf(5f) }

    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Host a Private Mini League",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create a private room for 2 to 5 total participants. You will receive a 6-character room code to share with friends. All participants solve the same solver-verified continuous puzzle simultaneously.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Player Capacity: ${capacity.roundToInt()} Players Total",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AccentGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = capacity,
                onValueChange = { capacity = it },
                valueRange = 2f..5f,
                steps = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("2 Players (1v1)", style = MaterialTheme.typography.labelSmall, color = MutedSlate)
                Text("5 Players (Max)", style = MaterialTheme.typography.labelSmall, color = MutedSlate)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onCreateRoom(capacity.roundToInt()) },
                enabled = !isCreating,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DeepIndigo, strokeWidth = 2.dp)
                } else {
                    Text("Create Room", color = DeepIndigo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun JoinCodeTabContent(
    roomCode: String,
    isJoining: Boolean,
    onCodeChange: (String) -> Unit,
    onJoinClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Join Room with Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter the 6-character room code provided by the host to join their private Mini League lobby.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = roomCode,
                onValueChange = { if (it.length <= 6) onCodeChange(it) },
                label = { Text("6-Character Room Code") },
                placeholder = { Text("e.g. 7K2X9P") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onJoinClick,
                enabled = roomCode.length == 6 && !isJoining,
                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isJoining) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DeepIndigo, strokeWidth = 2.dp)
                } else {
                    Text("Join Mini League", color = DeepIndigo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun InvitationsTabContent(
    invitations: List<MiniLeagueInvitation>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pending Room Invitations",
                style = MaterialTheme.typography.titleSmall,
                color = MutedSlate
            )
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = MutedSlate)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (invitations.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GunmetalCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pending Mini League invitations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlate,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(invitations, key = { it.invitationId }) { inv ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(AccentGold.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = AccentGold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${inv.hostDisplayName} invited you",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OffWhiteText
                                )
                                Text(
                                    text = "Room Code: ${inv.roomCode}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AccentGold
                                )
                            }
                            Button(
                                onClick = { onAccept(inv.invitationId) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Join", color = DeepIndigo, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = { onDecline(inv.invitationId) }) {
                                Icon(Icons.Default.Close, contentDescription = "Decline", tint = MutedSlate)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniLeagueLobbyView(
    state: MiniLeagueUiState,
    context: Context,
    viewModel: MiniLeagueViewModel
) {
    val room = state.currentRoom ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Room Code Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = GunmetalCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ROOM CODE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.roomCode,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentGold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Zynpath Room Code", room.roomCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Room code copied!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code", color = AccentGold)
                    }

                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Join my Zynpath Mini League puzzle race! Room Code: ${room.roomCode}\nhttps://zynpath.com/minileague?code=${room.roomCode}"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Room Invitation"))
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = PathNeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = PathNeonCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Participants Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Participants (${room.currentParticipants}/${room.maxParticipants})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            if (room.currentParticipants < room.maxParticipants) {
                TextButton(onClick = { viewModel.onShowInviteDialog(true) }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = PathNeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invite Friends", color = PathNeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Participants List (2 to 5 cards)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            room.participants.forEach { participant ->
                ParticipantLobbyCard(participant = participant, currentUserId = state.authSession?.playerId)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ready and Start Controls
        if (state.isHost) {
            // Host Controls
            Button(
                onClick = { viewModel.onStartMatch() },
                enabled = state.canStartMatch,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isStartingMatch) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DeepIndigo, strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = DeepIndigo)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Mini League Race", color = DeepIndigo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            if (!state.canStartMatch) {
                Spacer(modifier = Modifier.height(6.dp))
                val reason = when {
                    room.currentParticipants < 2 -> "Need at least 2 participants to start."
                    !room.allReady -> "Waiting for all participants to confirm Ready."
                    else -> "Preparing match..."
                }
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningAmber,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle Ready Status Button
        OutlinedButton(
            onClick = { viewModel.onToggleReady() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (state.isLocalReady) EmeraldGreen else PathNeonCyan
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                if (state.isLocalReady) Icons.Default.CheckCircle else Icons.Default.Check,
                contentDescription = null,
                tint = if (state.isLocalReady) EmeraldGreen else PathNeonCyan
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (state.isLocalReady) "I AM READY (Tap to Unready)" else "TAP TO CONFIRM READY",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Leave Room Button
        TextButton(
            onClick = { viewModel.onLeaveRoom() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Leave Room", color = ErrorRed, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ParticipantLobbyCard(
    participant: MiniLeagueParticipant,
    currentUserId: String?
) {
    val isMe = participant.playerId == currentUserId

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) CharcoalNavy else GunmetalCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isMe) 1.dp else 0.dp,
                color = if (isMe) PathNeonCyan.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (participant.isHost) AccentGold.copy(alpha = 0.2f) else PathNeonCyan.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participant.displayName.take(1).uppercase(),
                    color = if (participant.isHost) AccentGold else PathNeonCyan,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = participant.displayName + (if (isMe) " (You)" else ""),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = OffWhiteText
                    )
                    if (participant.isHost) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "HOST",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepIndigo,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = if (participant.isConnected) "Connected" else "Connecting...",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (participant.isConnected) EmeraldGreen else MutedSlate
                )
            }

            // Ready Chip
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (participant.isReady) EmeraldGreen.copy(alpha = 0.2f) else CharcoalNavy
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (participant.isReady) Icons.Default.CheckCircle else Icons.Default.Refresh,
                        contentDescription = null,
                        tint = if (participant.isReady) EmeraldGreen else MutedSlate,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (participant.isReady) "READY" else "NOT READY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (participant.isReady) EmeraldGreen else MutedSlate
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniLeagueCountdownView(seconds: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MINI LEAGUE RACE STARTING",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (seconds > 0) seconds.toString() else "GO!",
                fontSize = 96.sp,
                fontWeight = FontWeight.Black,
                color = if (seconds > 0) PathNeonCyan else EmeraldGreen,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Solve the solver-verified continuous puzzle!\nFirst to complete the valid route wins!",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSlate,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MiniLeagueLiveGameView(
    state: MiniLeagueUiState,
    viewModel: MiniLeagueViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Compact 2-5 Player Live Progress Panel
        MiniLeagueParticipantsProgressPanel(
            participants = state.currentSession?.participants ?: emptyList(),
            currentUserId = state.authSession?.playerId
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Puzzle Board Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            state.boardState?.let { board ->
                PuzzleBoard(
                    boardState = board,
                    onCellEntered = { pos -> viewModel.handleCellPointerMove(pos) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (state.isValidatingCompletion) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalNavy.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AccentGold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Validating route with server...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = OffWhiteText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Controls: Undo, Reset, Quick Reactions, Exit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.undo() },
                    enabled = state.canUndo
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (state.canUndo) OffWhiteText else MutedSlate.copy(alpha = 0.4f)
                    )
                }

                IconButton(
                    onClick = { viewModel.reset() },
                    enabled = state.canReset
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = if (state.canReset) OffWhiteText else MutedSlate.copy(alpha = 0.4f)
                    )
                }
            }

            // Quick Preset Reactions
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("🔥", "⚡", "👏", "😱").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GunmetalCard, CircleShape)
                            .clickable { viewModel.sendReaction(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 18.sp)
                    }
                }
            }

            // Exit / Forfeit Action
            OutlinedButton(
                onClick = { viewModel.onShowExitDialog(true) },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Exit", color = ErrorRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Compact horizontal progress panel for 2 to 5 participants.
 */
@Composable
private fun MiniLeagueParticipantsProgressPanel(
    participants: List<MatchParticipantDto>,
    currentUserId: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GunmetalCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(participants, key = { it.playerId }) { p ->
                    val isMe = p.playerId == currentUserId
                    CompactPlayerProgressCard(p = p, isMe = isMe)
                }
            }
        }
    }
}

@Composable
private fun CompactPlayerProgressCard(
    p: MatchParticipantDto,
    isMe: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) CharcoalNavy else DeepIndigo
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .width(110.dp)
            .border(
                width = if (isMe) 1.dp else 0.dp,
                color = if (isMe) PathNeonCyan else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = p.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isMe) PathNeonCyan else OffWhiteText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (p.isCompleted) {
                    val rankText = when (p.finishOrder) {
                        1 -> "1st"
                        2 -> "2nd"
                        3 -> "3rd"
                        4 -> "4th"
                        5 -> "5th"
                        else -> "Done"
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AccentGold),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = rankText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = DeepIndigo,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (p.coveredCellsCount / 25f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (p.isCompleted) EmeraldGreen else AccentGold,
                trackColor = CharcoalNavy
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${p.coveredCellsCount} cells",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MutedSlate
                )
                Text(
                    text = "CP ${p.lastCheckpoint}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = AccentGold
                )
            }
        }
    }
}

@Composable
private fun MiniLeagueResultView(
    state: MiniLeagueUiState,
    onPlayAgain: () -> Unit,
    onReturnToMultiplayer: () -> Unit
) {
    val results = state.sortedResults

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MINI LEAGUE STANDINGS",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AccentGold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Authoritative Standings (1st to 5th)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            results.forEach { r ->
                val isMe = r.playerId == state.authSession?.playerId
                ResultStandingCard(result = r, isMe = isMe)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPlayAgain,
            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Play Another Mini League", color = DeepIndigo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onReturnToMultiplayer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Return to Multiplayer", color = OffWhiteText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResultStandingCard(
    result: MatchResultDto,
    isMe: Boolean
) {
    val rankBadgeColor = when (result.finishOrder) {
        1 -> AccentGold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MutedSlate
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) CharcoalNavy else GunmetalCard
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isMe) 1.dp else 0.dp,
                color = if (isMe) PathNeonCyan else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(rankBadgeColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (result.finishOrder > 0) "#${result.finishOrder}" else "-",
                    color = rankBadgeColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.displayName + (if (isMe) " (You)" else ""),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Text(
                    text = if (result.completed && result.solveTimeMs != null) {
                        "Time: ${formatDuration(result.solveTimeMs)}"
                    } else {
                        result.resultStatus
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (result.completed) EmeraldGreen else MutedSlate
                )
            }
            if (result.isWinner) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "WINNER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = DeepIndigo,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniLeagueInviteFriendsDialog(
    friends: List<FriendItem>,
    roomParticipants: List<MiniLeagueParticipant>,
    isSendingInvite: Boolean,
    onDismiss: () -> Unit,
    onInvite: (String) -> Unit,
    onNavigateToFriends: () -> Unit
) {
    val participantIds = remember(roomParticipants) { roomParticipants.map { it.playerId }.toSet() }
    val eligibleFriends = remember(friends, participantIds) {
        friends.filter { it.friendPlayerId !in participantIds }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Invite Friends to Mini League", color = OffWhiteText, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select accepted friends to invite to this room (Max 5 total players).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (eligibleFriends.isEmpty()) {
                    Text(
                        text = "No available accepted friends to invite.\nAdd friends in the Friends screen first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSlate,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(eligibleFriends, key = { it.friendPlayerId }) { friend ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(GunmetalCard, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = friend.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OffWhiteText
                                )
                                Button(
                                    onClick = { onInvite(friend.publicZynpathId) },
                                    enabled = !isSendingInvite,
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Invite", color = DeepIndigo, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = OffWhiteText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onNavigateToFriends()
            }) {
                Text("Manage Friends", color = PathNeonCyan)
            }
        },
        containerColor = CharcoalNavy
    )
}

@Composable
private fun UnauthenticatedMiniLeagueView(onNavigateToSignIn: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = GunmetalCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AccentGold.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = AccentGold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Sign In Required",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mini League private rooms require an authenticated Zynpath account to create rooms, invite friends, and participate in verified multiplayer races.\n\nSolo Play and offline Daily Challenge remain fully accessible as Guest.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSlate,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToSignIn,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In to Zynpath", color = DeepIndigo, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val millis = (durationMs % 1000) / 10
    return String.format("%02d:%02d.%02d", minutes, seconds, millis)
}
