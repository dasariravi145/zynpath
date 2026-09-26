package com.zynpath.game.feature.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

data class TutorialStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val tip: String
)

val TutorialSteps: List<TutorialStep> = listOf(
    TutorialStep(
        stepNumber = 1,
        title = "Start at Number 1",
        description = "Every puzzle begins at checkpoint 1. Place your finger on 1 to initiate your path.",
        tip = "Rule: You can never start from any other checkpoint."
    ),
    TutorialStep(
        stepNumber = 2,
        title = "Ascending Checkpoint Order",
        description = "Connect numbered checkpoints in strictly sequential order: 1 → 2 → 3 → 4.",
        tip = "Rule: Never skip a number or visit checkpoints out of sequence."
    ),
    TutorialStep(
        stepNumber = 3,
        title = "One Continuous Path",
        description = "Move horizontally or vertically into adjacent cells without lifting your finger.",
        tip = "Rule: No diagonal movement, no crossing your own path, and no revisiting cells."
    ),
    TutorialStep(
        stepNumber = 4,
        title = "Navigate Around Walls",
        description = "Thick crimson barriers are blocked connections. You cannot draw a path across a wall.",
        tip = "Rule: Plan your route through corridors to avoid dead-ends."
    ),
    TutorialStep(
        stepNumber = 5,
        title = "Cover Every Required Cell",
        description = "You cannot take direct shortcuts between checkpoints. Every single square in the grid must be visited.",
        tip = "Crucial: Reaching the last number without 100% cell coverage is NOT a victory!"
    ),
    TutorialStep(
        stepNumber = 6,
        title = "Complete Full Path",
        description = "When all checkpoints are visited in order AND all cells are covered, the puzzle is solved!",
        tip = "One path. Every number. You are now ready for World 1!"
    )
)

@Composable
fun TutorialScreen(
    onFinishTutorial: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = TutorialSteps.size
    val activeStep = TutorialSteps[currentStep - 1]
    val currentBoardState = remember(currentStep) {
        SamplePuzzles.getTutorialStepBoard(currentStep)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ScreenHeader(
                title = "How to Play",
                subtitle = "Step $currentStep of $totalSteps",
                onBackClick = onBackClick
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
            // Step Progress Bar
            LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ForestMint,
                trackColor = BackgroundCard
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Real PuzzleBoard Rendering Component
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PuzzleBoard(
                    boardState = currentBoardState,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Instruction Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundCard)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = activeStep.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = activeStep.description,
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
                            text = activeStep.tip,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ForestMint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 1) {
                    ZynpathSecondaryButton(
                        text = "Back",
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f)
                    )
                }

                ZynpathPrimaryButton(
                    text = if (currentStep == totalSteps) "Start Playing" else "Next Step",
                    onClick = {
                        if (currentStep < totalSteps) {
                            currentStep++
                        } else {
                            onFinishTutorial()
                        }
                    },
                    modifier = Modifier.weight(if (currentStep > 1) 1.5f else 1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun TutorialScreenPreview() {
    TutorialScreen(onFinishTutorial = {}, onBackClick = {})
}
