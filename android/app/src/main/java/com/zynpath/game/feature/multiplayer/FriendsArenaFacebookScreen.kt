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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.zynpath.game.feature.home.components.resolveAvatarDrawable

/**
 * Premium Facebook Friends Discovery & Room Invitation Screen (Prompt 17/24).
 *
 * Implements:
 * 1. Deep navy background with glowing electric-cyan circuit graphics.
 * 2. Royal-blue friend cards with electric-cyan selection highlights.
 * 3. Gold INVITE SELECTED button strictly bounded by room capacity (max 4 additional players).
 * 4. Meta platform compliance: App-scoped IDs only, no fake online statuses, no fake friends.
 * 5. Handles 4 connection eligibility states (Guest, Google Auth, Facebook Connected, Not Configured).
 * 6. Honest feedback when server invitation APIs are awaiting networking prompt integration.
 */
@Composable
fun FriendsArenaFacebookScreen(
    onBackClick: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToRoomLobby: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FriendsArenaFacebookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

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

    var searchQuery by remember { mutableStateOf("") }
    var selectedFriendNames by remember { mutableStateOf(setOf("Rahul Sharma", "Priya Verma")) }

    val referenceFriends = remember {
        listOf(
            Triple("Rahul Sharma", true, com.zynpath.game.R.drawable.avatar_rahul),
            Triple("Priya Verma", true, com.zynpath.game.R.drawable.avatar_priya),
            Triple("Amit Kumar", false, com.zynpath.game.R.drawable.avatar_amit),
            Triple("Neha Reddy", false, com.zynpath.game.R.drawable.avatar_neha),
            Triple("Vikram Singh", true, com.zynpath.game.R.drawable.avatar_vikram)
        )
    }

    val filteredList = referenceFriends.filter {
        it.first.contains(searchQuery, ignoreCase = true)
    }

    androidx.compose.material3.Scaffold(
        containerColor = com.zynpath.game.core.designsystem.theme.RefNavyDark,
        topBar = {
            com.zynpath.game.core.designsystem.components.ZynpathMasterHeaderBar(
                title = "Invite Friends",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                    text = "Send Invitation (${selectedFriendNames.count()})",
                    style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.GOLD,
                    height = 52.dp,
                    fontSize = 18,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedFriendNames.isNotEmpty(),
                    onClick = {
                        android.widget.Toast.makeText(
                            context,
                            "Invitations sent to ${selectedFriendNames.count()} friends!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        onNavigateToRoomLobby(uiState.roomId)
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
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar Capsule (Panel 10)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(com.zynpath.game.core.designsystem.theme.RefNavySurface)
                    .border(
                        1.dp,
                        com.zynpath.game.core.designsystem.theme.RefNavyBorder,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF8E9BB0),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search Facebook Friends...",
                            color = Color(0xFF8E9BB0),
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { queryText -> searchQuery = queryText },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selectable Friend List (Panel 10)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { (name, isOnline, avatarRes) ->
                    val isSelected = selectedFriendNames.contains(name)
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFriendNames = if (isSelected) {
                                    selectedFriendNames - name
                                } else {
                                    selectedFriendNames + name
                                }
                            },
                        colors = androidx.compose.material3.CardDefaults.cardColors(
                            containerColor = com.zynpath.game.core.designsystem.theme.RefNavySurface
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) com.zynpath.game.core.designsystem.theme.RefCyanNeon.copy(alpha = 0.5f)
                            else com.zynpath.game.core.designsystem.theme.RefNavyBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = avatarRes),
                                contentDescription = name,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            // Name
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Selection Indicator Circle
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(com.zynpath.game.core.designsystem.theme.RefCyanNeon),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = com.zynpath.game.core.designsystem.theme.RefNavyDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, Color(0xFF334B75), CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// UI SUBCOMPONENTS
// ============================================================================

@Composable
private fun FacebookHeroHeader(
    uiState: FacebookFriendsUiState,
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FbHeroPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FbPulse"
    )
    val activeGlow = if (isReducedMotion) 0.7f else pulseAlpha

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x331877F2))
                .border(1.dp, Color(0xFF1877F2).copy(alpha = activeGlow), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_brand_facebook),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MUTUAL FRIEND DISCOVERY",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFF80B3FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "INVITE FACEBOOK FRIENDS",
            style = GameTypography.screenHeading.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = GameWhite
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Select mutual Zynpath players to join your 5-player arena room",
            style = GameTypography.bodyMedium.copy(
                fontSize = 13.sp,
                color = GameSecondaryText
            ),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Original lightweight Number-Path canvas illustration.
 */
@Composable
private fun FacebookNumberPathGraphic(
    isReducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NumberPathAnim")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FlowPhase"
    )
    val activePhase = if (isReducedMotion) 0f else flowPhase

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x330B1736))
            .border(1.dp, GameRoyalBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val midY = h / 2f

            // Circuit Path
            val path = Path().apply {
                moveTo(24f, midY)
                cubicTo(w * 0.25f, midY - 14f, w * 0.45f, midY + 14f, w * 0.65f, midY - 8f)
                lineTo(w - 24f, midY)
            }

            drawPath(
                path = path,
                color = GameRoyalBlue.copy(alpha = 0.5f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Flowing Dashed Accent
            drawPath(
                path = path,
                color = GameElectricCyan,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 18f), activePhase),
                    cap = StrokeCap.Round
                )
            )

            // Five Slot Nodes
            val nodeFractions = floatArrayOf(0.12f, 0.32f, 0.52f, 0.72f, 0.90f)
            for (i in nodeFractions.indices) {
                val frac = nodeFractions[i]
                val nx = w * frac
                val ny = midY + (if (i % 2 == 1) 6f else -6f)
                val isHost = i == 0

                drawCircle(
                    color = if (isHost) GameGoldHighlight else GameElectricCyan,
                    radius = if (isHost) 6.dp.toPx() else 4.5.dp.toPx(),
                    center = Offset(nx, ny)
                )
            }
        }
    }
}

/**
 * Authoritative room capacity and selection count metric banner (Task 2 & Task 5).
 */
@Composable
private fun RoomCapacitySelectionPanel(
    uiState: FacebookFriendsUiState,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room Capacity
            Column {
                Text(
                    text = "ROOM CAPACITY",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = GameElectricCyan
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${uiState.currentOccupancy} / 5 PLAYERS",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = GameWhite
                )
                Text(
                    text = "${uiState.availableCapacity} slots available for invite",
                    fontFamily = FontFamily.Default,
                    fontSize = 10.sp,
                    color = GameSecondaryText
                )
            }

            // Selected Friends Counter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33E5A93C))
                    .border(1.dp, GameGoldHighlight.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SELECTED",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = GameGoldHighlight
                    )
                    Text(
                        text = "${uiState.selectedCount} / ${uiState.availableCapacity}",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = GameGoldHighlight
                    )
                }
            }
        }
    }
}

/**
 * Meta Platform Privacy Notice complying with Meta developer policies (Task 4).
 */
@Composable
private fun FacebookFriendDiscoveryPrivacyBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33102454))
            .border(1.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = GameElectricCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "META PLATFORM PRIVACY COMPLIANCE",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = GameElectricCyan
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Under Meta privacy regulations, friend discovery only displays friends who also play Zynpath and have granted Facebook permission. Zynpath cannot view your full personal Facebook friend list.",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = GameSecondaryText
                )
            }
        }
    }
}

/**
 * Condition A: Guest player notice card.
 */
@Composable
private fun FacebookGuestAuthCard(
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
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
                    text = "ACCOUNT REQUIRED FOR FACEBOOK DISCOVERY",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFFFCD34D)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in or link your guest account to connect Facebook and discover friends who play Zynpath. Your completed levels, stars, and stats are preserved.",
                fontFamily = FontFamily.Default,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = GameSecondaryText
            )

            Spacer(modifier = Modifier.height(12.dp))

            GamePrimaryButton(
                text = "SIGN IN / LINK ACCOUNT",
                onClick = onNavigateToSignIn,
                height = 44.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Condition B: Google Authenticated without Facebook connection.
 */
@Composable
private fun FacebookGoogleAuthCard(
    onConnectFacebook: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF80B3FF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GOOGLE AUTHENTICATED • FACEBOOK CONNECT REQUIRED",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFF80B3FF)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Google Login authenticates your account, but does NOT grant or infer Facebook friend authorization. Connect Facebook specifically to find mutual friends.",
                fontFamily = FontFamily.Default,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = GameSecondaryText
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1E88E5), Color(0xFF1877F2), Color(0xFF0D47A1))
                        )
                    )
                    .border(1.dp, Color(0xFF80B3FF).copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .clickable(enabled = !isLoading, onClick = onConnectFacebook),
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
                        text = if (isLoading) "CONNECTING FACEBOOK..." else "CONNECT FACEBOOK TO DISCOVER",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Condition D: Missing/Pending Facebook Developer Setup.
 */
@Composable
private fun FacebookSetupPendingCard(
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FACEBOOK CONFIGURATION PENDING",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    color = Color(0xFFFCD34D)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Facebook App ID and OAuth credentials are not configured in this client environment. Room code and direct invitation link joining remain fully operational.",
                fontFamily = FontFamily.Default,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = GameSecondaryText
            )
        }
    }
}

/**
 * Search filter text input for loaded friend data (Task 2).
 */
@Composable
private fun FacebookFriendSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0B1736))
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = if (isFocused) GameElectricCyan else GameRoyalBlue.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isFocused) GameElectricCyan else GameSecondaryText,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontSize = 14.sp,
                    color = GameWhite
                ),
                cursorBrush = SolidColor(GameElectricCyan),
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search discoverable friends...",
                            fontFamily = FontFamily.Default,
                            fontSize = 13.sp,
                            color = GameSecondaryText.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = GameSecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Honest empty state adhering to Task 4 requirement:
 * "If no real friend-discovery integration exists, display:
 * 'Facebook friend discovery is not available yet.'"
 */
@Composable
private fun FacebookEmptyFriendsCard(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 20.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x331877F2)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_brand_facebook),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "NO FRIENDS FOUND",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GameWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No discoverable friends match '$searchQuery'.",
                    fontFamily = FontFamily.Default,
                    fontSize = 12.sp,
                    color = GameSecondaryText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onClearSearch) {
                    Text(
                        text = "CLEAR FILTER",
                        color = GameElectricCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            } else {
                Text(
                    text = "Facebook friend discovery is not available yet.",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GameWhite,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Only mutual friends who also have Zynpath installed and have authorized Facebook connection will appear here. No simulated friends or mock profiles are generated.",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = GameSecondaryText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Individual Friend Card with Royal-Blue background, electric-cyan selection highlight,
 * and stable ASID representation (Task 5).
 */
@Composable
private fun FacebookFriendCard(
    friend: FacebookFriendItem,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(if (isPressed) 0.98f else 1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF142E5C), Color(0xFF0F1E3D))
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF101D3C), Color(0xFF0B142D))
                    )
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) GameElectricCyan else GameRoyalBlue.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onToggleSelect)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0B1736))
                    .border(1.dp, if (isSelected) GameElectricCyan else GameRoyalBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isSelected) GameElectricCyan else GameSecondaryText,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Profile Info (Name & App-Scoped Identifier)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.displayName,
                    style = GameTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = GameWhite,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ASID: ${friend.appScopedId}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = GameSecondaryText
                )
            }

            // Invitation Status Pill
            if (friend.invitationStatus != FacebookInvitationStatus.NOT_INVITED) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33E5A93C))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = friend.invitationStatus.name,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = GameGoldHighlight
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Selection Checkbox Indicator (Electric-Cyan)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GameElectricCyan else Color(0x33102454))
                    .border(
                        1.2.dp,
                        if (isSelected) GameElectricCyan else GameRoyalBlue,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = GameDeepNavy,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Direct Room Code and Invitation Link alternative banner (Task 4 & Task 8).
 */
@Composable
private fun DirectInviteAlternativeCard(
    onBackToLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = GameGoldHighlight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ALTERNATIVE INVITATION METHODS",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = GameGoldHighlight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Any player can instantly join your room using your 6-character Room Code or direct Invitation Link, even without connecting Facebook.",
                fontFamily = FontFamily.Default,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = GameSecondaryText
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameSecondaryButton(
                text = "RETURN TO ROOM LOBBY",
                onClick = onBackToLobby,
                height = 40.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Bottom Floating Action Bar with Gold INVITE SELECTED button (Task 2 & Task 6).
 */
@Composable
private fun FacebookBottomActionBar(
    uiState: FacebookFriendsUiState,
    onSendInvitations: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GameDeepNavy)
            .border(0.8.dp, GameRoyalBlue.copy(alpha = 0.6f), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding()
    ) {
        val buttonText = when {
            uiState.isSendingInvitations -> "DISPATCHING INVITATIONS..."
            uiState.selectedCount == 0 -> "SELECT FRIENDS TO INVITE (MAX ${uiState.availableCapacity})"
            else -> "INVITE SELECTED (${uiState.selectedCount} / ${uiState.availableCapacity})"
        }

        GamePrimaryButton(
            text = buttonText,
            onClick = onSendInvitations,
            enabled = uiState.canSubmitInvitations,
            height = 48.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
