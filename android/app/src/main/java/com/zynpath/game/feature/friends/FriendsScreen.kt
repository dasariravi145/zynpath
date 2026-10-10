package com.zynpath.game.feature.friends

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ErrorRed
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.social.model.FriendItem
import com.zynpath.game.core.social.model.FriendRelationshipStatus
import com.zynpath.game.core.social.model.FriendRequestItem
import com.zynpath.game.core.social.model.InvitationLinkHelper
import com.zynpath.game.core.social.model.PlayerPresenceState
import com.zynpath.game.core.social.model.PublicPlayerProfile
import com.zynpath.game.core.social.model.SentFriendRequestItem

/**
 * Interactive Friends & Social Screen.
 *
 * Implements Prompt 19 Sections 5-27, 29-35:
 * - Public player ID display and copy action
 * - Shareable invitation link via Android Sharesheet
 * - Exact-ID player search with privacy preservation
 * - Real friend list with authoritative presence badges
 * - Incoming and outgoing request queues
 * - Guest mode explanation without fake data
 */
@Composable
fun FriendsScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    initialInvitationId: String? = null,
    onNavigateToFriendDuel: (String) -> Unit = {},
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle-aware presence tracking (Foreground -> ONLINE, Background -> AWAY)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onLifecycleStateChanged(true)
                Lifecycle.Event.ON_PAUSE -> viewModel.onLifecycleStateChanged(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle deep link invitation parameter if passed
    LaunchedEffect(initialInvitationId) {
        if (!initialInvitationId.isNullOrBlank()) {
            viewModel.handleIncomingInvitation(initialInvitationId)
        }
    }

    // Display snackbar messages
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Confirmation dialog states
    var friendToRemove by remember { mutableStateOf<FriendItem?>(null) }
    var friendToBlock by remember { mutableStateOf<FriendItem?>(null) }
    var selectedTopTab by remember { mutableStateOf(0) } // 0: FRIENDS, 1: INVITATIONS

    // Auto-prompt compact Google Connect modal over screen when guest enters
    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) {
            viewModel.openGoogleConnectModal()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = com.zynpath.game.core.designsystem.theme.RefNavyDark,
            topBar = {
                com.zynpath.game.core.designsystem.components.ZynpathMasterHeaderBar(
                    title = "FRIENDS",
                    onBackClick = onBackClick
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Provider-neutral Segmented Tabs: "FRIENDS" / "INVITATIONS"
                com.zynpath.game.core.designsystem.components.ZynpathMasterSegmentedTabs(
                    tabs = listOf("FRIENDS", "INVITATIONS"),
                    selectedIndex = selectedTopTab,
                    onTabSelected = { selectedTopTab = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )

                if (selectedTopTab == 0) {
                    // TAB 0: Real Friends List / Clean Empty State
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Unconnected Guest Banner (if modal was dismissed)
                        if (!uiState.isAuthenticated) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = com.zynpath.game.core.designsystem.theme.RefNavySurface),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, com.zynpath.game.core.designsystem.theme.RefNavyBorder)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF0F224D))
                                                    .border(
                                                        androidx.compose.foundation.BorderStroke(1.dp, PathCyanGlow.copy(alpha = 0.5f)),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                androidx.compose.foundation.Image(
                                                    painter = androidx.compose.ui.res.painterResource(id = com.zynpath.game.R.drawable.ic_brand_google),
                                                    contentDescription = "Google",
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(14.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "CONNECT WITH GOOGLE",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "Play with friends and keep your online progress connected.",
                                                    fontSize = 12.sp,
                                                    color = com.zynpath.game.core.designsystem.theme.RefTextMuted
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                                            text = "Connect Google",
                                            style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.BLUE,
                                            height = 42.dp,
                                            fontSize = 14,
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = { viewModel.openGoogleConnectModal() }
                                        )
                                    }
                                }
                            }
                        }

                        // Connected Friends (if any exist in repository)
                        if (uiState.friends.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Connected Friends (${uiState.friends.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8E9BB0),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }

                            items(uiState.friends, key = { it.playerId }) { friend ->
                                FriendCard(
                                    friend = friend,
                                    onDuel = { onNavigateToFriendDuel(friend.publicZynpathId) },
                                    onRemove = { friendToRemove = friend },
                                    onBlock = { friendToBlock = friend }
                                )
                            }
                        } else {
                            // Section 23: Clean Empty State (no fake suggested friends)
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp, horizontal = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x2200E5FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = PathCyanGlow,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "NO FRIENDS YET",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Invite a friend to play Zynpath.",
                                        fontSize = 13.sp,
                                        color = com.zynpath.game.core.designsystem.theme.RefTextMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                                        text = "INVITE FRIEND",
                                        style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.GOLD,
                                        height = 42.dp,
                                        fontSize = 14,
                                        modifier = Modifier.width(180.dp),
                                        onClick = {
                                            val publicId = uiState.myPublicId ?: "ZYN-PLAYER"
                                            val displayName = uiState.myDisplayName ?: "Zynpath Player"
                                            val shareIntent = InvitationLinkHelper.createShareIntent(context, publicId, displayName)
                                            context.startActivity(shareIntent)
                                        }
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    // TAB 1: Invitations & Exact Search
                    if (!uiState.isAuthenticated) {
                        GuestConnectBanner(onConnectGoogle = { viewModel.openGoogleConnectModal() })
                    } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            MyIdentityCard(
                                displayName = uiState.myDisplayName ?: "Player",
                                publicId = uiState.myPublicId ?: "ZYN-UNKNOWN",
                                onCopyId = {
                                    uiState.myPublicId?.let { id ->
                                        clipboardManager.setText(AnnotatedString(id))
                                        Toast.makeText(context, "Public ID copied: $id", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onShareInvite = {
                                    uiState.myPublicId?.let { id ->
                                        val intent = InvitationLinkHelper.createShareIntent(
                                            context, id, uiState.myDisplayName ?: "Zynpath Player"
                                        )
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }

                        item {
                            // Sub-tabs for Requests & Find Player
                            val subTabs = listOf(
                                "Requests (${uiState.incomingRequests.size + uiState.outgoingRequests.size})" to FriendsTab.REQUESTS,
                                "Find Player" to FriendsTab.FIND_PLAYER
                            )
                            com.zynpath.game.core.designsystem.components.ZynpathMasterSegmentedTabs(
                                tabs = subTabs.map { it.first },
                                selectedIndex = if (uiState.selectedTab == FriendsTab.FIND_PLAYER) 1 else 0,
                                onTabSelected = { idx ->
                                    viewModel.selectTab(subTabs[idx].second)
                                }
                            )
                        }

                        if (uiState.selectedTab == FriendsTab.FIND_PLAYER) {
                            item {
                                FindPlayerTab(
                                    query = uiState.searchQuery,
                                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                    onSearch = { viewModel.searchPlayer() },
                                    isSearching = uiState.isSearching,
                                    searchResult = uiState.searchResult,
                                    errorMessage = uiState.searchError,
                                    onSendRequest = { viewModel.sendFriendRequest(it) }
                                )
                            }
                        } else {
                            item {
                                RequestsTab(
                                    incoming = uiState.incomingRequests,
                                    outgoing = uiState.outgoingRequests,
                                    onAccept = { viewModel.acceptRequest(it.requestId) },
                                    onReject = { viewModel.rejectRequest(it.requestId) },
                                    onCancel = { viewModel.cancelRequest(it.requestId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Remove Friend Confirmation Dialog
    friendToRemove?.let { friend ->
        AlertDialog(
            onDismissRequest = { friendToRemove = null },
            containerColor = BackgroundElevated,
            title = { Text("Remove Friend", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to remove ${friend.displayName} (${friend.publicZynpathId}) from your friends list? You can send a new friend request anytime.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeFriend(friend.playerId)
                        friendToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Remove", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { friendToRemove = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Block Player Confirmation Dialog
    friendToBlock?.let { friend ->
        AlertDialog(
            onDismissRequest = { friendToBlock = null },
            containerColor = BackgroundElevated,
            title = { Text("Block Player", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to block ${friend.displayName} (${friend.publicZynpathId})? They will no longer be able to send you friend requests or multiplayer invites.",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.blockPlayer(friend.playerId)
                        friendToBlock = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Block", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { friendToBlock = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    if (uiState.showGoogleConnectModal) {
            com.zynpath.game.core.designsystem.components.GoogleConnectModal(
                onDismiss = { viewModel.dismissGoogleConnectModal() },
                onContinueWithGoogle = { viewModel.connectWithGoogle(context) },
                isLoading = uiState.isGoogleConnecting,
                errorMessage = uiState.googleConnectError,
                onRetry = { viewModel.connectWithGoogle(context) }
            )
        }
    }
}

@Composable
private fun GuestConnectBanner(onConnectGoogle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F224D))
                .border(androidx.compose.foundation.BorderStroke(1.dp, PathCyanGlow.copy(alpha = 0.5f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.zynpath.game.R.drawable.ic_brand_google),
                contentDescription = "Google",
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "CONNECT WITH GOOGLE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Play with friends and keep your\nonline progress connected.",
            fontSize = 13.sp,
            color = com.zynpath.game.core.designsystem.theme.RefTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
            text = "Connect Google",
            style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.BLUE,
            height = 46.dp,
            fontSize = 15,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onClick = onConnectGoogle
        )
    }
}

@Composable
private fun MyIdentityCard(
    displayName: String,
    publicId: String,
    onCopyId: () -> Unit,
    onShareInvite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ForestMint.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = ForestMint,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Public ID: $publicId",
                        fontSize = 13.sp,
                        color = PathCyanGlow,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyId,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy ID", fontSize = 13.sp)
                }

                Button(
                    onClick = onShareInvite,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestMint),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = BackgroundDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Invite", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BackgroundDark)
                }
            }
        }
    }
}

@Composable
private fun FriendsListTab(
    friends: List<FriendItem>,
    onDuel: (String) -> Unit,
    onRemoveFriend: (FriendItem) -> Unit,
    onBlockPlayer: (FriendItem) -> Unit
) {
    if (friends.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Friends Added Yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Share your Public Zynpath ID or search for friends using the 'Find Player' tab.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(friends, key = { it.playerId }) { friend ->
                FriendCard(
                    friend = friend,
                    onDuel = { onDuel(friend.publicZynpathId) },
                    onRemove = { onRemoveFriend(friend) },
                    onBlock = { onBlockPlayer(friend) }
                )
            }
        }
    }
}

@Composable
private fun FriendCard(
    friend: FriendItem,
    onDuel: () -> Unit,
    onRemove: () -> Unit,
    onBlock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PathCyanGlow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.displayName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PresenceIndicator(presence = friend.presenceState)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = friend.publicZynpathId,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            // Quick actions: Duel button & Remove
            Button(
                onClick = onDuel,
                colors = ButtonDefaults.buttonColors(containerColor = ForestMint),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Duel", color = BackgroundDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = "Remove Friend",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PresenceIndicator(presence: PlayerPresenceState) {
    val (color, label) = when (presence) {
        PlayerPresenceState.ONLINE -> ForestMint to "Online"
        PlayerPresenceState.AWAY -> AccentGold to "Away"
        PlayerPresenceState.OFFLINE -> TextMuted to "Offline"
        PlayerPresenceState.UNKNOWN -> TextMuted to "Unknown"
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RequestsTab(
    incoming: List<FriendRequestItem>,
    outgoing: List<SentFriendRequestItem>,
    onAccept: (FriendRequestItem) -> Unit,
    onReject: (FriendRequestItem) -> Unit,
    onCancel: (SentFriendRequestItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Incoming Requests (${incoming.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (incoming.isEmpty()) {
            item {
                Text(
                    text = "No pending incoming friend requests.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(incoming, key = { it.requestId }) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.senderDisplayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = req.senderPublicZynpathId,
                                fontSize = 12.sp,
                                color = PathCyanGlow
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { onAccept(req) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ForestMint.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Accept",
                                    tint = ForestMint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { onReject(req) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Reject",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sent Requests (${outgoing.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (outgoing.isEmpty()) {
            item {
                Text(
                    text = "No pending outgoing friend requests.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(outgoing, key = { it.requestId }) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.recipientDisplayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = req.recipientPublicZynpathId,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        TextButton(onClick = { onCancel(req) }) {
                            Text("Cancel", fontSize = 12.sp, color = ErrorRed)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FindPlayerTab(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isSearching: Boolean,
    searchResult: PublicPlayerProfile?,
    errorMessage: String?,
    onSendRequest: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter Public Zynpath ID (e.g. ZYN-1234-5678)", color = TextMuted, fontSize = 13.sp) },
            trailingIcon = {
                IconButton(onClick = onSearch, enabled = !isSearching) {
                    if (isSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ForestMint, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ForestMint)
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestMint,
                unfocusedBorderColor = TextMuted.copy(alpha = 0.4f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = ForestMint
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = error, fontSize = 13.sp, color = ErrorRed)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        searchResult?.let { profile ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BackgroundElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ForestMint,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = profile.publicZynpathId,
                                fontSize = 13.sp,
                                color = PathCyanGlow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    when (profile.relationshipStatus) {
                        FriendRelationshipStatus.NONE -> {
                            Button(
                                onClick = { onSendRequest(profile.publicZynpathId) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestMint),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = BackgroundDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send Friend Request", color = BackgroundDark, fontWeight = FontWeight.Bold)
                            }
                        }
                        FriendRelationshipStatus.FRIENDS -> {
                            Text(
                                text = "✓ You are already friends",
                                fontSize = 13.sp,
                                color = ForestMint,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        FriendRelationshipStatus.OUTGOING_REQUEST -> {
                            Text(
                                text = "⌛ Friend request already sent (Pending)",
                                fontSize = 13.sp,
                                color = AccentGold,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        FriendRelationshipStatus.INCOMING_REQUEST -> {
                            Text(
                                text = "📬 This player sent you a request! Check the Requests tab.",
                                fontSize = 13.sp,
                                color = AccentPurple,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        FriendRelationshipStatus.SELF -> {
                            Text(
                                text = "👤 This is your own Public Zynpath ID",
                                fontSize = 13.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        FriendRelationshipStatus.BLOCKED -> {
                            Text(
                                text = "🚫 This player is currently blocked",
                                fontSize = 13.sp,
                                color = ErrorRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
