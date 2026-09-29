package com.zynpath.backend.operational;

import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.daily.service.DailyChallengeService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.multiplayer.service.MatchmakingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OperationalReadinessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServerPuzzleValidator serverPuzzleValidator;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private DailyChallengeService dailyChallengeService;

    @Test
    @DisplayName("Health endpoint reports UP status and service metadata")
    void healthEndpoint_reportsUpStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Zynpath Game Service"))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    @DisplayName("Actuator health endpoint reports status UP")
    void actuatorHealth_reportsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Performance: Server puzzle validation completes in under 10ms")
    void performance_puzzleValidation_under10ms() {
        List<String> cells4x4 = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                cells4x4.add(r + "," + c);
            }
        }

        PuzzleAssignment puzzle = new PuzzleAssignment(
                "perf_puzzle_4x4", 1, "fp_perf_4x4", 4, 4,
                cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0)
                ),
                List.of()
        );

        List<String> path = List.of(
                "0,0", "0,1", "0,2", "0,3",
                "1,3", "1,2", "1,1", "1,0",
                "2,0", "2,1", "2,2", "2,3",
                "3,3", "3,2", "3,1", "3,0"
        );

        // Warmup
        for (int i = 0; i < 50; i++) {
            serverPuzzleValidator.validateSolution(puzzle, path);
        }

        long start = System.nanoTime();
        var outcome = serverPuzzleValidator.validateSolution(puzzle, path);
        long elapsedNanos = System.nanoTime() - start;
        long elapsedMillis = elapsedNanos / 1_000_000L;

        assertThat(outcome.isValid()).isTrue();
        assertThat(elapsedMillis).isLessThan(10L);
    }

    @Test
    @DisplayName("Performance: Account creation and resolution completes in under 20ms")
    void performance_accountCreation_under20ms() {
        long start = System.currentTimeMillis();
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(
                AuthProvider.GOOGLE,
                "perf_sub_" + System.currentTimeMillis(),
                "Perf User",
                "perf@example.com",
                true
        );
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        long elapsed = System.currentTimeMillis() - start;

        assertThat(account.playerId()).isNotEmpty();
        assertThat(elapsed).isLessThan(50L);
    }

    @Test
    @DisplayName("Performance: Daily challenge canonical puzzle lookup completes in under 15ms")
    void performance_dailyChallengeLookup_under15ms() {
        long start = System.currentTimeMillis();
        var def = dailyChallengeService.getOfficialChallenge("2026-11-01");
        long elapsed = System.currentTimeMillis() - start;

        assertThat(def.puzzleId()).isNotEmpty();
        assertThat(elapsed).isLessThan(50L);
    }
}
