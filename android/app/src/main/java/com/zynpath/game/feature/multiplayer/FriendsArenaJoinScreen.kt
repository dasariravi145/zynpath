package com.zynpath.game.feature.multiplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * Premium Friends Arena Join Room Screen (Prompt 16/24).
 *
 * Implements:
 * 1. Deep navy background with celestial particles and number-path decoration.
 * 2. Royal-blue entry panel with electric-cyan accents.
 * 3. Large, accessible uppercase room-code input field with tactile focus glow.
 * 4. Live format validation and helper messages.
 * 5. Gold JOIN ROOM action with debounce and loading indicator.
 * 6. Game-style error panel for handling room not found, room full, invalid code, or service pending.
 * 7. Guest player support: Preserves pending invitation while displaying sign-in CTA.
 * 8. Back navigation support.
 */
@Composable
fun FriendsArenaJoinScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onJoinSuccess: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialCode: String? = null,
    viewModel: FriendsArenaJoinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Receive initial room code from deep links / external intent payloads (Task 4)
    LaunchedEffect(initialCode) {
        if (!initialCode.isNullOrBlank()) {
            viewModel.setInitialInvitation(code = initialCode, source = "deep_link")
        }
    }

    // Alert Notice Dialog
    if (uiState.userNoticeTitle != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearNotice() },
            title = {
                Text(
                    text = uiState.userNoticeTitle ?: "Notice",
                    style = GameTypography.screenHeading.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                )
            },
            text = {
                Text(
                    text = uiState.userNoticeDetails ?: "",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = GameSecondaryText,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearNotice() }) {
                    Text(
                        text = "UNDERSTOOD",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        color = GameGoldHighlight
                    )
                }
            },
            containerColor = GameMidnightBlue,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        )
    }

    GameScreenBackground(
        showCelestialParticles = true,
        isReducedMotion = uiState.isReducedMotion,
        applyStatusBarPadding = false,
        applyNavigationBarPadding = false
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            // Screen Top Bar
            GameTopBar(
                title = "JOIN WITH CODE",
                subtitle = "Enter 6-Character Room Code",
                onBackClick = onBackClick
            )

            // Scrollable Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header Banner
                JoinRoomHeroHeader(isReducedMotion = uiState.isReducedMotion)

                // 2. Original Number-Path Graphic
                JoinRoomNumberPathIllustration(isReducedMotion = uiState.isReducedMotion)

                // 3. Room Code Input Panel
                RoomCodeEntryPanel(
                    uiState = uiState,
                    onCodeChange = { viewModel.onRoomCodeInputChange(it) },
                    onClearCode = { viewModel.onRoomCodeInputChange("") },
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.canSubmit) {
                            viewModel.onJoinRoom(onSuccess = onJoinSuccess)
                        }
                    }
                )

                // 4. Pending Invitation Notice (if received from link)
                if (uiState.hasPendingInvitation) {
                    PendingInvitationBanner(
                        invitation = uiState.pendingInvitation!!
                    )
                }

                // 5. Game-Style Error Panel
                if (uiState.joinError != null) {
                    JoinErrorPanel(
                        error = uiState.joinError!!,
                        onDismiss = { viewModel.clearError() }
                    )
                }

                // 6. Guest Account Notice (Task 6)
                if (uiState.isGuest) {
                    JoinGuestAccountCard(
                        onNavigateToSignIn = onNavigateToSignIn
                    )
                }

                // 7. Primary Action Button
                JoinRoomActionSection(
                    uiState = uiState,
                    onJoinRoom = {
                        focusManager.clearFocus()
                        viewModel.onJoinRoom(onSuccess = onJoinSuccess)
                    },
                    onNavigateToSignIn = onNavigateToSignIn
                )

                // System Navigation Spacing
                Spacer(modifier = Modifier.navigationBarsPadding())
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ============================================================================
// UI SUBCOMPONENTS
// ============================================================================

@Composable
private fun JoinRoomHeroHeader(
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "JoinGlowPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )
    val activeGlow = if (isReducedMotion) 0.7f else pulse

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x3300E5FF))
                .border(1.dp, GameElectricCyan.copy(alpha = activeGlow), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Pin,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DIRECT ROOM CODE ENTRY",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = GameElectricCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ENTER ROOM CODE",
            style = GameTypography.screenHeading.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = GameWhite
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Connect to your friend's 1–5 player Friends Arena lobby",
            style = GameTypography.bodyMedium.copy(
                fontSize = 13.sp,
                color = GameSecondaryText
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Lightweight Compose graphics: Number-path circuit illustration (Task 2).
 */
@Composable
private fun JoinRoomNumberPathIllustration(
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "JoinPathPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnim"
    )
    val activePulse = if (isReducedMotion) 0.75f else pulse

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val startX = w * 0.15f
            val endX = w * 0.85f
            val midY = h * 0.50f

            val circuitPath = Path().apply {
                moveTo(startX, midY)
                cubicTo(
                    w * 0.35f, midY - 20f,
                    w * 0.65f, midY + 20f,
                    endX, midY
                )
            }

            // Glow path
            drawPath(
                path = circuitPath,
                color = GameElectricCyan.copy(alpha = 0.25f * activePulse),
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Dashed active path
            drawPath(
                path = circuitPath,
                color = GameElectricCyan.copy(alpha = 0.85f * activePulse),
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )

            // Center beacon node
            drawCircle(
                color = GameGoldHighlight.copy(alpha = 0.25f * activePulse),
                radius = 16.dp.toPx(),
                center = Offset(w * 0.5f, midY)
            )
            drawCircle(
                color = GameGoldHighlight,
                radius = 4.dp.toPx(),
                center = Offset(w * 0.5f, midY)
            )
        }
    }
}

/**
 * Room code entry input box with focus glow and clear validation feedback.
 */
@Composable
private fun RoomCodeEntryPanel(
    uiState: FriendsArenaJoinUiState,
    onCodeChange: (String) -> Unit,
    onClearCode: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ROOM CODE INPUT",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = GameElectricCyan
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33102454))
                        .border(1.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${uiState.cleanedCode.length} / 6",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (uiState.isExactStandardLength) GameSuccessGreen else GameGoldHighlight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large, accessible input container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF07142D))
                    .border(
                        width = if (isFocused) 2.dp else 1.2.dp,
                        color = when {
                            isFocused -> GameElectricCyan
                            uiState.isExactStandardLength -> GameSuccessGreen.copy(alpha = 0.8f)
                            else -> GameRoyalBlue.copy(alpha = 0.7f)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Input TextField
                    BasicTextField(
                        value = uiState.roomCodeInput,
                        onValueChange = onCodeChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        interactionSource = interactionSource,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            letterSpacing = 4.sp,
                            color = GameWhite
                        ),
                        cursorBrush = SolidColor(GameElectricCyan),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onDone() }
                        ),
                        decorationBox = { innerTextField ->
                            if (uiState.roomCodeInput.isEmpty()) {
                                Text(
                                    text = "E.G. ARC942",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    letterSpacing = 3.sp,
                                    color = GameSecondaryText.copy(alpha = 0.35f)
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Clear button
                    if (uiState.roomCodeInput.isNotEmpty()) {
                        IconButton(
                            onClick = onClearCode,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Code",
                                tint = GameSecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Validation Status Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (statusIcon, statusColor, statusText) = when {
                    uiState.cleanedCode.isEmpty() -> Triple(
                        Icons.Default.Info,
                        GameSecondaryText,
                        "Ask the room host for their 6-character room code."
                    )
                    uiState.isExactStandardLength -> Triple(
                        Icons.Default.Check,
                        GameSuccessGreen,
                        "Valid 6-character room code format."
                    )
                    uiState.cleanedCode.length < 6 -> Triple(
                        Icons.Default.Pin,
                        GameGoldHighlight,
                        "${6 - uiState.cleanedCode.length} more character(s) needed."
                    )
                    else -> Triple(
                        Icons.Default.Check,
                        GameElectricCyan,
                        "Valid code format."
                    )
                }

                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusText,
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    color = statusColor
                )
            }
        }
    }
}

/**
 * Notice banner when an invitation link was parsed.
 */
@Composable
private fun PendingInvitationBanner(
    invitation: FriendsArenaInvitationPayload,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x2200E5FF))
            .border(1.dp, GameElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = GameElectricCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "INVITATION DETECTED",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GameElectricCyan
                )
                Text(
                    text = "Room Code ${invitation.roomCode} loaded from invitation link.",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    color = GameSecondaryText
                )
            }
        }
    }
}

/**
 * Game-style Error Panel (Task 3 & Task 9).
 */
@Composable
private fun JoinErrorPanel(
    error: FriendsArenaJoinError,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPendingService = error.type == FriendsArenaJoinErrorType.SERVICE_PENDING

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPendingService) Color(0x331E2A4A) else Color(0x333A1010))
            .border(
                1.dp,
                if (isPendingService) Color(0xFFF59E0B).copy(alpha = 0.7f) else Color(0xFFFF4757).copy(alpha = 0.7f),
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (isPendingService) Icons.Default.Info else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isPendingService) Color(0xFFFCD34D) else Color(0xFFFF6B6B),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (error.type) {
                        FriendsArenaJoinErrorType.ROOM_NOT_FOUND -> "ROOM NOT FOUND"
                        FriendsArenaJoinErrorType.ROOM_FULL -> "ROOM IS FULL (5/5 PLAYERS)"
                        FriendsArenaJoinErrorType.ROOM_CLOSED -> "ROOM CLOSED"
                        FriendsArenaJoinErrorType.INVALID_CODE -> "INVALID ROOM CODE"
                        FriendsArenaJoinErrorType.UNAUTHORIZED -> "SIGN IN REQUIRED"
                        FriendsArenaJoinErrorType.CONNECTION_FAILURE -> "CONNECTION FAILED"
                        FriendsArenaJoinErrorType.ALREADY_JOINED -> "ALREADY IN ROOM"
                        FriendsArenaJoinErrorType.SERVICE_PENDING -> "ROOM SERVICE PENDING"
                    },
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = if (isPendingService) Color(0xFFFCD34D) else Color(0xFFFF6B6B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = error.message,
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = GameWhite
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Dismiss Error",
                    tint = GameSecondaryText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Guest Account warning card with sign-in prompt (Task 6).
 */
@Composable
private fun JoinGuestAccountCard(
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACCOUNT REQUIRED TO JOIN",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFFFCD34D)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Online multiplayer rooms require an authenticated Zynpath account. Linking your account preserves all completed levels and stats.",
                fontFamily = FontFamily.Default,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = GameSecondaryText
            )

            Spacer(modifier = Modifier.height(10.dp))

            GamePrimaryButton(
                text = "SIGN IN / LINK ACCOUNT",
                onClick = onNavigateToSignIn,
                height = 42.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Primary action section with Join Button and loading state.
 */
@Composable
private fun JoinRoomActionSection(
    uiState: FriendsArenaJoinUiState,
    onJoinRoom: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (uiState.isGuest) {
            GamePrimaryButton(
                text = "SIGN IN TO JOIN ROOM",
                onClick = onNavigateToSignIn,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            GamePrimaryButton(
                text = if (uiState.isJoining) "CONNECTING TO ROOM..." else "JOIN ARENA ROOM",
                onClick = onJoinRoom,
                enabled = uiState.canSubmit,
                height = 50.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = "Maximum 5 players per room • Match requires 2 to 5 connected players",
            fontFamily = FontFamily.Default,
            fontSize = 11.sp,
            color = GameSecondaryText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
