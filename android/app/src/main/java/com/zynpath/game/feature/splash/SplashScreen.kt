package com.zynpath.game.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.ForestMint
import com.zynpath.game.core.designsystem.theme.PathCyanGlow
import com.zynpath.game.core.designsystem.theme.TextMuted
import com.zynpath.game.core.designsystem.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navTarget by viewModel.navigationTarget.collectAsStateWithLifecycle()

    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        scale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(600) // Brief subtle transition for brand recognition
        when (navTarget) {
            SplashNavigationTarget.Home -> onNavigateToHome()
            SplashNavigationTarget.Onboarding -> onNavigateToOnboarding()
            SplashNavigationTarget.Loading -> {
                // If still resolving, delay a frame
                delay(100)
                if (navTarget == SplashNavigationTarget.Home) onNavigateToHome() else onNavigateToOnboarding()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Visual Logo Motif: Connected number nodes
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(com.zynpath.game.core.designsystem.theme.BackgroundCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1 → N",
                    color = PathCyanGlow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Zynpath",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Number Path Puzzle",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestMint,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "One path. Every number.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
