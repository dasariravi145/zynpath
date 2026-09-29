package com.zynpath.game.feature.onboarding

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.R
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.components.ZynpathSecondaryButton
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.TextSecondary
import com.zynpath.game.core.puzzle.model.SamplePuzzles
import com.zynpath.game.core.puzzle.ui.PuzzleBoard

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    onNavigateToTutorial: () -> Unit = onFinishOnboarding,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val sampleBoard = SamplePuzzles.Sample3x3_Solved

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Guest Status Badge & Skip Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundElevated)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ForestMint)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Guest Player • Offline Ready",
                        color = ForestMint,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            TextButton(onClick = { viewModel.onSkipClicked(onFinishOnboarding) }) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    color = TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Identity Header
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.app_tagline),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = PathCyanGlow,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Center Visual: Interactive Board Mechanics Diagram with real wall edges
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(BackgroundElevated)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            PuzzleBoard(
                boardState = sampleBoard,
                isInputEnabled = false,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Concise Mechanics Description
        Text(
            text = stringResource(R.string.onboarding_welcome_desc),
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons: PLAY (Primary) & HOW TO PLAY (Secondary)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ZynpathPrimaryButton(
                text = stringResource(R.string.onboarding_play),
                onClick = { viewModel.onPlayClicked(onNavigateToTutorial, onFinishOnboarding) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = BackgroundDark
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            ZynpathSecondaryButton(
                text = stringResource(R.string.onboarding_how_to_play),
                onClick = { viewModel.onHowToPlayClicked(onNavigateToTutorial) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = ForestMint
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No account required. Your progress is saved automatically on this device.",
            fontSize = 11.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}
