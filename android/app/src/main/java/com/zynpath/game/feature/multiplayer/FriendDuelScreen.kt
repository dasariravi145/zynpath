package com.zynpath.game.feature.multiplayer

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.zynpath.game.core.multiplayer.model.FriendDuelInvitationDto
import com.zynpath.game.core.multiplayer.model.MatchParticipantDto
import com.zynpath.game.core.multiplayer.model.MatchResultDto
import com.zynpath.game.core.puzzle.ui.PuzzleBoard
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.PlayerPresenceState

/**
 * Screen rendering the complete Friend Duel experience (Prompt 22).
 */
@Composable
fun FriendDuelScreen(
    initialTargetPublicZynpathId: String? = null,
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToFriends: () -> Unit,
    viewModel: FriendDuelViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle initial target ID if passed via navigation
    androidx.compose.runtime.LaunchedEffect(initialTargetPublicZynpathId) {
        if (!initialTargetPublicZynpathId.isNullOrBlank()) {
            viewModel.onTargetIdInputChanged(initialTargetPublicZynpathId)
            viewModel.onTabSelected(FriendDuelTab.DIRECT_ID)
        }
    }

    // Android back handler
    BackHandler {
        if (state.isActiveGameplay) {
            viewModel.requestForfeit()
        } else {
            viewModel.leaveMatch()
            onBackClick()
        }
    }

    // Forfeit Confirmation Dialog
    if (state.showForfeitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissForfeitDialog() },
            title = {
                Text(
                    text = "Forfeit Match?",
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
            },
            text = {
                Text(
                    text = "Leaving an active Friend Duel forfeits the match and awards victory to your friend. Are you sure you want to forfeit?",
                    color = MutedSlate
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmForfeit() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Forfeit Match", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissForfeitDialog() }) {
                    Text("Stay in Duel", color = PathNeonCyan)
                }
            },
            containerColor = CharcoalNavy
        )
    }

    Scaffold(
        topBar = {
            FriendDuelTopBar(
                isConnected = state.isConnected,
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
        containerColor = DeepIndigo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. Authoritative Completed Result Screen
                state.isCompleted -> {
                    FriendDuelResultContent(
                        state = state,
                        onRequestRematch = { viewModel.onRequestRematch() },
                        onAcceptRematch = { viewModel.onAcceptRematch() },
                        onDeclineRematch = { viewModel.onDeclineRematch() },
                        onBackToFriends = {
                            viewModel.leaveMatch()
                            onNavigateToFriends()
                        },
                        onBackToHub = {
                            viewModel.leaveMatch()
                            onBackClick()
                        }
                    )
                }

                // 2. Authoritative Validating Solution Overlay
                state.isValidatingCompletion -> {
                    FriendDuelValidatingOverlay()
                }

                // 3. Synchronized 3-second Countdown Overlay
                state.isCountdown -> {
                    state.countdownSeconds?.let { sec ->
                        FriendDuelCountdownContent(seconds = sec)
                    }
                }

                // 4. Live Dual-Win Competitive Gameplay
                state.isActiveGameplay -> {
                    FriendDuelActiveGameplayContent(
                        state = state,
                        onCellEntered = { viewModel.onCellEntered(it) },
                        onUndo = { viewModel.undo() },
                        onReset = { viewModel.reset() },
                        onForfeit = { viewModel.requestForfeit() },
                        onSendReaction = { viewModel.sendReaction(it) }
                    )
                }

                // 5. Match Found Lobby (Waiting for Ready Window)
                state.isMatchFound -> {
                    FriendDuelLobbyContent(
                        state = state,
                        onMarkReady = { viewModel.onReadyClicked() },
                        onLeaveMatch = {
                            viewModel.leaveMatch()
                            onBackClick()
                        }
                    )
                }

                // 6. Friend Selection & Invitations (Default View)
                else -> {
                    FriendDuelSelectionContent(
                        state = state,
                        onTabSelected = { viewModel.onTabSelected(it) },
                        onTargetIdChanged = { viewModel.onTargetIdInputChanged(it) },
                        onInviteFriend = { viewModel.onInviteFriendClicked(it) },
                        onDirectChallenge = { viewModel.onDirectChallengeClicked() },
                        onAcceptInvitation = { viewModel.onAcceptInvitationClicked(it) },
                        onDeclineInvitation = { viewModel.onDeclineInvitationClicked(it) },
                        onCancelInvitation = { viewModel.onCancelInvitationClicked(it) },
                        onRefresh = { viewModel.refreshFriendsAndInvitations() },
                        onNavigateToSignIn = onNavigateToSignIn,
                        onClearError = { viewModel.clearError() }
                    )
                }
            }

            // Floating Ephemeral Reaction Popup
            AnimatedVisibility(
                visible = state.activeReactionPopup != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                state.activeReactionPopup?.let { popup ->
                    ReactionBadge(popup = popup)
                }
            }
        }
    }
}

@Composable
private fun FriendDuelTopBar(
    isConnected: Boolean,
    isActiveGameplay: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CharcoalNavy)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OffWhiteText
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Friend Duel 1v1",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            Text(
                text = if (isActiveGameplay) "Live Match in Progress" else "Private Verified 1v1 Challenge",
                style = MaterialTheme.typography.labelSmall,
                color = if (isActiveGameplay) EmeraldGreen else MutedSlate
            )
        }

        // Live connection indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    if (isConnected) EmeraldGreen.copy(alpha = 0.2f) else CharcoalNavy,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    if (isConnected) EmeraldGreen else MutedSlate.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
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
private fun FriendDuelSelectionContent(
    state: FriendDuelUiState,
    onTabSelected: (FriendDuelTab) -> Unit,
    onTargetIdChanged: (String) -> Unit,
    onInviteFriend: (FriendItem) -> Unit,
    onDirectChallenge: () -> Unit,
    onAcceptInvitation: (String) -> Unit,
    onDeclineInvitation: (String) -> Unit,
    onCancelInvitation: (String) -> Unit,
    onRefresh: () -> Unit,
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

        // Guest Notice
        if (!state.isAuthenticated) {
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
                            text = "Authentication Required",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Friend Duel 1v1 challenges require both players to be signed into verified Zynpath accounts. Solo play remains 100% offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToSignIn,
                        colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign In to Challenge Friends", color = DeepIndigo, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        // Active Outgoing Invitation Pending Card
        state.activeOutgoingInvitation?.let { invitation ->
            Card(
                colors = CardDefaults.cardColors(containerColor = GunmetalCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, PathNeonCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = PathNeonCyan,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Challenge Sent!",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PathNeonCyan
                            )
                            Text(
                                text = "Waiting for ${invitation.recipientDisplayName} to accept...",
                                style = MaterialTheme.typography.bodySmall,
                                color = OffWhiteText
                            )
                        }
                        Text(
                            text = "${state.outgoingRemainingSeconds}s",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = AccentGold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onCancelInvitation(invitation.invitationId) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Challenge")
                    }
                }
            }
        }

        // Incoming Duel Invitations Card
        if (state.incomingInvitations.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, EmeraldGreen.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = EmeraldGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Incoming Duel Challenges (${state.incomingInvitations.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    state.incomingInvitations.forEach { inv ->
                        IncomingInvitationItem(
                            invitation = inv,
                            onAccept = { onAcceptInvitation(inv.invitationId) },
                            onDecline = { onDeclineInvitation(inv.invitationId) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Selection Tabs: Friends List vs Direct ID
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = CharcoalNavy,
            contentColor = PathNeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                    color = PathNeonCyan
                )
            }
        ) {
            Tab(
                selected = state.selectedTab == FriendDuelTab.FRIENDS_LIST,
                onClick = { onTabSelected(FriendDuelTab.FRIENDS_LIST) },
                text = { Text("Friends List (${state.acceptedFriends.size})", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = state.selectedTab == FriendDuelTab.DIRECT_ID,
                onClick = { onTabSelected(FriendDuelTab.DIRECT_ID) },
                text = { Text("Direct Zynpath ID", fontWeight = FontWeight.SemiBold) }
            )
        }

        when (state.selectedTab) {
            FriendDuelTab.FRIENDS_LIST -> {
                FriendsListTabContent(
                    friends = state.acceptedFriends,
                    isSending = state.isSendingInvitation,
                    isPending = state.isPendingInvitation,
                    onInvite = onInviteFriend,
                    onRefresh = onRefresh
                )
            }
            FriendDuelTab.DIRECT_ID -> {
                DirectIdTabContent(
                    targetId = state.friendTargetIdInput,
                    isSending = state.isSendingInvitation,
                    isPending = state.isPendingInvitation,
                    onTargetIdChanged = onTargetIdChanged,
                    onChallenge = onDirectChallenge
                )
            }
        }
    }
}

@Composable
private fun IncomingInvitationItem(
    invitation: FriendDuelInvitationDto,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
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
                    .background(EmeraldGreen.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldGreen)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invitation.inviterDisplayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Text(
                    text = "ID: ${invitation.inviterPublicZynpathId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedSlate
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Accept", color = DeepIndigo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onDecline,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedSlate),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Decline", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FriendsListTabContent(
    friends: List<FriendItem>,
    isSending: Boolean,
    isPending: Boolean,
    onInvite: (FriendItem) -> Unit,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose an accepted friend to challenge:",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate
            )
            IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = PathNeonCyan)
            }
        }

        if (friends.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Friends Added Yet",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OffWhiteText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Add friends from the Friends & Invites screen or challenge directly by entering their Public Zynpath ID.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSlate,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            friends.forEach { friend ->
                FriendItemCard(
                    friend = friend,
                    isSending = isSending,
                    isPending = isPending,
                    onInvite = { onInvite(friend) }
                )
            }
        }
    }
}

@Composable
private fun FriendItemCard(
    friend: FriendItem,
    isSending: Boolean,
    isPending: Boolean,
    onInvite: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(DeepIndigo, CircleShape)
                    .border(1.5.dp, PathNeonCyan.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PathNeonCyan)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.publicZynpathId,
                        style = MaterialTheme.typography.labelSmall,
                        color = PathNeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Presence badge
                    val presenceColor = when (friend.presenceState) {
                        PlayerPresenceState.ONLINE -> EmeraldGreen
                        PlayerPresenceState.AWAY -> WarningAmber
                        else -> Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(presenceColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = friend.presenceState.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = presenceColor
                    )
                }
            }
            Button(
                onClick = onInvite,
                enabled = !isSending && !isPending,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Duel", color = DeepIndigo, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DirectIdTabContent(
    targetId: String,
    isSending: Boolean,
    isPending: Boolean,
    onTargetIdChanged: (String) -> Unit,
    onChallenge: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Enter Friend's Public Zynpath ID",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your friend must already have an accepted friendship with you to participate.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSlate
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = targetId,
                onValueChange = onTargetIdChanged,
                label = { Text("e.g. ZYN-8492-3104") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onChallenge,
                enabled = targetId.isNotBlank() && !isSending && !isPending,
                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = DeepIndigo, modifier = Modifier.size(20.dp))
                } else {
                    Text("Send Duel Challenge", color = DeepIndigo, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FriendDuelLobbyContent(
    state: FriendDuelUiState,
    onMarkReady: () -> Unit,
    onLeaveMatch: () -> Unit
) {
    val session = state.currentSession ?: return
    val local = state.localParticipant
    val opponent = state.opponentParticipant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Friend Duel Lobby",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PathNeonCyan
                    )
                    Text(
                        text = "ID: ${session.matchId.take(8)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Both participants must confirm ready within 20 seconds. The backend will assign an identical verified puzzle.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OffWhiteText.copy(alpha = 0.8f)
                )
            }
        }

        // Versus Card
        Card(
            colors = CardDefaults.cardColors(containerColor = GunmetalCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Local Player
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(PathNeonCyan.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PathNeonCyan)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = local?.displayName ?: "You",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OffWhiteText
                            )
                            Text(
                                text = local?.publicZynpathId ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = PathNeonCyan
                            )
                        }
                    }
                    ReadyStatusBadge(isReady = local?.isReady == true)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MutedSlate.copy(alpha = 0.2f))
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Friend Opponent
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AccentGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AccentGold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = opponent?.displayName ?: "Friend",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OffWhiteText
                            )
                            Text(
                                text = opponent?.publicZynpathId ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGold
                            )
                        }
                    }
                    ReadyStatusBadge(isReady = opponent?.isReady == true)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Ready action
        if (local?.isReady != true) {
            Button(
                onClick = onMarkReady,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "I'm Ready!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = DeepIndigo
                )
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You are Ready! Waiting for friend confirmation...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onLeaveMatch,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Leave Lobby")
        }
    }
}

@Composable
private fun ReadyStatusBadge(isReady: Boolean) {
    if (isReady) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(EmeraldGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ready", color = EmeraldGreen, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    } else {
        Text("Waiting...", color = MutedSlate, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun FriendDuelCountdownContent(seconds: Int) {
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
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                color = MutedSlate
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (seconds > 0) "$seconds" else "GO!",
                fontSize = 80.sp,
                fontWeight = FontWeight.Black,
                color = if (seconds > 0) PathNeonCyan else EmeraldGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Continuous dual-win puzzle: Start at 1, visit all checkpoints, cover every cell!",
                style = MaterialTheme.typography.bodySmall,
                color = OffWhiteText.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
private fun FriendDuelActiveGameplayContent(
    state: FriendDuelUiState,
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dual Progress Bar Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Local Progress
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = local?.displayName ?: "You",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PathNeonCyan
                    )
                    LinearProgressIndicator(
                        progress = { (localCovered.toFloat() / totalCells).coerceIn(0f, 1f) },
                        color = PathNeonCyan,
                        trackColor = GunmetalCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = "$localCovered / $totalCells cells",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate
                    )
                }

                // Match Timer
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatElapsedTime(state.elapsedMatchTimeMs),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = AccentGold
                    )
                    Text(
                        text = "ELAPSED",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MutedSlate
                    )
                }

                // Friend Progress
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = opponent?.displayName ?: "Friend",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    LinearProgressIndicator(
                        progress = { (opponentCovered.toFloat() / totalCells).coerceIn(0f, 1f) },
                        color = AccentGold,
                        trackColor = GunmetalCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = "$opponentCovered / $totalCells cells",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSlate
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Puzzle Board
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            state.boardState?.let { boardState ->
                PuzzleBoard(
                    boardState = boardState,
                    isInputEnabled = state.isInputEnabled,
                    onCellEntered = onCellEntered
                )
            } ?: CircularProgressIndicator(color = PathNeonCyan)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Controls Bar: Undo, Reset, Forfeit (Hints strictly disabled!)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onUndo,
                enabled = state.canUndo,
                colors = ButtonDefaults.buttonColors(containerColor = GunmetalCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = OffWhiteText)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Undo", color = OffWhiteText)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onReset,
                enabled = state.canReset,
                colors = ButtonDefaults.buttonColors(containerColor = GunmetalCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = OffWhiteText)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset", color = OffWhiteText)
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(
                onClick = onForfeit,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Forfeit", color = Color(0xFFFF6B6B))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Reactions Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val reactions = listOf(
                Pair("THUMBS_UP", "👍"),
                Pair("FIRE", "🔥"),
                Pair("CLAP", "👏"),
                Pair("MINDBLOWN", "🤯"),
                Pair("SPEED", "⚡"),
                Pair("GG", "🤝")
            )
            reactions.forEach { (code, emoji) ->
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(CharcoalNavy, CircleShape)
                        .clickable { onSendReaction(code) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun FriendDuelValidatingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepIndigo.copy(alpha = 0.90f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = PathNeonCyan, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Validating Completion",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OffWhiteText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The backend is authoritatively validating your path, checkpoint sequence, and edge constraints against the server solver.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSlate,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FriendDuelResultContent(
    state: FriendDuelUiState,
    onRequestRematch: () -> Unit,
    onAcceptRematch: () -> Unit,
    onDeclineRematch: () -> Unit,
    onBackToFriends: () -> Unit,
    onBackToHub: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isWinner = state.isLocalWinner
    val localRes = state.localResult
    val opponentRes = state.opponentResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Outcome Banner
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isWinner) EmeraldGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (isWinner) EmeraldGreen else ErrorRed, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isWinner) "VICTORY!" else "DEFEAT",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isWinner) EmeraldGreen else ErrorRed,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isWinner) "You completed the verified dual-win puzzle first!" else "Your friend completed the dual-win puzzle first!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OffWhiteText,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Summary Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Match Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PathNeonCyan
                )
                Spacer(modifier = Modifier.height(12.dp))

                ResultRow(label = "Your Solve Time", value = localRes?.solveTimeMs?.let { formatElapsedTime(it) } ?: "DNF")
                ResultRow(label = "Friend's Solve Time", value = opponentRes?.solveTimeMs?.let { formatElapsedTime(it) } ?: "DNF")
                ResultRow(label = "Puzzle ID", value = state.puzzleDefinition?.puzzleId?.take(12) ?: "Unknown")
                ResultRow(
                    label = "Fingerprint",
                    value = state.puzzleDefinition?.fingerprint?.take(10)?.let { "$it..." } ?: "Verified"
                )
            }
        }

        // Interactive Rematch Card (Prompt 22 Sections 38-42)
        Card(
            colors = CardDefaults.cardColors(containerColor = GunmetalCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Rematch Experience",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
                Spacer(modifier = Modifier.height(8.dp))

                when {
                    state.isRematchRequestedByOpponent -> {
                        // Friend requested rematch
                        Text(
                            text = "${state.opponentParticipant?.displayName ?: "Friend"} requested a rematch! Do you accept?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OffWhiteText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onAcceptRematch,
                                enabled = !state.isRematchLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Accept Rematch", color = DeepIndigo, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onDeclineRematch,
                                enabled = !state.isRematchLoading,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Decline")
                            }
                        }
                    }
                    state.isRematchRequestedByMe -> {
                        // Local player requested rematch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                color = AccentGold,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Rematch requested! Waiting for friend to accept...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AccentGold
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "Play again on a fresh solver-verified puzzle. Previous result is securely saved to match history.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedSlate
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRequestRematch,
                            enabled = !state.isRematchLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Request Rematch", color = DeepIndigo, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Return Navigation Buttons
        Button(
            onClick = onBackToFriends,
            colors = ButtonDefaults.buttonColors(containerColor = CharcoalNavy),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Friends List", color = OffWhiteText)
        }

        OutlinedButton(
            onClick = onBackToHub,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedSlate),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Return to Multiplayer Hub")
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MutedSlate)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = OffWhiteText,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun ReactionBadge(popup: ReactionPopup) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CharcoalNavy),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(1.5.dp, PathNeonCyan, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = popup.emojiChar, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = popup.labelText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = OffWhiteText
            )
        }
    }
}

private fun formatElapsedTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000L
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (timeMs % 1000L) / 100
    return String.format("%02d:%02d.%d", minutes, seconds, tenths)
}
