package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.MultiplayerPuzzlePool;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicPuzzleInventoryTest {

    private ProceduralPuzzleGenerator generator;
    private InMemoryMultiplayerPuzzlePersistenceAdapter persistencePort;
    private MultiplayerPuzzlePool fallbackPool;
    private DynamicPuzzleInventory inventory;
    private ServerPuzzleValidator serverValidator;

    @BeforeEach
    void setUp() {
        generator = new ProceduralPuzzleGenerator();
        persistencePort = new InMemoryMultiplayerPuzzlePersistenceAdapter();
        fallbackPool = new MultiplayerPuzzlePool();
        inventory = new DynamicPuzzleInventory(generator, persistencePort, fallbackPool);
        serverValidator = new ServerPuzzleValidator();
    }

    @Test
    @DisplayName("Acquires validated dynamic puzzle when inventory is primed")
    void testAcquireDynamicPuzzle() {
        inventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, 2);
        assertThat(inventory.getAvailableCount(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT)).isGreaterThanOrEqualTo(2);

        String matchId = "match_test_01";
        List<String> players = List.of("player_1", "player_2");
        PuzzleAssignment puzzle = inventory.acquirePuzzleForMatch(
                GameMode.QUICK_DUEL,
                CompetitiveDifficultyTier.EXPERT,
                matchId,
                players,
                null,
                12345L
        );

        assertThat(puzzle).isNotNull();
        assertThat(puzzle.puzzleId()).startsWith("mp_dyn_");
        assertThat(puzzle.gridRows()).isIn(11, 12);
        assertThat(puzzle.fingerprint()).isNotEmpty();

        // Verify with server validator
        GeneratedPuzzle gen = persistencePort.findPuzzleById(puzzle.puzzleId()).orElse(null);
        assertThat(gen).isNotNull();
        ValidationOutcome val = serverValidator.validateSolution(puzzle, gen.canonicalSolution());
        assertThat(val.isValid()).isTrue();
    }

    @Test
    @DisplayName("Recent repeat suppression: avoids serving recently played puzzle to the same player")
    void testRepeatSuppression() {
        inventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.VERY_HARD, 3);

        String playerA = "player_repeat_test";
        PuzzleAssignment match1 = inventory.acquirePuzzleForMatch(
                GameMode.QUICK_DUEL,
                CompetitiveDifficultyTier.VERY_HARD,
                "match_01",
                List.of(playerA, "player_B"),
                null,
                111L
        );

        PuzzleAssignment match2 = inventory.acquirePuzzleForMatch(
                GameMode.QUICK_DUEL,
                CompetitiveDifficultyTier.VERY_HARD,
                "match_02",
                List.of(playerA, "player_C"),
                null,
                222L
        );

        assertThat(match2.puzzleId())
                .as("Subsequent match should assign a different puzzle from inventory")
                .isNotEqualTo(match1.puzzleId());
        assertThat(match2.fingerprint())
                .as("Subsequent match should have different fingerprint")
                .isNotEqualTo(match1.fingerprint());
    }

    @Test
    @DisplayName("Safe fallback: seamlessly delegates to handcrafted pool when dynamic inventory is empty")
    void testFallbackWhenInventoryEmpty() {
        // Zero puzzles primed in inventory
        assertThat(inventory.getAvailableCount(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.MASTER)).isEqualTo(0);

        PuzzleAssignment puzzle = inventory.acquirePuzzleForMatch(
                GameMode.QUICK_DUEL,
                CompetitiveDifficultyTier.MASTER,
                "match_fallback_01",
                List.of("p1", "p2"),
                null,
                999L
        );

        assertThat(puzzle).isNotNull();
        assertThat(puzzle.puzzleId()).startsWith("mp_comp_"); // Sourced from verified handcrafted fallback pool
        assertThat(puzzle.gridRows()).isEqualTo(12);
    }
}
