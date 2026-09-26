package com.zynpath.game.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.ZynpathPrimaryButton
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundCard
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.BoardBackgroundLight
import com.zynpath.game.core.designsystem.theme.CheckpointDark
import com.zynpath.game.core.designsystem.theme.CheckpointTextWhite
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import com.zynpath.game.core.designsystem.theme.WallCrimson

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val currentSlideIndex by viewModel.currentSlideIndex.collectAsStateWithLifecycle()
    val slides = viewModel.slides
    val currentSlide = slides[currentSlideIndex]
    val isLastSlide = currentSlideIndex == slides.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Step Counter & Skip Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = currentSlide.stepIndicator,
                    color = ForestMint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!isLastSlide) {
                TextButton(onClick = { viewModel.skipOnboarding(onFinishOnboarding) }) {
                    Text(
                        text = "Skip",
                        color = TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Center Visual: Interactive Board Mechanics Diagram
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(BackgroundElevated)
                .border(2.dp, BackgroundCard, RoundedCornerShape(24.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            OnboardingBoardDiagram(slideIndex = currentSlideIndex)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Slide Content Area
        AnimatedContent(
            targetState = currentSlide,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "SlideTransition"
        ) { slide ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = slide.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = slide.subtitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestMint,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = slide.detail,
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Step Dots
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            slides.indices.forEach { index ->
                val isSelected = index == currentSlideIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ForestMint else BackgroundCard)
                )
            }
        }

        // Action Buttons: Back & Next / Get Started
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentSlideIndex > 0) {
                TextButton(
                    onClick = { viewModel.previousSlide() },
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundCard)
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Back",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            ZynpathPrimaryButton(
                text = if (isLastSlide) "Get Started" else "Next",
                onClick = { viewModel.nextSlide(onFinishOnboarding) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OnboardingBoardDiagram(slideIndex: Int) {
    // 3x3 Mini-board illustrating the exact continuous number-path mechanic
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (r in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (c in 0..2) {
                    val isCheckpoint1 = r == 0 && c == 0
                    val isCheckpoint2 = r == 1 && c == 2
                    val isCheckpoint3 = r == 2 && c == 0
                    val isWall = slideIndex == 3 && r == 0 && c == 1

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isWall -> WallCrimson.copy(alpha = 0.8f)
                                    isCheckpoint1 || isCheckpoint2 || isCheckpoint3 -> CheckpointDark
                                    slideIndex >= 1 -> PathCyanGlow.copy(alpha = 0.25f)
                                    else -> BoardBackgroundLight
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isWall) WallCrimson else com.zynpath.game.core.designsystem.theme.BoardCellBorder,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isCheckpoint1 -> Text("1", color = CheckpointTextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            isCheckpoint2 -> Text("2", color = CheckpointTextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            isCheckpoint3 -> Text("3", color = CheckpointTextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            isWall -> Text("✕", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            slideIndex >= 2 -> Text("•", color = PathCyanGlow, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
