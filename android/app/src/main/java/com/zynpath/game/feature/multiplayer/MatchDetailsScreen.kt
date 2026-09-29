package com.zynpath.game.feature.multiplayer

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zynpath.game.core.designsystem.theme.AccentGold
import com.zynpath.game.core.designsystem.theme.CharcoalNavy
import com.zynpath.game.core.designsystem.theme.DeepIndigo
import com.zynpath.game.core.designsystem.theme.EmeraldGreen
import com.zynpath.game.core.designsystem.theme.GunmetalCard
import com.zynpath.game.core.designsystem.theme.MutedSlate
import com.zynpath.game.core.designsystem.theme.OffWhiteText
import com.zynpath.game.core.designsystem.theme.PathNeonCyan
import com.zynpath.game.core.multiplayer.model.GameMode
import com.zynpath.game.core.multiplayer.model.MatchDetails
import com.zynpath.game.core.multiplayer.model.MatchParticipantSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Authoritative Match Details Screen.
 *
 * Implements Prompt 24 Sections 14, 16, 51:
 * - Detailed view of finalized match session.
 * - Mode, puzzle identity, authoritative server timing, and duration.
 * - Participant breakdown with validated standings and solve times.
 * - Restricted to authorized participants.
 */
@Composable
fun MatchDetailsScreen(
    matchId: String,
    onBackClick: () -> Unit,
    viewModel: CompetitiveViewModel = hiltViewModel()
) {
    val state by viewModel.detailsState.collectAsStateWithLifecycle()

    LaunchedEffect(matchId) {
        viewModel.loadMatchDetails(matchId)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalNavy)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OffWhiteText
                    )
                }
                Text(
                    text = "Match Details",
                    color = OffWhiteText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = DeepIndigo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PathNeonCyan
                    )
                }
                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.errorMessage ?: "Failed to load match details",
                                color = Color(0xFFEF5350),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadMatchDetails(matchId) },
                                colors = ButtonDefaults.buttonColors(containerColor = PathNeonCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Retry", color = DeepIndigo, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                state.details != null -> {
                    MatchDetailsContent(details = state.details!!)
                }
            }
        }
    }
}

@Composable
private fun MatchDetailsContent(details: MatchDetails) {
    val scrollState = rememberScrollState()

    val dateFormatted = try {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(if (details.endedAt > 0) details.endedAt else details.startedAt))
    } catch (_: Exception) {
        ""
    }

    val modeTitle = when (details.gameMode) {
        GameMode.QUICK_DUEL -> "Quick Duel (1v1)"
        GameMode.FRIEND_DUEL -> "Friend Duel (1v1)"
        GameMode.MINI_LEAGUE -> "Mini League (${details.participantCount} Players)"
        GameMode.FRIENDS_ARENA -> "Friends Arena (${details.participantCount} Players)"
    }

    val modeColor = when (details.gameMode) {
        GameMode.QUICK_DUEL -> PathNeonCyan
        GameMode.FRIEND_DUEL -> AccentGold
        GameMode.MINI_LEAGUE -> EmeraldGreen
        GameMode.FRIENDS_ARENA -> PathNeonCyan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Match Overview Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GunmetalCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(modeColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .border(1.dp, modeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = modeTitle,
                            color = modeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CharcoalNavy, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = details.matchStatus,
                            color = if (details.matchStatus == "COMPLETED") EmeraldGreen else AccentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = dateFormatted,
                    color = MutedSlate,
                    fontSize = 13.sp
                )

                if (details.durationMs > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Match Duration: ${String.format(Locale.US, "%.1fs", details.durationMs / 1000.0)}",
                        color = OffWhiteText.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Puzzle Specifications Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GunmetalCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = null,
                        tint = PathNeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Puzzle Assignment",
                        color = OffWhiteText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Puzzle ID", color = MutedSlate, fontSize = 11.sp)
                        Text(details.puzzleId, color = OffWhiteText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Grid Dimensions", color = MutedSlate, fontSize = 11.sp)
                        Text("${details.gridRows} × ${details.gridCols}", color = OffWhiteText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Fingerprint", color = MutedSlate, fontSize = 11.sp)
                        Text(details.puzzleFingerprint.take(8), color = PathNeonCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Standings Section
        Text(
            text = "Final Standings",
            color = OffWhiteText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        details.participants.forEach { participant ->
            ParticipantResultCard(participant = participant)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ParticipantResultCard(participant: MatchParticipantSummary) {
    val isWin = participant.isWinner || participant.finishOrder == 1
    val rankBadgeColor = when (participant.finishOrder) {
        1 -> AccentGold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MutedSlate
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isWin) CharcoalNavy.copy(alpha = 0.9f) else GunmetalCard
        ),
        border = if (isWin) androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(rankBadgeColor.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, rankBadgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${participant.finishOrder}",
                    color = rankBadgeColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Identity
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = participant.displayName,
                        color = OffWhiteText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isWin) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Winner",
                            tint = AccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = participant.publicZynpathId,
                    color = MutedSlate,
                    fontSize = 12.sp
                )
            }

            // Outcome & Timing
            Column(horizontalAlignment = Alignment.End) {
                if (participant.completed && participant.solveTimeMs != null && participant.solveTimeMs > 0) {
                    Text(
                        text = String.format(Locale.US, "%.1fs", participant.solveTimeMs / 1000.0),
                        color = PathNeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = participant.outcomeStatus,
                        color = if (isWin) EmeraldGreen else MutedSlate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = participant.outcomeStatus.ifEmpty { "DNF" },
                        color = Color(0xFFEF5350),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
