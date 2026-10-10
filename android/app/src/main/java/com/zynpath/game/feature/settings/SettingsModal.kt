package com.zynpath.game.feature.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.BuildConfig
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.designsystem.components.GoogleConnectModal
import com.zynpath.game.core.designsystem.components.glossyTopHighlight
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameSuccessGreen
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite

/**
 * Compact, player-essential Settings Modal popup.
 *
 * Implements Prompt 49 — ZYNPATH SETTINGS COMPLETE SIMPLIFICATION:
 * - Opens as a centered modal over the current screen with dark translucent scrim.
 * - Current screen remains mounted and visible underneath.
 * - X dismisses; Android Back dismisses.
 * - Extremely compact, fits on a single phone screen without scrolling.
 * - Sound Effects (ON/OFF) + Haptic Feedback (ON/OFF) persisted immediately.
 * - Background music completely removed.
 * - Google Account (Connect > if guest / <name> CONNECTED ✓ if connected). No Facebook references.
 * - How to Play > (compact tutorial modal).
 * - Privacy & Terms > (compact policy/terms viewer with data rights & deletion).
 * - Accessibility > (compact submodal with Reduced Motion & High Contrast).
 * - Small subtle footer: Zynpath v1.0.0.
 * - Zero developer/backend debug clutter in player UI.
 * - Offline-first, instant open.
 */
@Composable
fun SettingsModal(
    onDismiss: () -> Unit,
    onNavigateToTutorial: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    val isAuth = (authState == AuthState.AUTHENTICATED && currentSession != null && currentSession?.provider != com.zynpath.game.core.auth.model.AuthProvider.GUEST) ||
        (currentSession != null && currentSession?.provider == com.zynpath.game.core.auth.model.AuthProvider.GOOGLE && !currentSession!!.isExpired)
    val isGoogleConnecting by viewModel.isGoogleConnecting.collectAsStateWithLifecycle()
    val googleConnectError by viewModel.googleConnectError.collectAsStateWithLifecycle()
    val lastExportJson by viewModel.lastExportJson.collectAsStateWithLifecycle()

    var showGoogleConnectModal by remember { mutableStateOf(false) }
    var showAccountDetailsDialog by remember { mutableStateOf(false) }
    var showHowToPlayModal by remember { mutableStateOf(false) }
    var showPrivacyAndTermsModal by remember { mutableStateOf(false) }
    var showAccessibilityModal by remember { mutableStateOf(false) }

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showExportDataDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Intercept hardware/gesture back to dismiss nested dialogs first, then modal
    BackHandler {
        when {
            showGoogleConnectModal -> showGoogleConnectModal = false
            showAccountDetailsDialog -> showAccountDetailsDialog = false
            showHowToPlayModal -> showHowToPlayModal = false
            showPrivacyAndTermsModal -> showPrivacyAndTermsModal = false
            showAccessibilityModal -> showAccessibilityModal = false
            showPrivacyPolicyDialog -> showPrivacyPolicyDialog = false
            showTermsDialog -> showTermsDialog = false
            showExportDataDialog -> showExportDataDialog = false
            showDeleteAccountDialog -> showDeleteAccountDialog = false
            showSignOutDialog -> showSignOutDialog = false
            else -> onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC040C1E)) // Dark translucent scrim
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        // Centered Settings Modal Card
        Box(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 20.dp)
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
                    BorderStroke(
                        1.4.dp,
                        Brush.horizontalGradient(
                            listOf(
                                GameElectricCyan.copy(alpha = 0.85f),
                                GameGoldHighlight.copy(alpha = 0.65f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .glossyTopHighlight(cornerRadius = 22.dp, highlightAlpha = 0.16f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Absorb clicks so clicking inside modal card doesn't dismiss
                }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: SETTINGS + Close X
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SETTINGS",
                        style = GameTypography.screenHeading.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        ),
                        color = GameWhite
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = GameWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Sound Effects (ON/OFF)
                CompactSettingsToggleRow(
                    icon = if (preferences.isSfxEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                    iconTint = GameElectricCyan,
                    title = "Sound Effects",
                    checked = preferences.isSfxEnabled,
                    onCheckedChange = { viewModel.toggleSfx() }
                )

                CompactSettingsDivider()

                // 2. Haptic Feedback (ON/OFF)
                CompactSettingsToggleRow(
                    icon = Icons.Default.Vibration,
                    iconTint = GameGold,
                    title = "Haptic Feedback",
                    checked = preferences.isHapticsEnabled,
                    onCheckedChange = { viewModel.toggleHaptics() }
                )

                CompactSettingsDivider()

                // 3. Google Account (Connect > or <Name> CONNECTED ✓)
                CompactSettingsAccountRow(
                    isAuthenticated = isAuth,
                    displayName = currentSession?.displayName,
                    onClick = {
                        if (isAuth) {
                            showAccountDetailsDialog = true
                        } else {
                            showGoogleConnectModal = true
                        }
                    }
                )

                CompactSettingsDivider()

                // 4. How to Play >
                CompactSettingsNavRow(
                    icon = Icons.Default.School,
                    iconTint = GameGold,
                    title = "How to Play",
                    onClick = { showHowToPlayModal = true }
                )

                CompactSettingsDivider()

                // 5. Privacy & Terms >
                CompactSettingsNavRow(
                    icon = Icons.Default.Security,
                    iconTint = GameElectricCyan,
                    title = "Privacy & Terms",
                    onClick = { showPrivacyAndTermsModal = true }
                )

                CompactSettingsDivider()

                // 6. Accessibility >
                CompactSettingsNavRow(
                    icon = Icons.Default.Contrast,
                    iconTint = GameGoldHighlight,
                    title = "Accessibility",
                    onClick = { showAccessibilityModal = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // App Version Footer
                Text(
                    text = "Zynpath v${BuildConfig.VERSION_NAME}",
                    style = GameTypography.secondaryInfo.copy(
                        fontSize = 11.sp,
                        color = GameSecondaryText.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        // --- NESTED MODALS & DIALOGS ---

        // Google Connect Modal (reused from approved flow)
        if (showGoogleConnectModal) {
            GoogleConnectModal(
                onDismiss = { showGoogleConnectModal = false },
                onContinueWithGoogle = {
                    viewModel.connectWithGoogle(context) {
                        showGoogleConnectModal = false
                    }
                },
                isLoading = isGoogleConnecting,
                errorMessage = googleConnectError,
                onRetry = {
                    viewModel.connectWithGoogle(context) {
                        showGoogleConnectModal = false
                    }
                }
            )
        }

        // Account Details Dialog (when connected)
        if (showAccountDetailsDialog && currentSession != null) {
            val session = currentSession!!
            AlertDialog(
                onDismissRequest = { showAccountDetailsDialog = false },
                title = {
                    Text(
                        text = "Google Account",
                        color = GameWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Connected as: ${session.displayName}",
                            color = GameWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Zynpath ID: ${session.publicZynpathId}",
                            color = GameElectricCyan,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Your progress and cloud backup are synced with your Google Account.",
                            color = GameSecondaryText,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showAccountDetailsDialog = false
                            showSignOutDialog = true
                        }
                    ) {
                        Text("Sign Out", color = Color(0xFFFF5252), fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAccountDetailsDialog = false }) {
                        Text("Close", color = GameSecondaryText)
                    }
                },
                containerColor = Color(0xF207142D),
                shape = GameShapes.dialog
            )
        }

        // Sign Out Confirmation Dialog
        if (showSignOutDialog) {
            AlertDialog(
                onDismissRequest = { showSignOutDialog = false },
                title = {
                    Text(
                        text = "Sign Out?",
                        color = GameWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Signing out will return you to Guest mode. Your progress remains saved online with your Google Account.",
                        color = GameSecondaryText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.signOut()
                            showSignOutDialog = false
                        }
                    ) {
                        Text("Sign Out", color = GameGoldHighlight, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSignOutDialog = false }) {
                        Text("Cancel", color = GameSecondaryText)
                    }
                },
                containerColor = Color(0xF207142D),
                shape = GameShapes.dialog
            )
        }

        // How to Play Modal (Compact Rules)
        if (showHowToPlayModal) {
            CompactHowToPlayModal(
                onDismiss = { showHowToPlayModal = false },
                onLaunchTutorial = {
                    showHowToPlayModal = false
                    onDismiss()
                    onNavigateToTutorial()
                }
            )
        }

        // Privacy & Terms Submodal
        if (showPrivacyAndTermsModal) {
            CompactPrivacyAndTermsModal(
                isAuthenticated = isAuth,
                onDismiss = { showPrivacyAndTermsModal = false },
                onOpenPrivacyPolicy = { showPrivacyPolicyDialog = true },
                onOpenTerms = { showTermsDialog = true },
                onExportData = {
                    viewModel.requestDataExport()
                    showExportDataDialog = true
                },
                onDeleteAccount = { showDeleteAccountDialog = true }
            )
        }

        // Accessibility Submodal (Reduced Motion & High Contrast)
        if (showAccessibilityModal) {
            CompactAccessibilityModal(
                isReducedMotion = preferences.isReducedMotion,
                isHighContrast = preferences.isHighContrast,
                onToggleReducedMotion = { viewModel.toggleReducedMotion() },
                onToggleHighContrast = { viewModel.toggleHighContrast() },
                onDismiss = { showAccessibilityModal = false }
            )
        }

        // Privacy Policy Viewer Dialog
        if (showPrivacyPolicyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyPolicyDialog = false },
                title = {
                    Text(
                        text = "Privacy Policy Summary",
                        color = GameWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Zynpath is built on a guest-first, offline-first foundation.\n\n" +
                                    "1. DATA MINIMIZATION & GUEST PLAY:\n" +
                                    "• Solo gameplay, level stars, and Daily Challenge progress are saved locally on your device.\n" +
                                    "• You can play campaign levels offline without an account.\n\n" +
                                    "2. OPTIONAL GOOGLE ACCOUNT:\n" +
                                    "• Linking a Google Account creates a cloud backup and enables multiplayer with friends.\n" +
                                    "• We store your public Zynpath ID, chosen display name, and avatar index. We do not sell your personal data.\n\n" +
                                    "3. GOOGLE PLAY BILLING:\n" +
                                    "• Purchases are processed securely by Google Play. We verify purchase tokens to unlock ad-free play and themes.\n\n" +
                                    "4. DATA RIGHTS & DELETION:\n" +
                                    "• You can export your data or permanently delete your account at any time in Settings.",
                            fontSize = 13.sp,
                            color = GameSecondaryText,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                        Text("Close", color = GameElectricCyan, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xF207142D),
                shape = GameShapes.dialog
            )
        }

        // Terms of Service Viewer Dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = {
                    Text(
                        text = "Terms of Service Summary",
                        color = GameWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "1. FAIR PLAY & INTEGRITY:\n" +
                                    "• Players must not utilize automated bots, network injection tools, or exploits.\n\n" +
                                    "2. MULTIPLAYER CONDUCT:\n" +
                                    "• Multiplayer matches and room codes are designed for positive, competitive puzzle solving.\n\n" +
                                    "3. SUBSCRIPTIONS & PURCHASES:\n" +
                                    "• Premium features and purchases are billed through Google Play in accordance with Google Play Terms.",
                            fontSize = 13.sp,
                            color = GameSecondaryText,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text("Close", color = GameElectricCyan, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xF207142D),
                shape = GameShapes.dialog
            )
        }

        // Data Export Viewer Dialog (Section 21)
        if (showExportDataDialog) {
            AlertDialog(
                onDismissRequest = {
                    showExportDataDialog = false
                    viewModel.clearExportJson()
                },
                title = {
                    Text(
                        text = "Account Data Export",
                        color = GameWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = lastExportJson ?: "Generating export payload...",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = GameSecondaryText,
                            lineHeight = 15.sp
                        )
                    }
                },
                confirmButton = {
                    Row {
                        if (!lastExportJson.isNullOrBlank()) {
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(lastExportJson!!))
                                }
                            ) {
                                Text("Copy JSON", color = GameGoldHighlight, fontWeight = FontWeight.Bold)
                            }
                        }
                        TextButton(
                            onClick = {
                                showExportDataDialog = false
                                viewModel.clearExportJson()
                            }
                        ) {
                            Text("Done", color = GameElectricCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                containerColor = Color(0xF207142D),
                shape = GameShapes.dialog
            )
        }

        // Delete Account Confirmation Dialog
        if (showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAccountDialog = false },
                title = {
                    Text(
                        text = "Delete Cloud Account?",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "This will permanently delete your online account, stats, and cloud saves from Zynpath servers. Local offline levels on this device will not be deleted.",
                        color = GameSecondaryText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAccount {
                                showDeleteAccountDialog = false
                                showPrivacyAndTermsModal = false
                            }
                        }
                    ) {
                        Text("Delete", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccountDialog = false }) {
                        Text("Cancel", color = GameSecondaryText)
                    }
                },
                containerColor = Color(0xF207142D),
                shape = GameShapes.dialog
            )
        }
    }
}

// --- COMPACT ROW COMPONENTS ---

@Composable
private fun CompactSettingsToggleRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = GameTypography.bodyMedium,
                color = GameWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GameWhite,
                checkedTrackColor = GameElectricCyan,
                uncheckedThumbColor = GameSecondaryText,
                uncheckedTrackColor = Color(0xFF162544)
            )
        )
    }
}

@Composable
private fun CompactSettingsAccountRow(
    isAuthenticated: Boolean,
    displayName: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GameElectricCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Google Account",
                    style = GameTypography.bodyMedium,
                    color = GameWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isAuthenticated && !displayName.isNullOrBlank()) {
                    Text(
                        text = displayName,
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (isAuthenticated) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3320D76B))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "CONNECTED ✓",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameSuccessGreen
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x2A21D4FD))
                    .border(1.dp, GameElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "CONNECT >",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameElectricCyan
                )
            }
        }
    }
}

@Composable
private fun CompactSettingsNavRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = GameTypography.bodyMedium,
                color = GameWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = ">",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = GameSecondaryText.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun CompactSettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 0.8.dp,
        color = Color(0x2621D4FD)
    )
}

// --- COMPACT HOW TO PLAY MODAL ---

@Composable
private fun CompactHowToPlayModal(
    onDismiss: () -> Unit,
    onLaunchTutorial: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HOW TO PLAY",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GameWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RuleBulletPoint(
                    number = "1",
                    title = "Connect in Order",
                    description = "Draw paths connecting numbered checkpoints sequentially (1 → 2 → 3 ... → N)."
                )
                RuleBulletPoint(
                    number = "2",
                    title = "Continuous Path",
                    description = "Move through adjacent vertical and horizontal cells. Avoid crossing crimson walls."
                )
                RuleBulletPoint(
                    number = "3",
                    title = "Cover Every Cell",
                    description = "Fill all available grid cells to solve the puzzle and earn 3 stars!"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onLaunchTutorial) {
                Text(
                    text = "Play Tutorial →",
                    color = GameElectricCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got It",
                    color = GameSecondaryText,
                    fontSize = 14.sp
                )
            }
        },
        containerColor = Color(0xF207142D),
        shape = GameShapes.dialog
    )
}

@Composable
private fun RuleBulletPoint(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(GameElectricCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = GameElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = GameWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = GameSecondaryText,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

// --- COMPACT PRIVACY & TERMS MODAL ---

@Composable
private fun CompactPrivacyAndTermsModal(
    isAuthenticated: Boolean,
    onDismiss: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit,
    onExportData: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRIVACY & TERMS",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GameWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LegalActionRow(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    subtitle = "Offline-first data protection",
                    onClick = onOpenPrivacyPolicy
                )
                LegalActionRow(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    subtitle = "Fair play and conditions",
                    onClick = onOpenTerms
                )
                if (isAuthenticated) {
                    LegalActionRow(
                        icon = Icons.Default.Download,
                        title = "Request My Data (Export)",
                        subtitle = "Download account JSON",
                        onClick = onExportData
                    )
                    LegalActionRow(
                        icon = Icons.Default.DeleteForever,
                        iconTint = Color(0xFFFF5252),
                        title = "Delete Cloud Account",
                        subtitle = "Permanently remove online data",
                        onClick = onDeleteAccount
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = GameElectricCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xF207142D),
        shape = GameShapes.dialog
    )
}

@Composable
private fun LegalActionRow(
    icon: ImageVector,
    iconTint: Color = GameElectricCyan,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33101D3C))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = GameWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = GameSecondaryText,
                    fontSize = 11.sp
                )
            }
        }
        Text(
            text = ">",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GameSecondaryText.copy(alpha = 0.6f)
        )
    }
}

// --- COMPACT ACCESSIBILITY MODAL ---

@Composable
private fun CompactAccessibilityModal(
    isReducedMotion: Boolean,
    isHighContrast: Boolean,
    onToggleReducedMotion: () -> Unit,
    onToggleHighContrast: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACCESSIBILITY",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GameWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactSettingsToggleRow(
                    icon = Icons.Default.MotionPhotosOff,
                    iconTint = GameGoldHighlight,
                    title = "Reduced Motion",
                    checked = isReducedMotion,
                    onCheckedChange = { onToggleReducedMotion() }
                )
                CompactSettingsDivider()
                CompactSettingsToggleRow(
                    icon = Icons.Default.Contrast,
                    iconTint = GameElectricCyan,
                    title = "High Contrast Mode",
                    checked = isHighContrast,
                    onCheckedChange = { onToggleHighContrast() }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = GameElectricCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xF207142D),
        shape = GameShapes.dialog
    )
}
