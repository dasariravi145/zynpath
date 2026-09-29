package com.zynpath.game.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.auth.model.AuthProvider
import com.zynpath.game.core.auth.model.AuthState
import com.zynpath.game.core.designsystem.components.GameLoadingIndicator
import com.zynpath.game.core.designsystem.components.GamePanel
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.theme.GameBorders
import com.zynpath.game.core.designsystem.theme.GameBrushes
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameShapes
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.auth.components.AnimatedPuzzlePathIllustration
import com.zynpath.game.feature.auth.components.FacebookSignInButton
import com.zynpath.game.feature.auth.components.GoogleSignInButton
import com.zynpath.game.feature.splash.components.ZynpathCinematicLogo

/**
 * Premium mobile game welcome and authentication screen for Zynpath.
 *
 * Implements Prompt 03/24:
 * - TOP: Original ZYNPATH game logo, NUMBER PATH PUZZLE subtitle, atmospheric glowing background.
 * - CENTER: Animated puzzle-world illustration with floating numbered tiles and glowing path connections.
 * - LOWER: Three distinct entry options:
 *     1. PRIMARY: "PLAY AS GUEST" via [GamePrimaryButton] (instant offline play, preserves local progress).
 *     2. SECONDARY: "CONTINUE WITH FACEBOOK" with recognizable brand presentation and setup pending state.
 *     3. SECONDARY: "CONTINUE WITH GOOGLE" with Credential Manager integration and setup pending state.
 * - FOOTER: Terms & Privacy links, legal reassurance, app version metadata.
 * - Preserves existing authentication contracts, session restoration, and guest progress.
 */
@Composable
fun SignInScreen(
    onNavigateBack: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onSignInSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToPrivacy: (() -> Unit)? = null,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.authState) {
        if (uiState.authState == AuthState.AUTHENTICATED) {
            onSignInSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    // Account Linking Conflict Dialog
    if (uiState.conflictDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConflictDialog() },
            shape = GameShapes.dialog,
            containerColor = Color(0xF80B1736),
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = GameGoldHighlight,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Account Conflict",
                    style = GameTypography.screenHeading,
                    color = GameWhite
                )
            },
            text = {
                Text(
                    text = uiState.conflictDialogMessage ?: "",
                    style = GameTypography.secondaryInfo,
                    color = GameSecondaryText
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissConflictDialog() }) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.Bold,
                        color = GameElectricCyan
                    )
                }
            }
        )
    }

    // Unconfigured Provider Notice Dialog
    if (uiState.unconfiguredProviderNotice != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUnconfiguredNotice() },
            shape = GameShapes.dialog,
            containerColor = Color(0xF80B1736),
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GameElectricCyan,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Provider Setup Pending",
                    style = GameTypography.screenHeading,
                    color = GameWhite
                )
            },
            text = {
                Column {
                    Text(
                        text = uiState.unconfiguredProviderNotice ?: "",
                        style = GameTypography.secondaryInfo,
                        color = GameSecondaryText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Solo Play, level progression, and Daily Challenges are 100% playable offline without signing in.",
                        style = GameTypography.secondaryInfo.copy(fontSize = 11.sp),
                        color = GameGoldHighlight
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissUnconfiguredNotice() }) {
                    Text(
                        text = "Understood",
                        fontWeight = FontWeight.Bold,
                        color = GameElectricCyan
                    )
                }
            }
        )
    }

    GameScreenBackground(
        showCelestialParticles = true,
        isReducedMotion = uiState.isReducedMotion
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP SECTION: Back Navigation & ZYNPATH Game Logo
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Back Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GameRoyalBlue.copy(alpha = 0.35f))
                                .border(1.dp, GameElectricCyan.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = GameWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Original ZYNPATH Game Logo & Subtitle
                    ZynpathCinematicLogo(
                        isReducedMotion = uiState.isReducedMotion
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // CENTER SECTION: Reference 3D Characters Artwork
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg_login_hero_clean),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // LOWER SECTION: Action Buttons matching Reference Panel 02
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. CONTINUE WITH FACEBOOK BUTTON (Solid Blue Pill)
                    com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                        text = "Continue with Facebook",
                        onClick = { viewModel.signInWithFacebook(context) },
                        style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.FACEBOOK,
                        enabled = !uiState.isLoading,
                        height = 52.dp,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_brand_facebook),
                                contentDescription = "Facebook",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )

                    // 2. PLAY AS GUEST BUTTON (White / Silver Pill)
                    com.zynpath.game.core.designsystem.components.ZynpathMasterPillButton(
                        text = "Play as Guest",
                        onClick = onContinueAsGuest,
                        style = com.zynpath.game.core.designsystem.components.ZynpathPillStyle.SILVER,
                        enabled = !uiState.isLoading,
                        height = 52.dp,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Guest",
                                tint = Color(0xFF0D1B2A),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FOOTER: Reference Wording
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Play with friends • Save progress",
                        color = Color(0xFF8E9EB5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Compete • Win rewards",
                        color = Color(0xFF8E9EB5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FOOTER: Terms & Privacy Links, Legal Notice, and App Version
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Terms of Service",
                            style = GameTypography.secondaryInfo.copy(
                                fontSize = 11.sp,
                                color = GameElectricCyan,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable {
                                onNavigateToPrivacy?.invoke()
                            }
                        )

                        Text(
                            text = " • ",
                            style = GameTypography.secondaryInfo.copy(fontSize = 11.sp, color = GameSecondaryText)
                        )

                        Text(
                            text = "Privacy Policy",
                            style = GameTypography.secondaryInfo.copy(
                                fontSize = 11.sp,
                                color = GameElectricCyan,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable {
                                onNavigateToPrivacy?.invoke()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Zynpath v1.0.0 • Progress is safely preserved to this device",
                        style = GameTypography.secondaryInfo.copy(
                            fontSize = 10.sp,
                            color = GameSecondaryText.copy(alpha = 0.55f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // FULL-SCREEN LOADING OVERLAY (During Provider Exchange)
            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xCC07142D)),
                    contentAlignment = Alignment.Center
                ) {
                    GameLoadingIndicator(
                        size = 56.dp,
                        message = if (uiState.authState == AuthState.LINKING) "Linking Account..." else "Connecting to Server..."
                    )
                }
            }

            // Snackbar Host for Error Notifications
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            )
        }
    }
}
