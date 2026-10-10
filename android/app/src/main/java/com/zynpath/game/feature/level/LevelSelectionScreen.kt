package com.zynpath.game.feature.level

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.LevelCardData
import com.zynpath.game.core.designsystem.components.LevelState
import com.zynpath.game.core.designsystem.components.ZynpathMasterHeaderBar
import com.zynpath.game.core.designsystem.components.ZynpathMasterSegmentedTabs
import com.zynpath.game.core.designsystem.theme.RefCyanGlow
import com.zynpath.game.core.designsystem.theme.RefCyanNeon
import com.zynpath.game.core.designsystem.theme.RefGoldPrimary
import com.zynpath.game.core.designsystem.theme.RefNavyDark
import com.zynpath.game.core.designsystem.theme.RefNavySurface
import com.zynpath.game.core.feedback.rememberZynpathFeedbackCoordinator
import com.zynpath.game.core.onboarding.WorldIntroductionDialog
import com.zynpath.game.feature.level.components.LevelDetailsDialog

/**
 * Level Selection Screen matching Master Reference Panel 04 (`04_level_selection.png`).
 *
 * - Header: Back '<', Title "Select Level", Forward '>'
 * - World Tabs: "World 1", "World 2", "World 3" (gold pill active)
 * - 4-column level grid:
 *   - Completed: Glowing cyan gradient, bold white number, gold stars below
 *   - Unlocked: Dark navy, cyan border, white number, stars below
 *   - Locked: Dark navy, gray number, silver lock icon below
 * - Bottom Scenic Artwork: `bg_level_scenic`
 * - Preserves all 300 levels, progress tracking, and LevelDetailsDialog.
 */
@Composable
fun LevelSelectionScreen(
    onBackClick: () -> Unit,
    onSelectLevel: (worldId: Int, levelNumber: Int) -> Unit,
    onNavigateToTutorial: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LevelSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val feedbackCoordinator = rememberZynpathFeedbackCoordinator()
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        feedbackCoordinator.onScreenOpened()
    }

    // Auto-focus current level when world or nextPlayableLevelId changes
    LaunchedEffect(uiState.worldId, uiState.nextPlayableLevelId) {
        val currentLevelIndex = uiState.levels.indexOfFirst { it.isCurrent }
        if (currentLevelIndex >= 0) {
            gridState.animateScrollToItem(currentLevelIndex)
        }
    }

    var selectedLevelForDetails by remember { mutableStateOf<LevelCardData?>(null) }

    // World Introduction Tip Dialog
    val activeIntro = uiState.activeWorldIntro
    if (activeIntro != null) {
        WorldIntroductionDialog(
            tip = activeIntro,
            onDismiss = { viewModel.dismissWorldIntro() }
        )
    }

    // Level Details Modal Dialog (can still be shown if explicit details requested)
    val currentSelected = selectedLevelForDetails
    if (currentSelected != null) {
        LevelDetailsDialog(
            level = currentSelected,
            worldId = uiState.worldId,
            worldName = uiState.worldName,
            onPlayClick = {
                feedbackCoordinator.onButtonPressed()
                viewModel.onLevelSelected(currentSelected.levelNumber)
                onSelectLevel(uiState.worldId, currentSelected.levelNumber)
            },
            onDismiss = { selectedLevelForDetails = null },
            isReducedMotion = uiState.isReducedMotion
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RefNavyDark)
    ) {
        // Bottom Scenic Artwork (Master Reference Panel 04)
        Image(
            painter = painterResource(id = R.drawable.bg_level_scenic),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(200.dp)
        )

        // Dark gradient scrim over the top portion of the bottom art
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RefNavyDark.copy(alpha = 0.95f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Master Header Bar (Panel 04: '<' Select Level '>')
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        feedbackCoordinator.onButtonPressed()
                        onBackClick()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131D33))
                        .border(1.dp, Color(0xFF24324E), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Select Level",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(
                    onClick = {
                        feedbackCoordinator.onButtonPressed()
                        val nextWorld = if (uiState.worldId >= com.zynpath.game.core.puzzle.model.WorldConfiguration.TOTAL_WORLDS) 1 else uiState.worldId + 1
                        viewModel.selectWorld(nextWorld)
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131D33))
                        .border(1.dp, Color(0xFF24324E), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next World",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // World Tabs: All 6 canonical worlds
            val worldTabs = (1..com.zynpath.game.core.puzzle.model.WorldConfiguration.TOTAL_WORLDS).map { "World $it" }
            val selectedTabIndex = (uiState.worldId - 1).coerceIn(0, com.zynpath.game.core.puzzle.model.WorldConfiguration.TOTAL_WORLDS - 1)

            ZynpathMasterSegmentedTabs(
                tabs = worldTabs,
                selectedIndex = selectedTabIndex,
                onTabSelected = { index ->
                    feedbackCoordinator.onButtonPressed()
                    viewModel.selectWorld(index + 1)
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Compact World Status Bar (Name, Grid Size, Solved / Locked Status)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.worldName} (${uiState.gridSize})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RefCyanNeon
                )

                if (uiState.isWorldLocked && uiState.worldUnlockRequirement != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFCA5A5),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Locked",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                } else {
                    Text(
                        text = "${uiState.completedCount}/${uiState.levels.size} Solved",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4-Column Grid of Levels
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                state = gridState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(
                    items = uiState.levels,
                    key = { it.levelNumber }
                ) { level ->
                    ReferenceLevelItem(
                        level = level,
                        onClick = {
                            if (level.state == LevelState.LOCKED) {
                                feedbackCoordinator.onInvalidMove()
                                val reqMsg = if (uiState.isWorldLocked && uiState.worldUnlockRequirement != null) {
                                    uiState.worldUnlockRequirement!!
                                } else if (level.localLevelNumber == 1) {
                                    "Level 1 is unlocked."
                                } else {
                                    "Solve Level ${level.localLevelNumber - 1} to unlock this path."
                                }
                                coroutineScope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(reqMsg)
                                }
                            } else {
                                // Direct level entry into gameplay
                                feedbackCoordinator.onButtonPressed()
                                viewModel.onLevelSelected(level.levelNumber)
                                onSelectLevel(uiState.worldId, level.levelNumber)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.navigationBarsPadding())
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) { snackbarData ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.2.dp, RefCyanNeon.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RefGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = snackbarData.visuals.message,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * 4-column Reference Level Card (Panel 04).
 */
@Composable
private fun ReferenceLevelItem(
    level: LevelCardData,
    onClick: () -> Unit
) {
    val isCompleted = level.state == LevelState.COMPLETED
    val isCurrent = level.isCurrent
    val isUnlocked = level.state == LevelState.UNLOCKED || isCurrent
    val isLocked = level.state == LevelState.LOCKED

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Main Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    when {
                        isCompleted -> {
                            Modifier
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(RefCyanGlow, RefCyanNeon)
                                    )
                                )
                                .border(1.dp, Color(0xFF67E8F9), RoundedCornerShape(12.dp))
                        }
                        isCurrent -> {
                            Modifier
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF1E293B), RefNavySurface)
                                    )
                                )
                                .border(2.dp, RefGoldPrimary, RoundedCornerShape(12.dp))
                        }
                        isUnlocked -> {
                            Modifier
                                .background(RefNavySurface)
                                .border(1.5.dp, RefCyanNeon, RoundedCornerShape(12.dp))
                        }
                        else -> {
                            Modifier
                                .background(RefNavySurface.copy(alpha = 0.85f))
                                .border(1.dp, Color(0xFF24324E), RoundedCornerShape(12.dp))
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${level.localLevelNumber}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isCompleted -> Color.White
                    isCurrent -> RefGoldPrimary
                    isUnlocked -> Color.White
                    else -> Color(0xFF64748B)
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Sub-indicator: Stars, Current Badge, or Lock
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(13.dp)
            )
        } else if (isCurrent) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(RefGoldPrimary.copy(alpha = 0.2f))
                    .border(0.8.dp, RefGoldPrimary.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "CURRENT",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RefGoldPrimary
                )
            }
        } else {
            // Row of 3 stars
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { starIndex ->
                    val filled = starIndex < level.stars
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (filled) RefGoldPrimary else Color(0xFF334155),
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}
