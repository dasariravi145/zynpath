package com.zynpath.game.feature.tutorial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.designsystem.theme.WallCrimson
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

@Composable
fun TutorialScreen(
    onFinishTutorial: () -> Unit,
    onBackClick: () -> Unit,
    onStartSoloLevel1: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: InteractiveTutorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.tutorial_title),
                subtitle = stringResource(R.string.tutorial_stage_counter, uiState.currentStage, uiState.totalStages),
                onBackClick = onBackClick,
                actionSlot = {
                    TextButton(onClick = { viewModel.skipTutorial(onFinishTutorial) }) {
                        Text(
                            text = stringResource(R.string.onboarding_skip),
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stage Progress Bar
            LinearProgressIndicator(
                progress = { uiState.currentStage.toFloat() / uiState.totalStages.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ForestMint,
                trackColor = BackgroundCard
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mode Selector: Interactive Board vs Text Guide
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundElevated)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!uiState.isTextMode) BackgroundCard else BackgroundElevated)
                        .clickable { if (uiState.isTextMode) viewModel.toggleTextMode() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.tutorial_interactive_mode),
                        fontSize = 13.sp,
                        fontWeight = if (!uiState.isTextMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (!uiState.isTextMode) ForestMint else TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (uiState.isTextMode) BackgroundCard else BackgroundElevated)
                        .clickable { if (!uiState.isTextMode) viewModel.toggleTextMode() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.tutorial_alt_text_mode),
                        fontSize = 13.sp,
                        fontWeight = if (uiState.isTextMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (uiState.isTextMode) ForestMint else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isTextMode) {
                // Alternative Text Mode for Accessibility
                TutorialTextGuide(
                    currentStage = uiState.currentStage,
                    onSelectStage = { viewModel.loadStage(it) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Interactive Puzzle Engine Board
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    PuzzleBoard(
                        boardState = uiState.boardState,
                        modifier = Modifier.fillMaxWidth(0.92f),
                        isInputEnabled = !uiState.isStageCompleted || uiState.currentStage == 7,
                        onCellEntered = { pos -> viewModel.onCellEntered(pos) },
                        onPointerReleased = { viewModel.onPointerReleased() }
                    )
                }

                // Granular Move Rejection Banner
                AnimatedVisibility(
                    visible = uiState.activeFeedbackMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    uiState.activeFeedbackMessage?.let { feedback ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WallCrimson.copy(alpha = 0.2f))
                                .border(1.dp, WallCrimson.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .semantics {
                                    liveRegion = LiveRegionMode.Polite
                                    contentDescription = "Tutorial feedback: $feedback"
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = WallCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = feedback,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Real Gameplay Recovery Controls (Undo & Reset)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.onUndoClick() },
                        enabled = uiState.canUndo || uiState.currentStage == 6,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.tutorial_undo), fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.onResetClick() },
                        enabled = uiState.canReset || uiState.currentStage == 6,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.tutorial_reset), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Instruction Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundCard)
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(uiState.stageConfig.titleRes),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (uiState.isStageCompleted) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = ForestMint,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ready",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestMint
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(uiState.stageConfig.descRes),
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackgroundDark.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(uiState.stageConfig.tipRes),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ForestMint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Actions: Back / Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.currentStage > 1) {
                    ZynpathSecondaryButton(
                        text = stringResource(R.string.onboarding_back),
                        onClick = { viewModel.previousStage() },
                        modifier = Modifier.weight(1f)
                    )
                }

                ZynpathPrimaryButton(
                    text = if (uiState.currentStage == uiState.totalStages) {
                        if (uiState.isStageCompleted) "Finish Tutorial" else "Complete Board"
                    } else if (uiState.isStageCompleted) {
                        "Next Stage →"
                    } else {
                        "Skip Stage"
                    },
                    onClick = {
                        if (uiState.currentStage == uiState.totalStages && uiState.isStageCompleted) {
                            onFinishTutorial()
                        } else {
                            viewModel.nextStage()
                        }
                    },
                    modifier = Modifier.weight(if (uiState.currentStage > 1) 1.5f else 1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Completion Dialog / Success State (Prompt 33 Section 23)
        if (uiState.isAllTutorialFinished) {
            AlertDialog(
                onDismissRequest = { /* Modal */ },
                title = {
                    Text(
                        text = stringResource(R.string.tutorial_completed_title),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = stringResource(R.string.tutorial_completed_subtitle),
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForestMint.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Solo Level 1 is unlocked! You can replay this tutorial anytime from Settings or Home.",
                                fontSize = 12.sp,
                                color = ForestMint,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                confirmButton = {
                    ZynpathPrimaryButton(
                        text = stringResource(R.string.tutorial_btn_start_solo),
                        onClick = {
                            if (onStartSoloLevel1 != null) {
                                onStartSoloLevel1()
                            } else {
                                onFinishTutorial()
                            }
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.replayTutorial() }) {
                        Text(stringResource(R.string.tutorial_replay), color = PathCyanGlow)
                    }
                },
                containerColor = BackgroundCard,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun TutorialTextGuide(
    currentStage: Int,
    onSelectStage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Complete Rules & Mechanics Guide",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        TutorialStageConfigs.forEach { config ->
            val isActive = config.stageNumber == currentStage

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isActive) BackgroundElevated else BackgroundCard)
                    .border(
                        width = if (isActive) 1.5.dp else 1.dp,
                        color = if (isActive) ForestMint else com.zynpath.game.core.designsystem.theme.BoardCellBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectStage(config.stageNumber) }
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${config.stageNumber}. ${stringResource(config.titleRes)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) ForestMint else TextPrimary
                        )
                        if (isActive) {
                            Text("Current", fontSize = 11.sp, color = ForestMint, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(config.descRes),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(config.tipRes),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
