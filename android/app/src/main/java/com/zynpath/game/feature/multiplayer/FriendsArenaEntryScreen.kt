package com.zynpath.game.feature.multiplayer

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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameOrangeAccent
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.home.components.resolveAvatarDrawable

/**
 * Premium Friends Arena Entry & Eligibility Screen (Prompt 14/24).
 *
 * Implements:
 * 1. Dedicated arena visual styling: Deep navy fantasy background, royal-blue panels,
 *    electric-cyan glowing circuit number paths, and gold highlights.
 * 2. Original five-player arena illustration using lightweight Compose graphics:
 *    Host (Slot 1) + 4 player positions (Slots 2–5). These are illustrative empty slots,
 *    not simulated connected players.
 * 3. Authoritative rules presentation:
 *    - Total capacity: 1–5 players (including host).
 *    - One player may create and wait inside a room.
 *    - Multiplayer match requires at least 2 connected players.
 *    - Maximum match size is 5 players.
 * 4. Authentication & Facebook Eligibility (Conditions A, B, C, D):
 *    - Guest: Account required for online multiplayer, sign-in CTA, progress preservation note.
 *    - Google-authenticated without Facebook: Explain Facebook is needed specifically for Facebook friend discovery.
 *      Google Login is NOT treated as Facebook friend authorization.
 *    - Facebook-connected: Display actual connected state and verified status.
 *    - Missing Facebook configuration: Honest Setup Pending or unavailable state.
 * 5. Facebook Friend Discovery Privacy notice adhering to Meta's platform restrictions.
 *    Does not simulate friends, fake online statuses, or mock profile photos.
 * 6. Arena Entry Action cards: CREATE ROOM, JOIN WITH CODE, FACEBOOK FRIENDS with honest
 *    upcoming/disabled states for routes scheduled in later prompts.
 */
@Composable
fun FriendsArenaEntryScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToCreateRoom: () -> Unit = {},
    onNavigateToJoinWithCode: () -> Unit = {},
    onNavigateToFacebookFriends: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: FriendsArenaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Alert Notice Dialog
    if (uiState.userNotice != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearUserNotice() },
            title = {
                Text(
                    text = "Friends Arena Notice",
                    style = GameTypography.screenHeading.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameWhite
                    )
                )
            },
            text = {
                Text(
                    text = uiState.userNotice ?: "",
                    style = GameTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = GameSecondaryText,
                        lineHeight = 20.sp
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearUserNotice() }) {
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

    var selectedPlayers by remember { mutableStateOf(3) }
    var selectedLevelOption by remember { mutableStateOf("Random Level") }
    var selectedModeOption by remember { mutableStateOf("Standard") }

    androidx.compose.material3.Scaffold(
        containerColor = com.zynpath.game.core.designsystem.theme.RefNavyDark,
        topBar = {
            com.zynpath.game.core.designsystem.components.ZynpathMasterHeaderBar(
                title = "Create Game Room",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Player Count Selector
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Select number of players",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "(Max 5)",
                        fontSize = 12.sp,
                        color = Color(0xFF8E9BB0)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    (1..5).forEach { count ->
                        val isSelected = count == selectedPlayers
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) com.zynpath.game.core.designsystem.theme.RefCyanNeon
                                    else com.zynpath.game.core.designsystem.theme.RefNavySurface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) com.zynpath.game.core.designsystem.theme.RefCyanNeon
                                    else com.zynpath.game.core.designsystem.theme.RefNavyBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedPlayers = count },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) com.zynpath.game.core.designsystem.theme.RefNavyDark else Color.White
                            )
                        }
                    }
                }
            }

            // 2. Add from Facebook Card (Square + icon on left, text on right)
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToFacebookFriends() },
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = com.zynpath.game.core.designsystem.theme.RefNavySurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.zynpath.game.core.designsystem.theme.RefNavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF032564),
                                        Color(0xFF02449B)
                                    )
                                )
                            )
                            .border(1.dp, com.zynpath.game.core.designsystem.theme.RefCyanNeon.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add from Facebook",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add from Facebook",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Select your Facebook friends",
                            fontSize = 12.sp,
                            color = com.zynpath.game.core.designsystem.theme.RefTextMuted
                        )
                    }
                }
            }

            // 3. Game Settings List
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = com.zynpath.game.core.designsystem.theme.RefNavySurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.zynpath.game.core.designsystem.theme.RefNavyBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Level Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedLevelOption = when (selectedLevelOption) {
                                    "Random Level" -> "Level 1"
                                    "Level 1" -> "Level 5"
                                    "Level 5" -> "Level 10"
                                    else -> "Random Level"
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Level Selection",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$selectedLevelOption >",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.zynpath.game.core.designsystem.theme.RefCyanNeon
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(com.zynpath.game.core.designsystem.theme.RefNavyBorder)
                    )

                    // Game Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedModeOption = when (selectedModeOption) {
                                    "Standard" -> "Time Attack"
                                    "Time Attack" -> "Speed Rush"
                                    else -> "Standard"
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Game Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$selectedModeOption >",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.zynpath.game.core.designsystem.theme.RefCyanNeon
                        )
                    }
                }
            }

            // Quick Join Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToJoinWithCode() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Have a Room Code? ",
                    fontSize = 13.sp,
                    color = com.zynpath.game.core.designsystem.theme.RefTextMuted
                )
                Text(
                    text = "Join Room >",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = com.zynpath.game.core.designsystem.theme.RefCyanNeon
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Primary Action Button: "Create Room" (Gold Pill)
            com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                text = "Create Room",
                style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.GOLD,
                height = 52.dp,
                fontSize = 18,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                onClick = onNavigateToCreateRoom
            )
        }
    }
}

/**
 * Hero Header highlighting Friends Arena identity, capacity, and glowing accents.
 */
@Composable
private fun ArenaHeroHeader(
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HeroGlowPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulseAnim"
    )
    val activeGlowAlpha = if (isReducedMotion) 0.7f else glowAlpha

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mode Category Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x3300E5FF))
                .border(1.dp, GameElectricCyan.copy(alpha = activeGlowAlpha), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MULTIPLAYER EXPEDITION",
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
            text = "FRIENDS ARENA",
            style = GameTypography.screenHeading.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = GameWhite
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Play together with up to 5 players",
            style = GameTypography.bodyMedium.copy(
                fontSize = 14.sp,
                color = GameSecondaryText
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Original Five-Player Arena Illustration using lightweight Compose graphics.
 * Displays five player positions:
 * 1. Host (Slot 1)
 * 2. Player 2
 * 3. Player 3
 * 4. Player 4
 * 5. Player 5
 *
 * Connected by glowing electric-cyan number paths.
 * These are illustrative empty slots, NOT simulated connected players.
 */
@Composable
private fun ArenaFiveSlotIllustrationPanel(
    uiState: FriendsArenaUiState,
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArenaCircuitPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CircuitPulseAnim"
    )
    val activePulse = if (isReducedMotion) 0.8f else pulse

    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Slots Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ARENA ROSTER SLOTS",
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
                        .border(1.dp, GameRoyalBlue.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "1–5 PLAYERS",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        color = GameGoldHighlight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Procedural Canvas with Glowing Cyan Circuit Pathways
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Circuit Canvas connecting the 5 pedestals
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Five slot horizontal centers
                    val slotCentersX = listOf(
                        w * 0.10f,
                        w * 0.30f,
                        w * 0.50f,
                        w * 0.70f,
                        w * 0.90f
                    )
                    val centerY = h * 0.40f

                    // Draw connecting circuit path between all 5 pedestals
                    val circuitPath = Path().apply {
                        moveTo(slotCentersX[0], centerY)
                        for (i in 1 until slotCentersX.size) {
                            val prevX = slotCentersX[i - 1]
                            val currX = slotCentersX[i]
                            val midX = (prevX + currX) / 2f
                            // Slight wave/dip for arcade circuit flair
                            val dipY = if (i % 2 == 1) centerY + 14f else centerY - 14f
                            quadraticTo(midX, dipY, currX, centerY)
                        }
                    }

                    // Background circuit glow
                    drawPath(
                        path = circuitPath,
                        color = GameElectricCyan.copy(alpha = 0.20f * activePulse),
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Core bright circuit line
                    drawPath(
                        path = circuitPath,
                        color = GameElectricCyan.copy(alpha = 0.85f * activePulse),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 8f), 0f)
                        )
                    )

                    // Draw glowing node circles behind each pedestal
                    slotCentersX.forEachIndexed { index, cx ->
                        val nodeColor = if (index == 0) GameGoldHighlight else GameElectricCyan
                        drawCircle(
                            color = nodeColor.copy(alpha = 0.15f * activePulse),
                            radius = 28.dp.toPx(),
                            center = Offset(cx, centerY)
                        )
                    }
                }

                // Row of 5 Player Position Pedestals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Position 1: Host Pedestal
                    ArenaPlayerSlotPedestal(
                        slotNumber = 1,
                        label = "HOST",
                        isHost = true,
                        hostAvatarId = uiState.avatarId,
                        isReducedMotion = isReducedMotion
                    )

                    // Position 2: Player 2
                    ArenaPlayerSlotPedestal(
                        slotNumber = 2,
                        label = "SLOT 2",
                        isHost = false,
                        isReducedMotion = isReducedMotion
                    )

                    // Position 3: Player 3
                    ArenaPlayerSlotPedestal(
                        slotNumber = 3,
                        label = "SLOT 3",
                        isHost = false,
                        isReducedMotion = isReducedMotion
                    )

                    // Position 4: Player 4
                    ArenaPlayerSlotPedestal(
                        slotNumber = 4,
                        label = "SLOT 4",
                        isHost = false,
                        isReducedMotion = isReducedMotion
                    )

                    // Position 5: Player 5
                    ArenaPlayerSlotPedestal(
                        slotNumber = 5,
                        label = "SLOT 5",
                        isHost = false,
                        isReducedMotion = isReducedMotion
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanatory Caption
            Text(
                text = "Illustrative arena slots • Room creator occupies Host slot while up to 4 players join",
                fontFamily = FontFamily.Default,
                fontSize = 11.sp,
                color = GameSecondaryText.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Individual Arena Pedestal slot representation.
 */
@Composable
private fun ArenaPlayerSlotPedestal(
    slotNumber: Int,
    label: String,
    isHost: Boolean,
    modifier: Modifier = Modifier,
    hostAvatarId: String? = null,
    isReducedMotion: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isHost) {
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF2C2411),
                                Color(0xFF101D3C)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF0F2648),
                                Color(0xFF071228)
                            )
                        )
                    }
                )
                .border(
                    width = if (isHost) 2.dp else 1.2.dp,
                    brush = if (isHost) {
                        Brush.verticalGradient(listOf(GameGoldHighlight, GameGold))
                    } else {
                        Brush.verticalGradient(listOf(GameElectricCyan.copy(alpha = 0.8f), GameRoyalBlue))
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isHost && hostAvatarId != null) {
                // Host avatar illustration with crown badge
                Image(
                    painter = painterResource(id = resolveAvatarDrawable(hostAvatarId)),
                    contentDescription = "Host Avatar",
                    modifier = Modifier.size(34.dp)
                )
            } else {
                // Open empty slot with plus icon
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Empty Slot $slotNumber",
                    tint = GameElectricCyan.copy(alpha = 0.65f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Position label & badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isHost) Color(0x33E5A93C) else Color(0x2200E5FF))
                .border(
                    width = 0.8.dp,
                    color = if (isHost) GameGoldHighlight.copy(alpha = 0.7f) else GameElectricCyan.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = label,
                fontFamily = FontFamily.Default,
                fontWeight = if (isHost) FontWeight.Bold else FontWeight.Medium,
                fontSize = 9.sp,
                color = if (isHost) GameGoldHighlight else GameWhite
            )
        }
    }
}

/**
 * Capacity and match rules summary banner.
 */
@Composable
private fun ArenaRulesBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x330B1736))
            .border(1.dp, GameRoyalBlue.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0x3300E5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CAPACITY & MATCH RULES",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = GameElectricCyan
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "• Room capacity: 1–5 players (including Host)\n• 1 player may create & wait inside a room\n• Match requires at least 2 connected players (max 5)",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = GameSecondaryText
                )
            }
        }
    }
}

/**
 * Authentication and Facebook Eligibility card implementing Task 3.
 * Supports conditions:
 * A. Guest player
 * B. Google-authenticated without Facebook
 * C. Facebook-connected
 * D. Missing/Pending Facebook configuration
 */
@Composable
private fun ArenaAuthEligibilityCard(
    uiState: FriendsArenaUiState,
    onNavigateToSignIn: () -> Unit,
    onConnectFacebook: () -> Unit,
    onShowSetupPendingNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Status badge & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACCOUNT & ELIGIBILITY",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = GameElectricCyan
                )

                // Dynamic Status Pill
                val (badgeText, badgeBg, badgeBorder, badgeTextColor) = when {
                    uiState.isFacebookConnected -> Quadruple(
                        "FB CONNECTED",
                        Color(0x331877F2),
                        Color(0xFF1877F2),
                        Color(0xFF80B3FF)
                    )
                    uiState.isGoogleAuthenticated -> Quadruple(
                        "GOOGLE AUTH",
                        Color(0x334285F4),
                        Color(0xFF4285F4),
                        Color(0xFF90CAF9)
                    )
                    !uiState.isFacebookConfigured -> Quadruple(
                        "SETUP PENDING",
                        Color(0x33F59E0B),
                        Color(0xFFF59E0B),
                        Color(0xFFFCD34D)
                    )
                    else -> Quadruple(
                        "GUEST PROFILE",
                        Color(0x33718096),
                        Color(0xFF718096),
                        Color(0xFFCBD5E1)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .border(1.dp, badgeBorder.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = badgeTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF101D3C))
                        .border(1.5.dp, GameRoyalBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resolveAvatarDrawable(uiState.avatarId)),
                        contentDescription = "Player Avatar",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.displayName,
                        style = GameTypography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GameWhite,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "ID: ${uiState.publicId}",
                        style = GameTypography.secondaryInfo.copy(
                            color = GameSecondaryText,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Condition-specific presentation & actions
            when {
                // Condition A: Guest Player
                uiState.isGuest -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33142246))
                            .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = GameOrangeAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "An authenticated account is required for online multiplayer rooms. Linking your guest account preserves all completed levels, stars, and achievements.",
                                fontFamily = FontFamily.Default,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = GameSecondaryText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        GamePrimaryButton(
                            text = "SIGN IN / LINK ACCOUNT",
                            onClick = onNavigateToSignIn,
                            height = 46.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Condition B: Google Authenticated without Facebook
                uiState.isGoogleAuthenticated -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33102454))
                            .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Google account authenticated. Facebook connection is required specifically for Facebook Friend Discovery. Google Login does NOT grant or imply Facebook friend authorization.",
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = GameSecondaryText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (uiState.isFacebookConfigured) {
                            // Official Facebook Connection Action
                            FacebookConnectButton(
                                onClick = onConnectFacebook,
                                isLoading = uiState.isLoading
                            )
                        } else {
                            // Honest Setup Pending State
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x331E2A4A))
                                    .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable(onClick = onShowSetupPendingNotice)
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "FACEBOOK SETUP PENDING • CODE JOIN ACTIVE",
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFFFCD34D)
                                    )
                                }
                            }
                        }
                    }
                }

                // Condition C: Facebook Connected Player
                uiState.isFacebookConnected -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x221877F2))
                            .border(1.dp, Color(0xFF1877F2).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF1877F2),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Facebook Connected & Verified",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GameWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your account is verified for Facebook multiplayer. Friend list sync will be activated in Prompt 15.",
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = GameSecondaryText
                        )
                    }
                }

                // Condition D: Missing Facebook configuration
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x331E2A4A))
                            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable(onClick = onShowSetupPendingNotice)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Facebook provider setup pending. Room Code joining and private matches remain fully operational.",
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            color = Color(0xFFFCD34D)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Facebook Connect Button with official Facebook styling.
 */
@Composable
private fun FacebookConnectButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .scale(if (isPressed) 0.98f else 1f)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E88E5),
                        Color(0xFF1877F2),
                        Color(0xFF0D47A1)
                    )
                )
            )
            .border(1.dp, Color(0xFF80B3FF).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_brand_facebook),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isLoading) "CONNECTING..." else "CONNECT FACEBOOK FOR DISCOVERY",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                color = Color.White
            )
        }
    }
}

/**
 * Facebook Friend Discovery Privacy Notice complying with Meta's platform restrictions (Task 4).
 * Clarifies permissions, lack of fake friends, and separation from room codes.
 */
@Composable
private fun ArenaFriendDiscoveryPrivacyCard(
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FACEBOOK FRIEND DISCOVERY PRIVACY",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = GameElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Under Meta platform privacy policies, friend discovery is strictly limited to friends who also have Zynpath installed and have authorized Facebook connection. Zynpath cannot and does not access your entire Facebook friend list.",
                fontFamily = FontFamily.Default,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = GameSecondaryText
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Honest status badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x33102454))
                    .border(0.8.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GameGoldHighlight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "No simulated friends or fake profiles. Mutual friend list sync arrives in Prompt 15.",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = GameGoldHighlight
                )
            }
        }
    }
}

/**
 * Three Premium Action Cards for Friends Arena Entry (Task 5):
 * 1. CREATE ROOM
 * 2. JOIN WITH CODE
 * 3. FACEBOOK FRIENDS
 *
 * Each card maintains honest disabled/upcoming states for features scheduled in Prompt 15.
 */
@Composable
private fun ArenaEntryActionsSection(
    uiState: FriendsArenaUiState,
    onNavigateToSignIn: () -> Unit,
    onNavigateToCreateRoom: () -> Unit,
    onNavigateToJoinWithCode: () -> Unit,
    onNavigateToFacebookFriends: () -> Unit,
    onConnectFacebook: () -> Unit,
    onShowUpcomingNotice: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "ARENA ENTRY ACTIONS",
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = GameElectricCyan
        )

        // Action 1: CREATE ROOM
        ArenaActionCard(
            title = "CREATE ROOM",
            subtitle = "Host a private 1–5 player arena room",
            badgeText = "HOST (1–5)",
            badgeColor = GameGoldHighlight,
            badgeBg = Color(0x33E5A93C),
            icon = Icons.Default.GroupAdd,
            isPrimary = true,
            statusLabel = "1–5 PLAYERS • CREATE & HOST",
            onClick = {
                if (uiState.isGuest) {
                    onShowUpcomingNotice(
                        "Account Required",
                        "Please sign in or link your guest account before creating an online arena room."
                    )
                } else {
                    onNavigateToCreateRoom()
                }
            }
        )

        // Action 2: JOIN WITH CODE
        ArenaActionCard(
            title = "JOIN WITH CODE",
            subtitle = "Enter a 6-character room code from a friend",
            badgeText = "DIRECT CODE",
            badgeColor = GameElectricCyan,
            badgeBg = Color(0x3300E5FF),
            icon = Icons.Default.Pin,
            isPrimary = false,
            statusLabel = "ROOM CODE ENTRY • JOIN LOBBY",
            onClick = onNavigateToJoinWithCode
        )

        // Action 3: FACEBOOK FRIENDS
        val fbBadgeText = when {
            uiState.isFacebookConnected -> "AUTHORIZED"
            uiState.isGoogleAuthenticated -> "FB LOGIN REQUIRED"
            !uiState.isFacebookConfigured -> "SETUP PENDING"
            else -> "ACCOUNT REQUIRED"
        }
        val fbBadgeColor = if (uiState.isFacebookConnected) Color(0xFF80B3FF) else Color(0xFFCBD5E1)

        ArenaActionCard(
            title = "FACEBOOK FRIENDS",
            subtitle = "Invite and play with authorized mutual friends",
            badgeText = fbBadgeText,
            badgeColor = fbBadgeColor,
            badgeBg = Color(0x331877F2),
            icon = Icons.Default.Group,
            isPrimary = false,
            statusLabel = if (uiState.isFacebookConnected) "DISCOVER & INVITE" else "CONNECT TO UNLOCK",
            onClick = onNavigateToFacebookFriends
        )
    }
}

/**
 * Individual Arena Action Card with tactile spring feedback.
 */
@Composable
private fun ArenaActionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    badgeBg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPrimary: Boolean,
    statusLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(if (isPressed) 0.98f else 1f)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isPrimary) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xEE1E2F5A),
                            Color(0xEE101E3E)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xCC101D3C),
                            Color(0xCC0B142D)
                        )
                    )
                }
            )
            .border(
                width = if (isPrimary) 1.5.dp else 1.dp,
                brush = if (isPrimary) {
                    Brush.verticalGradient(listOf(GameGoldHighlight.copy(alpha = 0.8f), GameRoyalBlue))
                } else {
                    Brush.verticalGradient(listOf(GameRoyalBlue.copy(alpha = 0.6f), Color(0x33102454)))
                },
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Icon Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPrimary) Color(0x33E5A93C) else Color(0x2200E5FF)
                    )
                    .border(
                        1.dp,
                        if (isPrimary) GameGoldHighlight.copy(alpha = 0.6f) else GameElectricCyan.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) GameGoldHighlight else GameElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Action Texts
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = GameTypography.screenHeading.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPrimary) GameGoldHighlight else GameWhite
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = GameTypography.secondaryInfo.copy(
                        fontSize = 12.sp,
                        color = GameSecondaryText
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• $statusLabel",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = if (isPrimary) GameGold.copy(alpha = 0.9f) else GameElectricCyan.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Quadruple helper data holder for UI badge rendering.
 */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
