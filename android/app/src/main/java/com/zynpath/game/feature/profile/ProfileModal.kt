package com.zynpath.game.feature.profile

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameSecondaryButton
import com.zynpath.game.core.designsystem.components.GoogleConnectModal
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.player.AvatarCatalog
import com.zynpath.game.core.player.DisplayNameValidationResult

/**
 * Zynpath Simplified Player Profile Modal.
 *
 * Implements Complete Player Profile Simplification:
 * - Replaces the full-screen profile page with a compact modal popup.
 * - Underlying screen remains mounted and visible (dimmed background).
 * - Instant loading backed by local cached Room/DataStore profile data.
 * - Essential information only:
 *   1. Header: "PLAYER PROFILE" with "✕" close button.
 *   2. Player Identity: Circular avatar with edit badge, display name with edit pencil,
 *      and single account status ("Guest Player" or "Google Connected").
 *   3. Statistics: Single horizontal row with Levels (e.g. 68/300), Stars, and Awards.
 *   4. Account: Single compact row ("Google Account — Connect ›" / "Connected ✓").
 * - Zero unnecessary sections: no progression cards, daily challenges, trophies,
 *   multiplayer sign-in text, leaderboards, analytics, or clutter.
 */
@Composable
fun ProfileModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showGoogleConnectModal by remember { mutableStateOf(false) }
    var showSignOutConfirmation by remember { mutableStateOf(false) }

    // Intercept hardware/gesture back to dismiss popup cleanly
    BackHandler {
        when {
            showGoogleConnectModal -> showGoogleConnectModal = false
            showSignOutConfirmation -> showSignOutConfirmation = false
            uiState.isEditingName -> viewModel.cancelEditingName()
            uiState.isAvatarPickerOpen -> viewModel.toggleAvatarPicker(false)
            uiState.conflictDialogMessage != null -> viewModel.dismissConflictDialog()
            uiState.unconfiguredNotice != null -> viewModel.dismissUnconfiguredNotice()
            else -> onDismiss()
        }
    }

    // Modal Background Scrim
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        // Modal Card Container
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .widthIn(max = 380.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF80A1635),
                            GameMidnightBlue
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GameElectricCyan.copy(alpha = 0.65f),
                            Color(0x3321D4FD),
                            Color(0x1102449B)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .glossyTopHighlight(cornerRadius = 22.dp, highlightAlpha = 0.16f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Consume clicks inside card so tapping card doesn't dismiss
                }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            val profile = uiState.profile
            val stats = uiState.statistics
            val avatar = uiState.currentAvatar ?: AvatarCatalog.AVATARS.first()
            val isLinked = uiState.accountLinkingState.isLinked

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Header: PLAYER PROFILE with X close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PLAYER PROFILE",
                        style = GameTypography.screenHeading.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        ),
                        color = GameWhite
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x22102046))
                            .border(1.dp, Color(0x3321D4FD), CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = GameSecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Player Identity: Circular Avatar with subtle edit affordance
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle cyan glow ring
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        GameElectricCyan.copy(alpha = 0.22f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    AvatarWithFrame(
                        avatar = avatar,
                        frameId = uiState.equippedAvatarFrameId,
                        size = 76.dp,
                        onClick = { viewModel.toggleAvatarPicker(true) }
                    )

                    // Small avatar edit affordance badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F1C3F))
                            .border(1.2.dp, GameElectricCyan, CircleShape)
                            .clickable { viewModel.toggleAvatarPicker(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change Avatar",
                            tint = GameElectricCyan,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Player Display Name with edit pencil
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.startEditingName() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = profile?.displayName ?: "Pathfinder",
                        style = GameTypography.screenHeading.copy(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = GameWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Display Name",
                        tint = GameElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // One Account Status: Guest or Google Connected
                Text(
                    text = if (isLinked) "Google Connected" else "Guest Player",
                    style = GameTypography.secondaryInfo.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isLinked) ForestMint else TextMuted
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Player Statistics: Three small stats in ONE horizontal row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val totalLevels = if (stats.totalAvailableSoloLevels > 0) stats.totalAvailableSoloLevels else 300
                    ProfileStatTile(
                        title = "Levels",
                        value = "${stats.completedSoloLevels} / $totalLevels",
                        valueColor = GameWhite,
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatTile(
                        title = "Stars",
                        value = "${stats.totalStarsEarned}",
                        valueColor = GameGoldHighlight,
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatTile(
                        title = "Awards",
                        value = "${stats.unlockedAchievementsCount}",
                        valueColor = GameElectricCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Account: One compact row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF071228))
                        .border(
                            width = 1.dp,
                            color = if (isLinked) ForestMint.copy(alpha = 0.35f) else Color(0x3321D4FD),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Google Account",
                            style = GameTypography.labelMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )

                        if (isLinked) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showSignOutConfirmation = true }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Connected ✓",
                                    style = GameTypography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestMint
                                    )
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showGoogleConnectModal = true }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Connect",
                                    style = GameTypography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GameElectricCyan
                                    )
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Connect Google Account",
                                    tint = GameElectricCyan,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- COMPACT EDIT DIALOGS ---

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

        // Google Connect Modal (reused approved popup)
        if (showGoogleConnectModal) {
            GoogleConnectModal(
                onDismiss = {
                    showGoogleConnectModal = false
                    viewModel.clearGoogleConnectError()
                },
                onContinueWithGoogle = {
                    viewModel.connectWithGoogle(context) {
                        showGoogleConnectModal = false
                    }
                },
                isLoading = uiState.isGoogleConnecting || uiState.isLinkingLoading,
                errorMessage = uiState.googleConnectError,
                onRetry = {
                    viewModel.connectWithGoogle(context) {
                        showGoogleConnectModal = false
                    }
                }
            )
        }

        // Sign Out Confirmation Dialog (when player taps connected status)
        if (showSignOutConfirmation) {
            AlertDialog(
                onDismissRequest = { showSignOutConfirmation = false },
                title = {
                    Text(
                        text = "Google Account",
                        style = GameTypography.screenHeading.copy(fontSize = 18.sp),
                        color = GameWhite
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Connected with Google. Your progress is securely saved to your account.",
                            style = GameTypography.secondaryInfo.copy(fontSize = 13.sp, color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Would you like to sign out to return to offline guest mode?",
                            style = GameTypography.secondaryInfo.copy(fontSize = 12.sp, color = TextMuted)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSignOutConfirmation = false
                            viewModel.signOut()
                        }
                    ) {
                        Text(
                            text = "SIGN OUT",
                            style = GameTypography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutConfirmation = false }) {
                        Text(
                            text = "CANCEL",
                            style = GameTypography.labelMedium.copy(color = GameSecondaryText)
                        )
                    }
                },
                containerColor = Color(0xFF0F1A36),
                shape = GameShapes.panel
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
}

/**
 * Compact Stat Tile for the horizontal statistics row.
 */
@Composable
private fun ProfileStatTile(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF071228))
            .border(1.dp, Color(0x3321D4FD), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = GameTypography.labelMedium.copy(
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = GameTypography.screenHeading.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor
                ),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Accessible Display Name Editor Dialog.
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
 * Avatar Selection Dialog displaying the available pathfinder avatars.
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                GameSecondaryButton(
                    text = "CLOSE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
