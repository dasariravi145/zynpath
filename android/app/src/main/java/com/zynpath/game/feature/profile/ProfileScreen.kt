package com.zynpath.game.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.cosmetics.ui.AvatarWithFrame
import com.zynpath.game.core.designsystem.components.GameLoadingIndicator
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameProgressBar
import com.zynpath.game.core.designsystem.components.GameRewardBadge
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.components.ProgressBarStyle
import com.zynpath.game.core.designsystem.components.RewardTier
import com.zynpath.game.core.designsystem.components.RewardType
import com.zynpath.game.core.designsystem.components.StatusBadge
import com.zynpath.game.core.designsystem.layout.rememberZynpathWindowInfo
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.AccentPurple
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.multiplayer.model.CompetitiveStats
import com.zynpath.game.core.player.AvatarCatalog
import com.zynpath.game.core.player.DisplayNameValidationResult

/**
 * Premium Player Profile Screen displaying authoritative local guest identity, stats,
 * avatars, progression, achievements preview, and account linking options.
 *
 * Implements Prompt 01 Design System, Prompt 17, Prompt 18, and Prompt 10/24:
 * - Deep navy fantasy background with star dust
 * - Royal-blue glassmorphism game panels with cyan and gold rim lighting
 * - Original game-style avatar presentation with equipped frames
 * - Authoritative repository-backed values (no invented ranks or simulated stats)
 * - Clear distinction between guest and connected account status
 * - Compact achievement preview linking to Rewards & Achievements
 * - Responsive phone layout, accessibility, and system insets
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToMatchHistory: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToCosmetics: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val windowInfo = rememberZynpathWindowInfo()

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    GameScreenBackground(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                GameTopBar(
                    title = "PLAYER PROFILE",
                    subtitle = if (uiState.profile?.isGuest == true) "Offline Guest" else "Cloud Connected",
                    onBackClick = onBackClick,
                    actions = {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .semantics {
                                    role = Role.Button
                                    contentDescription = "Open Settings"
                                }
                                .clip(CircleShape)
                                .background(Color(0xCC101D3C))
                                .border(1.5.dp, GameRoyalBlue.copy(alpha = 0.6f), CircleShape)
                                .clickable(onClick = onNavigateToSettings),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = GameWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            val profile = uiState.profile
            val stats = uiState.statistics
            val avatar = uiState.currentAvatar ?: AvatarCatalog.AVATARS.first()

            val maxContentWidth = windowInfo.maxContentWidth
            val isBoundedWidth = maxContentWidth != androidx.compose.ui.unit.Dp.Unspecified && !windowInfo.useSideBySideLayout

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .then(
                        if (isBoundedWidth) Modifier.widthIn(max = maxContentWidth)
                        else Modifier
                    )
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Identity & Avatar Header Panel
                item(key = "identity_header") {
                    ProfileIdentityCard(
                        displayName = profile?.displayName ?: "Pathfinder",
                        shortTag = profile?.shortGuestTag ?: "ZYN-GUEST",
                        isGuest = profile?.isGuest ?: true,
                        avatar = avatar,
                        avatarFrameId = uiState.equippedAvatarFrameId,
                        publicId = profile?.formattedPublicId ?: "Unassigned (Offline Guest)",
                        onEditAvatarClick = { viewModel.toggleAvatarPicker(true) },
                        onEditNameClick = { viewModel.startEditingName() },
                        onCustomizeAppearanceClick = onNavigateToCosmetics
                    )
                }

                // 2. Account Status & Cloud Linking Panel
                item(key = "account_linking") {
                    AccountLinkingCard(
                        isLinked = uiState.accountLinkingState.isLinked,
                        provider = uiState.accountLinkingState.provider,
                        onActionClick = { viewModel.toggleAccountLinkingDialog(true) }
                    )
                }

                // 3. Player Progression Panels (Solo & Daily)
                item(key = "progression_milestones") {
                    PlayerProgressionPanel(
                        completedSoloLevels = stats.completedSoloLevels,
                        totalSoloLevels = stats.totalAvailableSoloLevels,
                        totalStars = stats.totalStarsEarned,
                        soloProgress = stats.soloCompletionPercentage,
                        completedDailyChallenges = stats.completedDailyChallenges,
                        currentStreak = stats.currentDailyStreak,
                        bestStreak = stats.bestDailyStreak
                    )
                }

                // 4. Compact Achievements Preview Panel (Links to Prompt 09 screen)
                item(key = "achievements_preview") {
                    AchievementsPreviewPanel(
                        unlockedCount = stats.unlockedAchievementsCount,
                        totalCount = stats.totalAchievementsCount,
                        totalStars = stats.totalStarsEarned,
                        percentage = stats.achievementPercentage,
                        onViewAllClick = onNavigateToAchievements
                    )
                }

                // 5. Competitive Multiplayer Progression Panel
                item(key = "competitive_stats") {
                    CompetitiveProgressionCard(
                        competitiveStats = uiState.competitiveStats,
                        isGuest = uiState.profile?.isGuest ?: true,
                        onNavigateToHistory = onNavigateToMatchHistory,
                        onNavigateToLeaderboard = onNavigateToLeaderboard,
                        onNavigateToSignIn = onNavigateToSignIn
                    )
                }

                // 6. Detailed Analytics & Insights Entry Card
                item(key = "analytics_entry") {
                    PersonalAnalyticsCard(onClick = onNavigateToStatistics)
                }
            }
        }
    }

    // Name Editing Modal
    if (uiState.isEditingName) {
        EditNameDialog(
            nameInput = uiState.nameInput,
            validationResult = uiState.nameValidationResult,
            onNameChange = { viewModel.onNameInputChanged(it) },
            onSave = { viewModel.saveName() },
            onDismiss = { viewModel.cancelEditingName() }
        )
    }

    // Avatar Selection Modal
    if (uiState.isAvatarPickerOpen) {
        AvatarPickerDialog(
            currentAvatarId = uiState.profile?.avatarId ?: AvatarCatalog.DEFAULT_AVATAR_ID,
            onSelect = { viewModel.selectAvatar(it) },
            onDismiss = { viewModel.toggleAvatarPicker(false) }
        )
    }

    // Account Linking Modal
    if (uiState.showAccountLinkingDialog) {
        AccountLinkingDialog(
            isLinked = uiState.accountLinkingState.isLinked,
            provider = uiState.accountLinkingState.provider,
            publicZynpathId = uiState.accountLinkingState.publicZynpathId,
            isLoading = uiState.isLinkingLoading,
            isGoogleAvailable = uiState.isGoogleAvailable,
            isFacebookAvailable = uiState.isFacebookAvailable,
            onLinkGoogle = { viewModel.linkWithGoogle(context) },
            onLinkFacebook = { viewModel.linkWithFacebook(context) },
            onSignOut = { viewModel.signOut() },
            onDismiss = { viewModel.toggleAccountLinkingDialog(false) }
        )
    }

    // Linking Conflict Dialog
    if (uiState.conflictDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConflictDialog() },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Account Conflict", style = GameTypography.screenHeading.copy(fontSize = 18.sp)) },
            text = { Text(uiState.conflictDialogMessage ?: "", style = GameTypography.secondaryInfo) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissConflictDialog() }) {
                    Text("OK", style = GameTypography.labelMedium.copy(color = GameElectricCyan))
                }
            },
            containerColor = Color(0xFF0F1A36),
            shape = GameShapes.panel
        )
    }

    // Unconfigured Provider Notice Dialog
    if (uiState.unconfiguredNotice != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUnconfiguredNotice() },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = ForestMint) },
            title = { Text("Provider Setup Pending", style = GameTypography.screenHeading.copy(fontSize = 18.sp)) },
            text = {
                Column {
                    Text(uiState.unconfiguredNotice ?: "", style = GameTypography.secondaryInfo)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Solo Play and offline Daily Challenge remain 100% available without login.",
                        style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextMuted)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissUnconfiguredNotice() }) {
                    Text("Understood", style = GameTypography.labelMedium.copy(color = ForestMint))
                }
            },
            containerColor = Color(0xFF0F1A36),
            shape = GameShapes.panel
        )
    }
}

/**
 * Profile Identity Card featuring player avatar with equipped cosmetics frame,
 * display name, guest tags, and customizable appearance action.
 */
@Composable
private fun ProfileIdentityCard(
    displayName: String,
    shortTag: String,
    isGuest: Boolean,
    avatar: com.zynpath.game.core.player.AvatarOption,
    avatarFrameId: String,
    publicId: String,
    onEditAvatarClick: () -> Unit,
    onEditNameClick: () -> Unit,
    onCustomizeAppearanceClick: () -> Unit,
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
            // Avatar with equipped cosmetics frame and radiant glow ring
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Subtle glowing background disc
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x3321D4FD),
                                    Color(0x11FFC247),
                                    Color.Transparent
                                )
                            )
                        )
                )

                AvatarWithFrame(
                    avatar = avatar,
                    frameId = avatarFrameId,
                    size = 92.dp,
                    onClick = onEditAvatarClick
                )

                // Edit badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1C3F))
                        .border(1.2.dp, GameElectricCyan, CircleShape)
                        .clickable(onClick = onEditAvatarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Avatar",
                        tint = GameElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Name with edit icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onEditNameClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = displayName,
                    style = GameTypography.screenHeading.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = GameWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Display Name",
                    tint = GameGoldHighlight,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status Badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = if (isGuest) "GUEST • $shortTag" else shortTag,
                    color = if (isGuest) ForestMint else AccentGold
                )
                StatusBadge(
                    text = if (isGuest) "OFFLINE PROFILE" else "CONNECTED",
                    color = if (isGuest) PathCyanGlow else ForestMint
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Public Zynpath ID Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF09142C))
                    .border(1.dp, Color(0xFF162544), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = "Public Zynpath ID",
                        style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextMuted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = publicId,
                        style = GameTypography.labelMedium.copy(fontSize = 13.sp, color = GameWhite),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(
                    text = if (isGuest) "Unlinked" else "Verified",
                    color = if (isGuest) TextMuted else ForestMint
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Customize Appearance Button
            GameSecondaryButton(
                text = "CUSTOMIZE APPEARANCE (THEMES & FRAMES)",
                onClick = onCustomizeAppearanceClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Account Status & Linking Panel presenting current session state (Guest, Google, Facebook)
 * and action button to connect accounts.
 */
@Composable
private fun AccountLinkingCard(
    isLinked: Boolean,
    provider: String?,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isLinked) ForestMint.copy(alpha = 0.18f)
                            else AccentPurple.copy(alpha = 0.2f)
                        )
                        .border(
                            1.dp,
                            if (isLinked) ForestMint else AccentPurple,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLinked) Icons.Default.CheckCircle else Icons.Default.Link,
                        contentDescription = "Account Status",
                        tint = if (isLinked) ForestMint else AccentPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = if (isLinked) "Linked with $provider" else "Link Account",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isLinked) "Progress securely backed up to cloud" else "Secure guest progress via Google or Facebook",
                        style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (isLinked) {
                GameSecondaryButton(
                    text = "MANAGE",
                    onClick = onActionClick,
                    fillMaxWidth = false,
                    height = 42.dp
                )
            } else {
                GamePrimaryButton(
                    text = "LINK",
                    onClick = onActionClick,
                    fillMaxWidth = false,
                    height = 42.dp
                )
            }
        }
    }
}

/**
 * Player Progression Panel displaying verified Solo completion, stars earned,
 * and Daily Challenge streak data.
 */
@Composable
private fun PlayerProgressionPanel(
    completedSoloLevels: Int,
    totalSoloLevels: Int,
    totalStars: Int,
    soloProgress: Float,
    completedDailyChallenges: Int,
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        title = "PLAYER PROGRESSION",
        subtitle = "Verified puzzle completion and milestone tracks",
        contentPadding = 16.dp
    ) {
        // Solo Progression Subcard
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF09142C))
                .border(1.dp, Color(0xFF162544), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PathCyanGlow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = "Solo Progression",
                            tint = PathCyanGlow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Solo Progression",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        )
                    )
                }

                Text(
                    text = "$completedSoloLevels / $totalSoloLevels Solved",
                    style = GameTypography.labelMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PathCyanGlow
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            GameProgressBar(
                progress = soloProgress,
                height = 8.dp,
                style = ProgressBarStyle.CYAN
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$totalStars Stars Earned across canonical levels",
                style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Daily Challenge Progression Subcard
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF09142C))
                .border(1.dp, Color(0xFF162544), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ForestMint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Daily Challenge",
                            tint = ForestMint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Daily Challenge",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        )
                    )
                }

                Text(
                    text = "$completedDailyChallenges Solved",
                    style = GameTypography.labelMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestMint
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val streakFraction = if (bestStreak > 0) currentStreak.toFloat() / bestStreak.toFloat() else 0f
            GameProgressBar(
                progress = streakFraction,
                height = 8.dp,
                style = ProgressBarStyle.GOLD
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🔥 ${currentStreak}d Current Streak (Best Record: ${bestStreak}d)",
                style = GameTypography.secondaryInfo.copy(
                    fontSize = 12.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

/**
 * Compact Achievements Preview Panel with authoritative milestone numbers,
 * GameRewardBadge highlights, and navigation to the full Achievements Screen.
 */
@Composable
private fun AchievementsPreviewPanel(
    unlockedCount: Int,
    totalCount: Int,
    totalStars: Int,
    percentage: Float,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        title = "ACHIEVEMENTS & TROPHIES",
        subtitle = "Verified milestone unlocks",
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Milestones Unlocked",
                style = GameTypography.labelMedium.copy(color = GameWhite)
            )

            Text(
                text = "$unlockedCount / $totalCount (${(percentage * 100).toInt()}%)",
                style = GameTypography.labelMedium.copy(
                    color = GameGoldHighlight,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        GameProgressBar(
            progress = percentage,
            height = 10.dp,
            style = ProgressBarStyle.GOLD
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Highlight Badges Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameRewardBadge(
                type = RewardType.TROPHY,
                title = "Trophies",
                value = "$unlockedCount",
                tier = RewardTier.GOLD,
                modifier = Modifier.weight(1f)
            )

            GameRewardBadge(
                type = RewardType.STAR,
                title = "Solo Stars",
                value = "$totalStars",
                tier = RewardTier.DIAMOND,
                modifier = Modifier.weight(1f)
            )

            GameRewardBadge(
                type = RewardType.CROWN,
                title = "Mastery",
                value = "${(percentage * 100).toInt()}%",
                tier = RewardTier.SILVER,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Button: View All Achievements
        GameSecondaryButton(
            text = "VIEW ALL ACHIEVEMENTS →",
            onClick = onViewAllClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Competitive Multiplayer Progression Card showing rating, duels, and league stats.
 */
@Composable
private fun CompetitiveProgressionCard(
    competitiveStats: CompetitiveStats?,
    isGuest: Boolean,
    onNavigateToHistory: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        title = "COMPETITIVE MULTIPLAYER",
        subtitle = "Global ratings, duels & leagues",
        contentPadding = 16.dp
    ) {
        if (isGuest || competitiveStats == null) {
            Text(
                text = "Sign in to track verified 1v1 Quick Duel & Mini League statistics, participate on global leaderboards, and unlock competitive achievements.",
                style = GameTypography.secondaryInfo.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = GameSecondaryText
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameSecondaryButton(
                    text = "LEADERBOARD",
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
                GamePrimaryButton(
                    text = "SIGN IN",
                    onClick = onNavigateToSignIn,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Metrics Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Quick Duel", style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextMuted))
                    Text(
                        "${competitiveStats.quickDuelWins}W / ${competitiveStats.quickDuelLosses}L",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PathCyanGlow
                        )
                    )
                    Text(
                        "${(competitiveStats.quickDuelWinRate * 100).toInt()}% Win Rate",
                        style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Friend Duel", style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextMuted))
                    Text(
                        "${competitiveStats.friendDuelWins} Wins",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    )
                    Text(
                        "${competitiveStats.friendDuelMatches} Matches",
                        style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mini League", style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextMuted))
                    Text(
                        "${competitiveStats.miniLeagueFirstPlaceFinishes} 1st Places",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestMint
                        )
                    )
                    Text(
                        "${competitiveStats.miniLeagueParticipations} Entered",
                        style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameSecondaryButton(
                    text = "MATCH HISTORY",
                    onClick = onNavigateToHistory,
                    modifier = Modifier.weight(1f)
                )
                GamePrimaryButton(
                    text = "LEADERBOARD",
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Detailed Analytics & Personal Insights Entry Card.
 */
@Composable
private fun PersonalAnalyticsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamePanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1A36))
                        .border(1.2.dp, GameElectricCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = null,
                        tint = GameElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Detailed Analytics & Insights",
                        style = GameTypography.labelMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameWhite
                        )
                    )
                    Text(
                        text = "Progression trends, personal bests & stats",
                        style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted)
                    )
                }
            }
            Text(
                text = "View →",
                style = GameTypography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameElectricCyan
                )
            )
        }
    }
}

/**
 * Accessible Display Name Editor Modal.
 */
@Composable
private fun EditNameDialog(
    nameInput: String,
    validationResult: DisplayNameValidationResult?,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(GameShapes.panel)
                .background(Color(0xFA0B1633))
                .border(1.5.dp, GameGoldHighlight, GameShapes.panel)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Display Name",
                    style = GameTypography.screenHeading.copy(fontSize = 18.sp),
                    color = GameWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Choose how other players see you in Zynpath",
                    style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = onNameChange,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GameWhite,
                        unfocusedTextColor = GameWhite,
                        focusedBorderColor = GameGoldHighlight,
                        unfocusedBorderColor = Color(0xFF1E2D50),
                        cursorColor = GameGoldHighlight
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error feedback
                val errorMessage = when (validationResult) {
                    is DisplayNameValidationResult.Empty -> "Name cannot be empty"
                    is DisplayNameValidationResult.TooShort -> "Must be at least 2 characters"
                    is DisplayNameValidationResult.TooLong -> "Must be 20 characters or less"
                    is DisplayNameValidationResult.InvalidCharacters -> "Only letters, numbers, spaces, '-' and '_' allowed"
                    else -> null
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        style = GameTypography.secondaryInfo.copy(
                            fontSize = 12.sp,
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameSecondaryButton(
                        text = "CANCEL",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    GamePrimaryButton(
                        text = "SAVE",
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Avatar Selection Modal displaying the available pathfinder avatars.
 */
@Composable
private fun AvatarPickerDialog(
    currentAvatarId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(GameShapes.panel)
                .background(Color(0xFA0B1633))
                .border(1.5.dp, GameElectricCyan, GameShapes.panel)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Avatar",
                    style = GameTypography.screenHeading.copy(fontSize = 18.sp),
                    color = GameWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Choose your pathfinder symbol",
                    style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AvatarCatalog.AVATARS) { avatar ->
                        val isSelected = avatar.id == currentAvatarId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF102046) else Color(0xFF091328))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GameGoldHighlight else Color(0x3321D4FD),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelect(avatar.id) }
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(avatar.primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = avatar.icon,
                                    contentDescription = avatar.title,
                                    tint = Color(0xFF060D1E),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = avatar.title,
                                style = GameTypography.secondaryInfo.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GameWhite else TextSecondary
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                GameSecondaryButton(
                    text = "DONE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Account Linking Modal supporting Google and Facebook providers,
 * sign out, and guest progress preservation notices.
 */
@Composable
private fun AccountLinkingDialog(
    isLinked: Boolean,
    provider: String?,
    publicZynpathId: String?,
    isLoading: Boolean,
    isGoogleAvailable: Boolean,
    isFacebookAvailable: Boolean,
    onLinkGoogle: () -> Unit,
    onLinkFacebook: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(GameShapes.panel)
                .background(Color(0xFA0B1633))
                .border(1.5.dp, if (isLinked) ForestMint else AccentPurple, GameShapes.panel)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isLinked) ForestMint.copy(alpha = 0.2f) else AccentPurple.copy(alpha = 0.2f))
                        .border(1.2.dp, if (isLinked) ForestMint else AccentPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLinked) Icons.Default.CheckCircle else Icons.Default.Link,
                        contentDescription = null,
                        tint = if (isLinked) ForestMint else AccentPurple,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isLinked) "Account Linked" else "Link Account",
                    style = GameTypography.screenHeading.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = GameWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (isLinked) {
                    Text(
                        text = "Linked with $provider\nPublic ID: ${publicZynpathId ?: "Assigned"}",
                        style = GameTypography.secondaryInfo.copy(
                            fontSize = 13.sp,
                            color = ForestMint,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your progress is secured. You can sign out at any time to return to offline guest play.",
                        style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    GameSecondaryButton(
                        text = "SIGN OUT",
                        onClick = onSignOut,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Connect Google or Facebook to link your guest progress. Your completed levels, stars, best times, and achievements are completely preserved!",
                        style = GameTypography.secondaryInfo.copy(fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        GameLoadingIndicator(message = "Linking account...")
                    } else {
                        // Google Link Button
                        GamePrimaryButton(
                            text = if (isGoogleAvailable) "LINK WITH GOOGLE" else "LINK WITH GOOGLE (SETUP PENDING)",
                            onClick = onLinkGoogle,
                            enabled = isGoogleAvailable,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Facebook Link Button
                        GameSecondaryButton(
                            text = if (isFacebookAvailable) "LINK WITH FACEBOOK" else "LINK WITH FACEBOOK (SETUP PENDING)",
                            onClick = onLinkFacebook,
                            enabled = isFacebookAvailable,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                GameSecondaryButton(
                    text = "CLOSE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
