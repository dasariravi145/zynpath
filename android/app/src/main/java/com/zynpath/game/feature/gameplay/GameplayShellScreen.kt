package com.zynpath.game.feature.gameplay

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.ZynpathConfirmationDialog
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

@Composable
fun GameplayShellScreen(
    worldId: Int,
    levelId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }

    // Use deterministic sample puzzle as visual template for shell
    val initialBoard = remember(worldId) {
        if (worldId == 1) SamplePuzzles.Sample4x4_Base else SamplePuzzles.Sample3x3_Base
    }
    var currentBoard by remember { mutableStateOf(initialBoard) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ScreenHeader(
                title = "World $worldId • Level $levelId",
                subtitle = "${currentBoard.rowCount}×${currentBoard.columnCount} Grid",
                onBackClick = onBackClick,
                actionSlot = {
                    IconButton(
                        onClick = { showPauseDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BackgroundElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Game Info",
                            tint = ForestMint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stats Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundElevated)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn(label = "TIME", value = "00:00")
                Box(
                    modifier = Modifier
                        .size(1.dp, 24.dp)
                        .background(BackgroundCard)
                )
                StatColumn(
                    label = "COVERAGE",
                    value = "${currentBoard.coveredCellCount} / ${currentBoard.totalRequiredCells}"
                )
                Box(
                    modifier = Modifier
                        .size(1.dp, 24.dp)
                        .background(BackgroundCard)
                )
                StatColumn(label = "BEST", value = "--:--")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Architectural Roadmap Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundCard.copy(alpha = 0.6f))
                    .border(1.dp, ForestMint.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "PHASE 1 VISUAL SHELL • Pure Kotlin engine and touch-drag Canvas integration will be completed in Phase 2.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Real PuzzleBoard Canvas Renderer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PuzzleBoard(
                    boardState = currentBoard,
                    modifier = Modifier.fillMaxWidth(0.95f),
                    onCellClick = { coord ->
                        // Visual tap preview (toggles single cell without fake game logic)
                        val newPath = if (currentBoard.path.contains(coord)) {
                            currentBoard.path.filter { it != coord }
                        } else {
                            currentBoard.path + coord
                        }
                        currentBoard = currentBoard.copy(path = newPath)
                    }
                )
            }

            // Controls Bar (Undo, Reset, Hint)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reset Button
                ShellActionButton(
                    label = "Reset",
                    icon = Icons.Default.Refresh,
                    onClick = { showResetDialog = true },
                    modifier = Modifier.weight(1f)
                )

                // Undo Button (Disabled shell)
                ShellActionButton(
                    label = "Undo",
                    icon = Icons.Default.PlayArrow,
                    onClick = {},
                    enabled = false,
                    badgeText = "Phase 2",
                    modifier = Modifier.weight(1f)
                )

                // Hint Button (Disabled shell)
                ShellActionButton(
                    label = "Hint",
                    icon = Icons.Default.Info,
                    onClick = {},
                    enabled = false,
                    badgeText = "Phase 2",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showResetDialog) {
        ZynpathConfirmationDialog(
            title = "Reset Puzzle",
            message = "Clear your current preview path? The game board will reset to its initial state.",
            confirmButtonText = "Reset",
            onConfirm = {
                currentBoard = initialBoard
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }

    if (showPauseDialog) {
        ZynpathConfirmationDialog(
            title = "Gameplay Shell",
            message = "Zynpath Number Path Puzzle rules:\n1. Start at 1\n2. Ascending order\n3. One continuous orthogonal path\n4. Cover 100% of cells\n5. Avoid walls\n\nPure Kotlin game engine integration will arrive in Phase 2.",
            confirmButtonText = "Resume",
            onConfirm = { showPauseDialog = false },
            onDismiss = { showPauseDialog = false }
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun ShellActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    badgeText: String? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) BackgroundElevated else BackgroundCard.copy(alpha = 0.5f))
            .then(
                if (enabled) Modifier.border(1.dp, ForestMint.copy(alpha = 0.3f), RoundedCornerShape(14.dp)) else Modifier
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) ForestMint else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) TextPrimary else TextMuted
            )
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun GameplayShellPreview() {
    GameplayShellScreen(worldId = 1, levelId = 1, onBackClick = {})
}
