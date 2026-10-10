package com.zynpath.game.feature.multiplayer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.GoogleConnectModal

/**
 * Compact Friends Arena Modal Host (Replaces full-screen Create Game Room page).
 *
 * Implements:
 * 1. Immediate Google Authentication Gate.
 * 2. Connected player -> opens compact CreateRoomModal.
 * 3. Guest player -> opens compact GoogleConnectModal.
 * 4. Successful login -> automatically opens CreateRoomModal.
 * 5. Dismiss / Cancel -> exits cleanly via onBackClick without opening a room.
 * 6. Zero Facebook authentication, zero empty space, zero separate full-screen pages.
 */
@Composable
fun FriendsArenaEntryScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToCreateRoom: (String?) -> Unit = {},
    onNavigateToJoinWithCode: () -> Unit = {},
    onNavigateToFacebookFriends: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FriendsArenaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Trigger initial auth gate upon entering
    LaunchedEffect(Unit) {
        viewModel.onFriendsArenaTapped()
    }

    BackHandler {
        onBackClick()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E))
    ) {
        // 1. Google Connect Modal (Shown for unauthenticated guests)
        if (uiState.showGoogleConnectModal) {
            GoogleConnectModal(
                onDismiss = {
                    viewModel.dismissGoogleConnectModal()
                    onBackClick()
                },
                onContinueWithGoogle = { viewModel.connectWithGoogle(context) },
                isLoading = uiState.isGoogleConnecting,
                errorMessage = uiState.googleConnectError,
                onRetry = { viewModel.connectWithGoogle(context) }
            )
        }

        // 2. Compact Create Room Modal
        if (uiState.showCreateRoomModal) {
            CreateRoomModal(
                onDismiss = {
                    viewModel.dismissCreateRoomModal()
                    onBackClick()
                },
                selectedPlayerCount = uiState.selectedPlayerCount,
                onPlayerCountSelected = { viewModel.onPlayerCountChanged(it) },
                invitedFriendsCount = uiState.selectedFriendIds.size,
                onInviteFriendsClick = { viewModel.openFriendPicker() },
                onCreateRoom = {
                    viewModel.createRoom { roomId ->
                        onNavigateToCreateRoom(roomId)
                    }
                },
                onJoinRoomClick = { viewModel.openJoinRoomModal() },
                isCreating = uiState.isCreatingRoom,
                errorMessage = uiState.createRoomError,
                puzzleDescription = uiState.selectedPuzzleDescription
            )
        }

        // 3. Compact Friend Picker Modal
        if (uiState.showFriendPickerModal) {
            FriendPickerModal(
                friends = uiState.eligibleFriends,
                selectedFriendIds = uiState.selectedFriendIds,
                maxSelection = (uiState.selectedPlayerCount - 1).coerceAtLeast(0),
                onToggleFriend = { viewModel.toggleFriendSelected(it) },
                onDismiss = { viewModel.dismissFriendPicker() }
            )
        }

        // 4. Compact Join Room Modal
        if (uiState.showJoinRoomModal) {
            JoinRoomModal(
                roomCode = uiState.joinRoomCode,
                onRoomCodeChange = { viewModel.onJoinRoomCodeChanged(it) },
                onJoinClick = {
                    viewModel.joinRoom(uiState.joinRoomCode) { roomId ->
                        onNavigateToCreateRoom(roomId)
                    }
                },
                onDismiss = {
                    viewModel.dismissJoinRoomModal()
                    viewModel.openCreateRoomModal()
                },
                isJoining = uiState.isJoiningRoom,
                errorMessage = uiState.joinRoomError
            )
        }
    }
}
