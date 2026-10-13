package com.zynpath.backend.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.multiplayer.generator.CompetitiveDifficultyTier;
import com.zynpath.backend.multiplayer.generator.GeneratedPuzzle;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.persistence.entity.GeneratedPuzzleJpaEntity;
import com.zynpath.backend.persistence.entity.MultiplayerPlayerPuzzleHistoryJpaEntity;
import com.zynpath.backend.persistence.entity.MultiplayerPuzzleAssignmentJpaEntity;
import com.zynpath.backend.persistence.repository.GeneratedPuzzleJpaRepository;
import com.zynpath.backend.persistence.repository.MultiplayerPlayerPuzzleHistoryJpaRepository;
import com.zynpath.backend.persistence.repository.MultiplayerPuzzleAssignmentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaMultiplayerPuzzlePersistenceAdapterTest {

    private GeneratedPuzzleJpaRepository puzzleRepository;
    private MultiplayerPuzzleAssignmentJpaRepository assignmentRepository;
    private MultiplayerPlayerPuzzleHistoryJpaRepository historyRepository;
    private ObjectMapper objectMapper;
    private JpaMultiplayerPuzzlePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        puzzleRepository = mock(GeneratedPuzzleJpaRepository.class);
        assignmentRepository = mock(MultiplayerPuzzleAssignmentJpaRepository.class);
        historyRepository = mock(MultiplayerPlayerPuzzleHistoryJpaRepository.class);
        objectMapper = new ObjectMapper();
        adapter = new JpaMultiplayerPuzzlePersistenceAdapter(
                puzzleRepository,
                assignmentRepository,
                historyRepository,
                objectMapper
        );
    }

    @Test
    @DisplayName("saveGeneratedPuzzle serializes and saves entity")
    void testSaveGeneratedPuzzle() {
        GeneratedPuzzle puzzle = new GeneratedPuzzle(
                "p_test_101",
                1,
                "fp_test_abc",
                CompetitiveDifficultyTier.EXPERT,
                GameMode.QUICK_DUEL,
                11,
                11,
                List.of("0,0", "0,1"),
                List.of(new com.zynpath.backend.multiplayer.model.PuzzleAssignment.CheckpointSpec(1, 0, 0)),
                List.of(),
                List.of("0,0", "0,1"),
                0.62,
                null,
                0L,
                0L
        );

        adapter.saveGeneratedPuzzle(puzzle);

        ArgumentCaptor<GeneratedPuzzleJpaEntity> captor = ArgumentCaptor.forClass(GeneratedPuzzleJpaEntity.class);
        verify(puzzleRepository).save(captor.capture());
        GeneratedPuzzleJpaEntity saved = captor.getValue();
        assertThat(saved.getPuzzleId()).isEqualTo("p_test_101");
        assertThat(saved.getFingerprint()).isEqualTo("fp_test_abc");
        assertThat(saved.getInventoryStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    @DisplayName("recordAssignment marks puzzle assigned and persists assignment and history entities")
    void testRecordAssignment() {
        String puzzleId = "p_test_101";
        GeneratedPuzzleJpaEntity puzzleEntity = new GeneratedPuzzleJpaEntity();
        puzzleEntity.setPuzzleId(puzzleId);
        puzzleEntity.setFingerprint("fp_test_abc");
        puzzleEntity.setInventoryStatus("AVAILABLE");

        when(puzzleRepository.findById(puzzleId)).thenReturn(Optional.of(puzzleEntity));

        adapter.recordAssignment(
                "asgn_01",
                puzzleId,
                "match_01",
                GameMode.QUICK_DUEL,
                CompetitiveDifficultyTier.EXPERT,
                List.of("player_1", "player_2")
        );

        // 1. Puzzle status updated
        verify(puzzleRepository).save(puzzleEntity);
        assertThat(puzzleEntity.getInventoryStatus()).isEqualTo("ASSIGNED");

        // 2. Assignment record saved
        ArgumentCaptor<MultiplayerPuzzleAssignmentJpaEntity> asgnCaptor =
                ArgumentCaptor.forClass(MultiplayerPuzzleAssignmentJpaEntity.class);
        verify(assignmentRepository).save(asgnCaptor.capture());
        MultiplayerPuzzleAssignmentJpaEntity asgn = asgnCaptor.getValue();
        assertThat(asgn.getAssignmentId()).isEqualTo("asgn_01");
        assertThat(asgn.getMatchId()).isEqualTo("match_01");
        assertThat(asgn.getPuzzleId()).isEqualTo(puzzleId);

        // 3. Player history records saved for both participants
        ArgumentCaptor<MultiplayerPlayerPuzzleHistoryJpaEntity> histCaptor =
                ArgumentCaptor.forClass(MultiplayerPlayerPuzzleHistoryJpaEntity.class);
        verify(historyRepository, times(2)).save(histCaptor.capture());
        List<MultiplayerPlayerPuzzleHistoryJpaEntity> savedHist = histCaptor.getAllValues();
        assertThat(savedHist).extracting(MultiplayerPlayerPuzzleHistoryJpaEntity::getPlayerId)
                .containsExactlyInAnyOrder("player_1", "player_2");
    }

    @Test
    @DisplayName("getRecentPuzzleFingerprintsForPlayer queries history repository")
    void testGetRecentPuzzleFingerprintsForPlayer() {
        when(historyRepository.findRecentFingerprints(eq("player_1"), any()))
                .thenReturn(List.of("fp_1", "fp_2"));

        List<String> result = adapter.getRecentPuzzleFingerprintsForPlayer("player_1", 5);

        assertThat(result).containsExactly("fp_1", "fp_2");
    }
}
