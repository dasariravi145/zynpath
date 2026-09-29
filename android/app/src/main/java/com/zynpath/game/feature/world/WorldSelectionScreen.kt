package com.zynpath.game.feature.world

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.animation.ZynpathAnimations.cardEntrance
import com.zynpath.game.core.designsystem.components.GamePrimaryButton
import com.zynpath.game.core.designsystem.components.GameScreenBackground
import com.zynpath.game.core.designsystem.layout.rememberZynpathWindowInfo
import com.zynpath.game.core.feedback.rememberZynpathFeedbackCoordinator
import com.zynpath.game.core.designsystem.theme.GameDeepNavy
import com.zynpath.game.core.designsystem.theme.GameElectricCyan
import com.zynpath.game.core.designsystem.theme.GameGold
import com.zynpath.game.core.designsystem.theme.GameGoldHighlight
import com.zynpath.game.core.designsystem.theme.GameMidnightBlue
import com.zynpath.game.core.designsystem.theme.GameRoyalBlue
import com.zynpath.game.core.designsystem.theme.GameSecondaryText
import com.zynpath.game.core.designsystem.theme.GameTypography
import com.zynpath.game.core.designsystem.theme.GameWhite
import com.zynpath.game.feature.world.components.GameWorldCard
import kotlinx.coroutines.launch

/**
 * Premium World Selection Screen (Prompt 05/24).
 *
 * Presents the 6 fantasy realms of Zynpath:
 * 1. Crystal Valley • Learn the Path (Levels 1–20)
 * 2. Mystic Forest • Longer Connections (Levels 21–50)
 * 3. Ember Canyon • Wall Challenge (Levels 51–100)
 * 4. Sky Islands • Complex Routes (Levels 101–150)
 * 5. Moonlight Temple • Advanced Logic (Levels 151–200)
 * 6. Celestial Summit • Expert Path (Levels 201–300)
 *
 * Features:
 * - Procedural fantasy biome illustrations and theme colors.
 * - Star counters, progression bars, and completion badges.
 * - Locked realms with clear progression requirements.
 * - Floating "CONTINUE ADVENTURE" primary action bar.
 */
@Composable
fun WorldSelectionScreen(
    onBackClick: () -> Unit,
    onSelectWorld: (worldId: Int) -> Unit,
    onNavigateToPremiumPacks: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorldSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowInfo = rememberZynpathWindowInfo()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val feedbackCoordinator = rememberZynpathFeedbackCoordinator()

    LaunchedEffect(Unit) {
        feedbackCoordinator.onScreenOpened()
    }

    GameScreenBackground(
        showCelestialParticles = true,
        isReducedMotion = uiState.isReducedMotion,
        applyStatusBarPadding = false,
        applyNavigationBarPadding = false
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Top Bar
            WorldSelectionTopBar(
                totalStars = uiState.totalStarsEarned,
                onBackClick = {
                    feedbackCoordinator.onButtonPressed()
                    onBackClick()
                }
            )

            // Content Area: List of World Cards + Floating Continue Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                val contentModifier = if (windowInfo.maxContentWidth != androidx.compose.ui.unit.Dp.Unspecified && !windowInfo.useSideBySideLayout) {
                    Modifier.widthIn(max = windowInfo.maxContentWidth)
                } else {
                    Modifier.fillMaxWidth()
                }

                LazyColumn(
                    modifier = contentModifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = if (windowInfo.isCompactPhone) 14.dp else 18.dp,
                        end = if (windowInfo.isCompactPhone) 14.dp else 18.dp,
                        top = 10.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.worlds,
                        key = { it.worldId }
                    ) { world ->
                        Box(modifier = Modifier.cardEntrance(world.worldId - 1, uiState.isReducedMotion)) {
                            GameWorldCard(
                                world = world,
                                onClick = {
                                    if (world.isLocked) {
                                        feedbackCoordinator.onInvalidMove()
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                world.unlockRequirementText
                                                    ?: "World ${world.worldId} is locked. Solve required levels in World ${world.worldId - 1} to unlock."
                                            )
                                        }
                                    } else {
                                        feedbackCoordinator.onButtonPressed()
                                        viewModel.onWorldSelected(world.worldId)
                                        onSelectWorld(world.worldId)
                                    }
                                },
                                isReducedMotion = uiState.isReducedMotion
                            )
                        }
                    }
                }

                // Floating Sticky Continue Button at bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    GamePrimaryButton(
                        text = "CONTINUE ADVENTURE • LEVEL ${uiState.nextPlayableLevelId}",
                        onClick = {
                            feedbackCoordinator.onButtonPressed()
                            viewModel.onWorldSelected(uiState.nextPlayableWorldId)
                            onSelectWorld(uiState.nextPlayableWorldId)
                        },
                        isReducedMotion = uiState.isReducedMotion,
                        height = 54.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (windowInfo.maxContentWidth != androidx.compose.ui.unit.Dp.Unspecified) {
                                    Modifier.widthIn(max = 420.dp)
                                } else {
                                    Modifier
                                }
                            ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = GameDeepNavy,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }

                // Snackbar Host
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 76.dp)
                )
            }
        }
    }
}

@Composable
private fun WorldSelectionTopBar(
    totalStars: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(GameMidnightBlue)
                .border(1.dp, GameRoyalBlue.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Home",
                tint = GameWhite,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Center Title
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WORLD MAP",
                style = GameTypography.screenHeading.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = GameWhite
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "6 Fantasy Realms • 300 Levels",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = GameElectricCyan
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Right Star Counter Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GameMidnightBlue)
                .border(1.2.dp, GameGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_game_star),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$totalStars",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GameGoldHighlight
                )
            }
        }
    }
}
