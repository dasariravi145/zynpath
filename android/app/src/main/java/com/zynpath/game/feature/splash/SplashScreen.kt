package com.zynpath.game.feature.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.R
import kotlinx.coroutines.delay

/**
 * Exact reference Zynpath splash screen matching 01_splash_1080x1920.png.
 *
 * Visual hierarchy:
 * - Exactly ONE ZYNPATH logo (authoritative 3D artwork from clean background).
 * - Exactly ONE NUMBER PATH PUZZLE subtitle.
 * - Exactly ONE Connect the Numbers / Conquer the Path tagline.
 * - Exactly ONE live loading progress bar (monotonic forward-only, never reverses).
 * - Exactly ONE Loading... label.
 *
 * Fixes physical device bugs:
 * - Zero duplicate branding or taglines overlayed on background.
 * - Zero backward-oscillating loading bar.
 * - Removed from backstack on completion so Back from Home never returns here.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: (() -> Unit)? = null,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navTarget by viewModel.navigationTarget.collectAsStateWithLifecycle()
    val progress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val isReducedMotion by viewModel.isReducedMotion.collectAsStateWithLifecycle()

    var hasNavigated by remember { mutableStateOf(false) }

    // Smooth forward-only progress animation (never reverses)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = if (isReducedMotion) 0 else 300),
        label = "splashProgressFill"
    )

    // Startup navigation guard: execute once and pop Splash from backstack
    LaunchedEffect(navTarget) {
        if (navTarget != SplashNavigationTarget.Loading && !hasNavigated) {
            val minDisplayDelay = if (isReducedMotion) 300L else 900L
            delay(minDisplayDelay)
            if (!hasNavigated) {
                hasNavigated = true
                when (navTarget) {
                    SplashNavigationTarget.Home -> onNavigateToHome()
                    SplashNavigationTarget.Login -> onNavigateToLogin()
                    SplashNavigationTarget.Onboarding -> (onNavigateToOnboarding ?: onNavigateToLogin)()
                    SplashNavigationTarget.Loading -> {}
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Reference Clean Artwork Background (Contains the single official 3D logo, subtitle, and tagline)
        Image(
            painter = painterResource(id = R.drawable.bg_splash_clean),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Lower Section: Single Live Progress Bar and Single "Loading..." Text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Reference-Exact Capsule Progress Bar (Electric Cyan border, deep navy track, cyan fill)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.52f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0xFF001736))
                    .border(1.5.dp, Color(0xFF0077D4), RoundedCornerShape(7.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress.coerceIn(0.02f, 1f))
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0091EA),
                                    Color(0xFF00B0FF),
                                    Color(0xFF00E5FF)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Single "Loading..." label matching reference typography
            Text(
                text = "Loading...",
                style = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 1.5f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}
