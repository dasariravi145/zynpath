package com.zynpath.game.feature.settings

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.BuildConfig
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.billing.SubscriptionConstants
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.components.GameTopBar
import com.zynpath.game.core.designsystem.theme.GameBrightBlue
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.diagnostics.ConnectivityDiagnosticsSection

/**
 * Premium Zynpath Game Settings Screen.
 *
 * Implements Prompt 11/24 focused UI redesign:
 * - Deep navy fantasy environment with atmospheric starfield
 * - Royal-blue glassmorphism settings panels with glowing borders
 * - Electric-cyan selected switch & slider states
 * - Gold section accents & badges
 * - Subtle original number-path header decoration
 * - Accessible controls, clear contrast, and system insets respect
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCosmetics: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToTutorial: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()
    val linkedProviders by viewModel.linkedProviders.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val exportJson by viewModel.lastExportJson.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var providerToUnlink by remember { mutableStateOf<String?>(null) }
    var touchSensitivitySlider by remember(preferences.touchSensitivity) {
        mutableFloatStateOf(preferences.touchSensitivity)
    }

    val timePickerDialog = remember(context, preferences.dailyReminderHour, preferences.dailyReminderMinute) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                viewModel.setDailyReminderTime(hourOfDay, minute)
            },
            preferences.dailyReminderHour,
            preferences.dailyReminderMinute,
            false
        )
    }

    GameScreenBackground(
        modifier = Modifier.fillMaxSize(),
        isReducedMotion = preferences.isReducedMotion
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                GameTopBar(
                    title = "",
                    onBackClick = onBackClick,
                    actions = {
                        MiniNumberPathDecoration()
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Prominent page title and subtitle positioned safely in scrollable content
                Text(
                    text = "SETTINGS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = GameWhite,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Preferences & Account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = GameSecondaryText
                )
                Spacer(modifier = Modifier.height(18.dp))
                // ==========================================
                // 1. GAME PREFERENCES (AUDIO & HAPTICS)
                // ==========================================
                SectionHeader(
                    title = "AUDIO & HAPTICS",
                    icon = Icons.AutoMirrored.Filled.VolumeUp
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsToggleRow(
                        title = "Sound Effects",
                        subtitle = "Clicks, path drawing audio & checkpoint chimes",
                        checked = preferences.isSfxEnabled,
                        onCheckedChange = { viewModel.toggleSfx() },
                        icon = Icons.AutoMirrored.Filled.VolumeUp
                    )
                    GameRowDivider()
                    GameSettingsToggleRow(
                        title = "Background Music",
                        subtitle = "Ambient puzzle soundscape",
                        checked = preferences.isMusicEnabled,
                        onCheckedChange = { viewModel.toggleMusic() },
                        icon = Icons.Default.MusicNote
                    )
                    GameRowDivider()
                    GameSettingsToggleRow(
                        title = "Haptic Feedback",
                        subtitle = "Tactile vibration pulses on cell steps & resets",
                        checked = preferences.isHapticsEnabled,
                        onCheckedChange = { viewModel.toggleHaptics() },
                        icon = Icons.Default.Vibration
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 2. CONTROLS & ACCESSIBILITY
                // ==========================================
                SectionHeader(
                    title = "CONTROLS & VISUALS",
                    icon = Icons.Default.TouchApp
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsToggleRow(
                        title = "Reduced Motion",
                        subtitle = "Minimize particle bursts and pulse animations",
                        checked = preferences.isReducedMotion,
                        onCheckedChange = { viewModel.toggleReducedMotion() },
                        icon = Icons.Default.MotionPhotosOff
                    )
                    GameRowDivider()
                    GameSettingsToggleRow(
                        title = "High Contrast Mode",
                        subtitle = "Enhance board borders and tile visibility",
                        checked = preferences.isHighContrast,
                        onCheckedChange = { viewModel.toggleHighContrast() },
                        icon = Icons.Default.Contrast
                    )
                    GameRowDivider()
                    GameSettingsToggleRow(
                        title = "Tap Input Mode",
                        subtitle = "Tap consecutive cells instead of continuous dragging",
                        checked = preferences.isTapInputMode,
                        onCheckedChange = { viewModel.toggleTapInputMode() },
                        icon = Icons.Default.TouchApp
                    )
                    GameRowDivider()
                    GameSettingsSliderRow(
                        title = "Touch Sensitivity",
                        subtitle = "Adjust path dragging responsiveness",
                        value = touchSensitivitySlider,
                        onValueChange = { touchSensitivitySlider = it },
                        onValueChangeFinished = { viewModel.setTouchSensitivity(touchSensitivitySlider) }
                    )
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Cosmetics & Themes",
                        subtitle = "Board themes, path effects & avatar frames",
                        detail = "Customize →",
                        icon = Icons.Default.Palette,
                        onClick = onNavigateToCosmetics
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 3. NOTIFICATIONS & REMINDERS
                // ==========================================
                SectionHeader(
                    title = "NOTIFICATIONS & REMINDERS",
                    icon = Icons.Default.Notifications
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsToggleRow(
                        title = "Daily Challenge Reminder",
                        subtitle = "Local alert when today's puzzle is waiting",
                        checked = preferences.isDailyReminderEnabled,
                        onCheckedChange = { viewModel.toggleDailyReminder() },
                        icon = Icons.Default.Alarm
                    )
                    if (preferences.isDailyReminderEnabled) {
                        GameRowDivider()
                        GameSettingsClickableRow(
                            title = "Reminder Time",
                            subtitle = "Tap to set scheduled reminder time",
                            detail = String.format(
                                java.util.Locale.getDefault(),
                                "%02d:%02d (Local Time) ✎",
                                preferences.dailyReminderHour,
                                preferences.dailyReminderMinute
                            ),
                            detailColor = GameGoldHighlight,
                            onClick = { timePickerDialog.show() }
                        )
                    }
                    GameRowDivider()
                    GameSettingsToggleRow(
                        title = "Friend Alerts",
                        subtitle = "Social invitations & duel notifications",
                        checked = preferences.isFriendAlertsEnabled,
                        onCheckedChange = { viewModel.toggleFriendAlerts() },
                        icon = Icons.Default.People
                    )
                    GameRowDivider()
                    GameSettingsToggleRow(
                        title = "Multiplayer Alerts",
                        subtitle = "Duel invites, Mini League rooms & match outcomes",
                        checked = preferences.isMultiplayerAlertsEnabled,
                        onCheckedChange = { viewModel.toggleMultiplayerAlerts() },
                        icon = Icons.Default.SportsEsports
                    )
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Notification Center",
                        subtitle = "Review alert inbox and history",
                        detail = "Open Inbox →",
                        icon = Icons.Default.Notifications,
                        onClick = onNavigateToNotifications
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 4. ACCOUNT & PROFILE
                // ==========================================
                SectionHeader(
                    title = "ACCOUNT & SECURITY",
                    icon = Icons.Default.Person
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsClickableRow(
                        title = "Player Profile",
                        subtitle = "View stats, avatars, and achievements",
                        detail = "View Profile →",
                        icon = Icons.Default.Person,
                        detailColor = GameGoldHighlight,
                        onClick = onNavigateToProfile
                    )
                    GameRowDivider()

                    val isAuthenticated = authState == AuthState.AUTHENTICATED && currentSession != null
                    if (!isAuthenticated) {
                        GameSettingsInfoRow(
                            title = "Account Status",
                            subtitle = "Local storage on this device",
                            value = "Guest Mode",
                            icon = Icons.Default.Link,
                            valueColor = GameGoldHighlight
                        )
                        GameRowDivider()
                        GameSettingsClickableRow(
                            title = "Link Cloud Account",
                            subtitle = "Connect Google or Facebook to backup progress",
                            detail = "Sign In →",
                            icon = Icons.Default.Link,
                            onClick = onNavigateToSignIn
                        )
                    } else {
                        val session = currentSession!!
                        GameSettingsInfoRow(
                            title = "Player Name",
                            value = session.displayName,
                            icon = Icons.Default.Person,
                            valueColor = GameWhite
                        )
                        GameRowDivider()
                        GameSettingsInfoRow(
                            title = "Zynpath ID",
                            value = session.publicZynpathId,
                            valueColor = GameElectricCyan
                        )
                        GameRowDivider()
                        GameSettingsInfoRow(
                            title = "Account Status",
                            value = "Linked & Verified",
                            valueColor = GameElectricCyan
                        )
                        GameRowDivider()

                        if (linkedProviders.isNotEmpty()) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text(
                                    text = "Linked Providers",
                                    style = GameTypography.labelMedium,
                                    color = GameWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                linkedProviders.forEach { provider ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "• $provider", style = GameTypography.secondaryInfo, color = GameSecondaryText)
                                        if (linkedProviders.size > 1) {
                                            Text(
                                                text = "Unlink",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GameGoldHighlight,
                                                modifier = Modifier.clickable { providerToUnlink = provider }
                                            )
                                        }
                                    }
                                }
                            }
                            GameRowDivider()
                        }

                        GameSettingsClickableRow(
                            title = "Sign Out",
                            subtitle = "Switch session back to Guest mode",
                            detail = "Sign Out →",
                            icon = Icons.AutoMirrored.Filled.Logout,
                            detailColor = GameGold,
                            onClick = { showSignOutDialog = true }
                        )
                        GameRowDivider()
                        GameSettingsClickableRow(
                            title = "Delete Cloud Account",
                            subtitle = "Permanently delete account from cloud servers",
                            detail = "Delete →",
                            icon = Icons.Default.DeleteForever,
                            detailColor = Color(0xFFFF5252),
                            onClick = { showDeleteAccountDialog = true }
                        )
                        GameRowDivider()
                    }

                    GameSettingsClickableRow(
                        title = "Clear Disposable Cache",
                        subtitle = "Free local temporary storage",
                        detail = "Clean Cache",
                        icon = Icons.Default.CleaningServices,
                        onClick = { viewModel.clearCache() }
                    )
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Request My Data (Export)",
                        subtitle = "Download portable JSON archive",
                        detail = "Export",
                        icon = Icons.Default.Download,
                        onClick = { viewModel.requestDataExport() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 5. PREMIUM PASS
                // ==========================================
                SectionHeader(
                    title = "PREMIUM PASS",
                    icon = Icons.Default.WorkspacePremium
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsInfoRow(
                        title = "Subscription Status",
                        value = if (preferences.isPremium) "Active (Pass Enabled)" else "Free Player",
                        icon = Icons.Default.WorkspacePremium,
                        valueColor = if (preferences.isPremium) GameGoldHighlight else GameSecondaryText
                    )
                    GameRowDivider()
                    if (preferences.isPremium) {
                        GameSettingsClickableRow(
                            title = "Manage Subscription",
                            subtitle = "Manage recurring membership on Google Play",
                            detail = "Open Play Store →",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SubscriptionConstants.getManageSubscriptionUrl()))
                                try { context.startActivity(intent) } catch (e: Exception) {}
                            }
                        )
                    } else {
                        GameSettingsClickableRow(
                            title = "Upgrade to Premium",
                            subtitle = "Ad-free play, exclusive themes & unlimited hints",
                            detail = "View Plans →",
                            detailColor = GameGoldHighlight,
                            onClick = onNavigateToPremium
                        )
                    }
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Restore Purchases",
                        subtitle = "Check Google Play for existing entitlements",
                        detail = "Restore →",
                        onClick = { viewModel.restorePurchases() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 6. PRIVACY & DISCOVERY
                // ==========================================
                SectionHeader(
                    title = "PRIVACY & DISCOVERY",
                    icon = Icons.Default.Security
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsInfoRow(
                        title = "Profile Visibility",
                        subtitle = "Social discovery mode",
                        value = preferences.profileVisibility,
                        icon = Icons.Default.Security,
                        valueColor = GameWhite
                    )
                    GameRowDivider()
                    GameSettingsInfoRow(
                        title = "Zynpath ID Search",
                        value = if (preferences.allowZynpathIdSearch) "Allowed" else "Hidden",
                        valueColor = if (preferences.allowZynpathIdSearch) GameElectricCyan else GameSecondaryText
                    )
                    GameRowDivider()
                    GameSettingsInfoRow(
                        title = "Friend Requests",
                        value = if (preferences.allowFriendRequests) "Open" else "Disabled",
                        valueColor = if (preferences.allowFriendRequests) GameElectricCyan else GameSecondaryText
                    )
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Manage Privacy & Blocked Users",
                        subtitle = "Custom permissions and blocked player list",
                        detail = "Open Controls →",
                        onClick = onNavigateToPrivacy
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 7. HELP & INFORMATION
                // ==========================================
                SectionHeader(
                    title = "HELP & INFORMATION",
                    icon = Icons.Default.Info
                )
                GamePanel(contentPadding = 4.dp) {
                    GameSettingsClickableRow(
                        title = "How to Play (Interactive Tutorial)",
                        subtitle = "Rules, wall mechanics & practice puzzles",
                        detail = "Play Tutorial →",
                        icon = Icons.Default.School,
                        detailColor = GameElectricCyan,
                        onClick = onNavigateToTutorial
                    )
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Privacy Policy",
                        subtitle = "Guest-first, offline-first data protection",
                        detail = "Read Policy →",
                        icon = Icons.Default.Security,
                        onClick = { showPrivacyPolicyDialog = true }
                    )
                    GameRowDivider()
                    GameSettingsClickableRow(
                        title = "Terms of Service",
                        subtitle = "Fair play rules & subscription terms",
                        detail = "Read Terms →",
                        icon = Icons.Default.Description,
                        onClick = { showTermsDialog = true }
                    )
                    GameRowDivider()
                    GameSettingsInfoRow(
                        title = "App Version",
                        subtitle = "Zynpath Puzzle Engine v1.0 • Offline-First",
                        value = "${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                        icon = Icons.Default.Info,
                        valueColor = GameGoldHighlight
                    )
                }

                // Diagnostics (Debug builds only)
                if (BuildConfig.DEBUG) {
                    Spacer(modifier = Modifier.height(20.dp))
                    ConnectivityDiagnosticsSection()
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }

    // --- GAME STYLED DIALOGS ---

    // Sign Out Dialog
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
                    text = "Signing out will clear active account credentials on this device and return you to Guest mode. Your server progress remains safe and can be accessed by signing in again.",
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

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Permanently Delete Account?",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "This action is permanent and cannot be undone.\n\n" +
                                "• All server profiles, friendships, and invitations will be erased.\n" +
                                "• Solo progress associated with this account will be purged.\n\n" +
                                "IMPORTANT NOTICE ON SUBSCRIPTIONS:\n" +
                                "Deleting your Zynpath account does NOT automatically cancel recurring subscriptions managed through Google Play. " +
                                "You must cancel any active subscription directly in the Google Play Store to prevent future renewal charges.",
                        fontSize = 13.sp,
                        color = GameSecondaryText,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount()
                        showDeleteAccountDialog = false
                    }
                ) {
                    Text("Delete Account", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
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

    // Privacy Policy In-App Viewer Dialog
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
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Zynpath: Number Path Puzzle is built on a guest-first, offline-first philosophy.\n\n" +
                                "1. DATA MINIMIZATION & GUEST PLAY:\n" +
                                "• Solo gameplay, level stars, and Daily Challenge progress are saved locally on your device.\n" +
                                "• You can play 100% of Solo campaign levels offline without creating an account.\n\n" +
                                "2. OPTIONAL ONLINE ACCOUNTS:\n" +
                                "• Linking Google or Facebook creates a cloud profile to back up progress and participate in online multiplayer (Quick Duel, Friend Duel, Mini League).\n" +
                                "• We store your public Zynpath ID, chosen display name, and avatar index. We do not sell your personal data.\n\n" +
                                "3. GOOGLE PLAY BILLING:\n" +
                                "• Premium subscriptions are processed securely by Google Play. We verify purchase tokens to unlock ad-free play and cosmetic themes.\n\n" +
                                "4. OPTIONAL REWARDED ADS:\n" +
                                "• Free players may optionally watch rewarded ads via Google Mobile Ads to earn bonus hints. Ads are never forced.\n\n" +
                                "5. ACCOUNT DELETION & DATA RIGHTS:\n" +
                                "• You can permanently delete your cloud account at any time in Settings. An external web deletion portal is also available without reinstalling the app.\n\n" +
                                "Full policy draft: docs/PRIVACY_POLICY_DRAFT.md",
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

    // Terms of Service In-App Viewer Dialog
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
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Welcome to Zynpath: Number Path Puzzle.\n\n" +
                                "1. FAIR PLAY & COMPETITIVE INTEGRITY:\n" +
                                "• Multiplayer modes (Quick Duel, Friend Duel, Mini League) use server-authoritative puzzle validation.\n" +
                                "• Automated bots, modified clients, and unauthorized scripts are strictly prohibited.\n" +
                                "• Hints are disabled in all competitive modes to ensure absolute fairness.\n\n" +
                                "2. VIRTUAL ITEMS & SUBSCRIPTIONS:\n" +
                                "• Optional Premium subscriptions grant cosmetic themes, avatar frames, and ad-free Solo play. They do not grant competitive advantages.\n" +
                                "• Subscriptions renew automatically through Google Play unless cancelled at least 24 hours before the end of the current period.\n\n" +
                                "3. PRESET COMMUNICATION:\n" +
                                "• In-game communication is restricted to preset positive emojis and phrases to maintain a safe, welcoming environment.\n\n" +
                                "Official Terms document: docs/STORE_COMPLIANCE_CHECKLIST.md",
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

    // Unlink Provider Dialog
    providerToUnlink?.let { provider ->
        AlertDialog(
            onDismissRequest = { providerToUnlink = null },
            title = {
                Text(
                    text = "Unlink $provider?",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to unlink $provider from your account? You will need your other linked provider to sign in.",
                    color = GameSecondaryText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.unlinkProvider(provider)
                        providerToUnlink = null
                    }
                ) {
                    Text("Unlink", color = GameGoldHighlight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { providerToUnlink = null }) {
                    Text("Cancel", color = GameSecondaryText)
                }
            },
            containerColor = Color(0xF207142D),
            shape = GameShapes.dialog
        )
    }

    // Data Export Display Dialog
    exportJson?.let { json ->
        AlertDialog(
            onDismissRequest = { viewModel.clearExportJson() },
            title = {
                Text(
                    text = "Data Export (JSON)",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your account data export has been prepared in portable JSON format. You can copy it to your clipboard.",
                        fontSize = 13.sp,
                        color = GameSecondaryText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GameDeepNavy)
                            .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (json.length > 300) json.take(300) + "..." else json,
                            fontSize = 11.sp,
                            color = GameWhite
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(json))
                        viewModel.clearExportJson()
                    }
                ) {
                    Text("Copy JSON", color = GameElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearExportJson() }) {
                    Text("Close", color = GameSecondaryText)
                }
            },
            containerColor = Color(0xF207142D),
            shape = GameShapes.dialog
        )
    }

    // UI Message Feedback Dialog
    uiMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissMessage() },
            title = {
                Text(
                    text = "Notice",
                    color = GameWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = { Text(text = msg, color = GameSecondaryText, fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissMessage() }) {
                    Text("OK", color = GameElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xF207142D),
            shape = GameShapes.dialog
        )
    }
}

// ==========================================
// REUSABLE PREMIUM GAME SETTINGS COMPONENTS
// ==========================================

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(start = 4.dp, bottom = 8.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GameGoldHighlight,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GameGoldHighlight,
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
private fun GameSettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null,
    iconTint: Color = GameElectricCyan,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {
                role = Role.Switch
                contentDescription = "$title: ${if (checked) "On" else "Off"}. $subtitle"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33154A98))
                        .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (checked) iconTint else GameSecondaryText.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = GameTypography.labelMedium,
                    color = GameWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = GameTypography.secondaryInfo,
                    color = GameSecondaryText
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GameWhite,
                checkedTrackColor = GameElectricCyan,
                checkedBorderColor = GameElectricCyan,
                uncheckedThumbColor = GameSecondaryText.copy(alpha = 0.7f),
                uncheckedTrackColor = GameDeepNavy,
                uncheckedBorderColor = GameRoyalBlue.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun GameSettingsClickableRow(
    title: String,
    detail: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = GameElectricCyan,
    detailColor: Color = GameElectricCyan,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = "$title. ${subtitle ?: ""}. $detail"
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33154A98))
                        .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = GameTypography.labelMedium,
                    color = GameWhite,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = detail,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = detailColor
        )
    }
}

@Composable
private fun GameSettingsInfoRow(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    valueColor: Color = GameSecondaryText,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x33154A98))
                        .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GameElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = GameTypography.labelMedium,
                    color = GameWhite,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = GameTypography.secondaryInfo,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun GameSettingsSliderRow(
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0.5f..2.0f,
    steps: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = GameTypography.labelMedium,
                color = GameWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${String.format(java.util.Locale.US, "%.1f", value)}x",
                fontSize = 14.sp,
                color = GameElectricCyan,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = subtitle,
            style = GameTypography.secondaryInfo,
            color = GameSecondaryText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = GameElectricCyan,
                activeTrackColor = GameElectricCyan,
                inactiveTrackColor = Color(0xFF0C1735),
                activeTickColor = GameDeepNavy,
                inactiveTickColor = GameRoyalBlue.copy(alpha = 0.6f)
            )
        )
    }
}

@Composable
private fun GameRowDivider() {
    HorizontalDivider(
        color = GameRoyalBlue.copy(alpha = 0.25f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 14.dp)
    )
}

/**
 * Subtle original number-path puzzle decoration in the header action slot.
 */
@Composable
private fun MiniNumberPathDecoration() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x4407142D))
            .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        MiniNode(number = "1", isCyan = true)
        MiniPathLine(isCyan = true)
        MiniNode(number = "2", isCyan = true)
        MiniPathLine(isCyan = false)
        MiniNode(number = "3", isCyan = false)
    }
}

@Composable
private fun MiniNode(number: String, isCyan: Boolean) {
    val borderColor = if (isCyan) GameElectricCyan else GameGoldHighlight
    val textColor = if (isCyan) GameElectricCyan else GameGoldHighlight
    val bgBrush = if (isCyan) {
        Brush.radialGradient(listOf(Color(0x6621D4FD), Color(0x22101D3C)))
    } else {
        Brush.radialGradient(listOf(Color(0x66FFC247), Color(0x22101D3C)))
    }

    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(bgBrush)
            .border(1.2.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun MiniPathLine(isCyan: Boolean) {
    Box(
        modifier = Modifier
            .width(8.dp)
            .height(2.dp)
            .background(
                if (isCyan) Brush.horizontalGradient(listOf(GameElectricCyan, GameBrightBlue))
                else Brush.horizontalGradient(listOf(GameBrightBlue, GameGold))
            )
    )
}
