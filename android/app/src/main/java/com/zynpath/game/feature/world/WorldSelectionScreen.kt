package com.zynpath.game.feature.world

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zynpath.game.core.designsystem.components.ScreenHeader
import com.zynpath.game.core.designsystem.components.ZynpathWorldCard
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.BackgroundDark
import com.zynpath.game.core.designsystem.theme.BackgroundElevated
import com.zynpath.game.core.designsystem.theme.TextPrimary

@Composable
fun WorldSelectionScreen(
    onBackClick: () -> Unit,
    onSelectWorld: (worldId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorldSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            ScreenHeader(
                title = "World Selection",
                subtitle = "6 distinct regions • 300 levels",
                onBackClick = onBackClick,
                actionSlot = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.totalStarsEarned}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(uiState.worlds, key = { it.worldId }) { world ->
                ZynpathWorldCard(
                    world = world,
                    onClick = { onSelectWorld(world.worldId) }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B132B)
@Composable
private fun WorldSelectionScreenPreview() {
    Column(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        ScreenHeader(title = "World Selection", subtitle = "6 worlds", onBackClick = {})
    }
}
