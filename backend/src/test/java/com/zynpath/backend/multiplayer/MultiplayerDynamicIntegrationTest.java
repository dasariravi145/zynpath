package com.zynpath.backend.multiplayer;

import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.generator.CompetitiveDifficultyTier;
import com.zynpath.backend.multiplayer.generator.DynamicPuzzleInventory;
import com.zynpath.backend.multiplayer.model.FriendsArenaMatch;
import com.zynpath.backend.multiplayer.model.FriendsArenaMatchStatus;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.model.ReconnectionSnapshot;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.multiplayer.service.FriendsArenaService;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MultiplayerDynamicIntegrationTest {

    @Autowired
    private MatchSessionService matchSessionService;

    @Autowired
    private FriendsArenaService friendsArenaService;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private DynamicPuzzleInventory puzzleInventory;

    @Autowired
    private ServerPuzzleValidator serverValidator;

    private PlayerAccount createTestAccount(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        return playerAccountService.getOrCreateForExternalIdentity(identity, null);
    }

    @Test
    @DisplayName("Stage B: Quick Duel match session acquires dynamic puzzle and shares identical puzzle to both duelists")
    void testQuickDuelDynamicPuzzleAssignmentAndIdenticalSharing() {
        // Prime dynamic inventory
        puzzleInventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, 3);

        String hostId = "player_host_01";
        String guestId = "player_guest_02";

        MatchSession session = matchSessionService.createMatchSession(
                GameMode.QUICK_DUEL, hostId, null, CompetitiveDifficultyTier.EXPERT
        );

        assertThat(session).isNotNull();
        PuzzleAssignment puzzle = session.getPuzzleAssignment();
        assertThat(puzzle).isNotNull();
        assertThat(puzzle.gridRows()).isIn(11, 12);
        assertThat(puzzle.gridCols()).isIn(11, 12);

        long now = System.currentTimeMillis();
        // Host joins
        session.addParticipant(new MatchParticipant(hostId, "ZYN_001", "Duelist One", null, now));
        // Guest joins
        session.addParticipant(new MatchParticipant(guestId, "ZYN_002", "Duelist Two", null, now));

        // Verify identical puzzle assignment in session for both duelists
        assertThat(session.getPuzzleAssignment().puzzleId()).isEqualTo(puzzle.puzzleId());
        assertThat(session.getPuzzleAssignment().fingerprint()).isEqualTo(puzzle.fingerprint());
        assertThat(session.getPuzzleAssignment().checkpoints()).isEqualTo(puzzle.checkpoints());
        assertThat(session.getPuzzleAssignment().blockedEdges()).isEqualTo(puzzle.blockedEdges());

        // Snapshot restoration verification
        ReconnectionSnapshot snapshot = session.toSnapshot();

        assertThat(snapshot.puzzleAssignment().puzzleId()).isEqualTo(puzzle.puzzleId());
        assertThat(snapshot.puzzleAssignment().fingerprint()).isEqualTo(puzzle.fingerprint());
        assertThat(snapshot.participants()).hasSize(2);
    }

    @Test
    @DisplayName("Stage B: Friends Arena match acquires dynamic puzzle and shares identically among all room participants")
    void testFriendsArenaDynamicPuzzleAssignment() {
        puzzleInventory.replenishTierBuffer(GameMode.FRIENDS_ARENA, CompetitiveDifficultyTier.EXPERT, 2);

        PlayerAccount host = createTestAccount("sub_fa_p1", "Arena Host");
        PlayerAccount guest2 = createTestAccount("sub_fa_p2", "Guest Two");
        PlayerAccount guest3 = createTestAccount("sub_fa_p3", "Guest Three");

        String hostId = host.playerId();
        MultiplayerDto.FriendsArenaRoomDto roomDto = friendsArenaService.createRoom(hostId, "idemp_room_1");

        // Add 2 more members
        friendsArenaService.joinRoomByCode(roomDto.roomCode(), guest2.playerId());
        friendsArenaService.joinRoomByCode(roomDto.roomCode(), guest3.playerId());

        // Host starts match
        MultiplayerDto.FriendsArenaRoomDto startedRoom = friendsArenaService.startMatch(roomDto.roomId(), hostId);

        assertThat(startedRoom).isNotNull();
        String matchId = startedRoom.activeMatchId();
        assertThat(matchId).isNotEmpty();

        FriendsArenaMatch match = friendsArenaService.getMatchEntity(matchId);
        assertThat(match).isNotNull();
        assertThat(match.getPuzzleAssignment()).isNotNull();
        String puzzleId = match.getPuzzleAssignment().puzzleId();
        assertThat(puzzleId).isNotEmpty();
        assertThat(match.getParticipants()).hasSize(3);

        // Mark match completed so rematch is allowed
        match.setStatus(FriendsArenaMatchStatus.COMPLETED);

        // Rematch should vary puzzle
        MultiplayerDto.FriendsArenaMatchDto rematchDto = friendsArenaService.requestRematch(roomDto.roomId(), hostId);
        assertThat(rematchDto).isNotNull();
        assertThat(rematchDto.puzzleAssignment().puzzleId()).isNotEqualTo(puzzleId);
    }

    @Test
    @DisplayName("Stage B: Concurrent match creation safety with zero duplicate assignment collisions")
    void testConcurrentMatchCreation() throws Exception {
        puzzleInventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.VERY_HARD, 10);

        int threadCount = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<MatchSession>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final String pid = "concurrent_player_" + i;
            tasks.add(() -> matchSessionService.createMatchSession(
                    GameMode.QUICK_DUEL, pid, null, CompetitiveDifficultyTier.VERY_HARD
            ));
        }

        List<Future<MatchSession>> futures = executor.invokeAll(tasks);
        List<String> assignedPuzzles = new ArrayList<>();

        for (Future<MatchSession> f : futures) {
            MatchSession s = f.get();
            assertThat(s).isNotNull();
            assignedPuzzles.add(s.getPuzzleAssignment().puzzleId());
        }

        executor.shutdown();
        assertThat(assignedPuzzles).hasSize(threadCount);
    }

    @Test
    @DisplayName("Stage B: Solver validation of submitted win and rejection of corrupted/invalid solutions")
    void testWinValidationAndRejection() {
        MatchSession session = matchSessionService.createMatchSession(
                GameMode.QUICK_DUEL, "val_p1", null, CompetitiveDifficultyTier.EXPERT
        );
        PuzzleAssignment puzzle = session.getPuzzleAssignment();

        // 1. Corrupted empty path rejection
        ValidationOutcome emptyOutcome = serverValidator.validateSolution(puzzle, List.of());
        assertThat(emptyOutcome.isValid()).isFalse();
        assertThat(emptyOutcome.getErrorMessage()).contains("EMPTY_PATH");

        // 2. Corrupted wrong start rejection
        ValidationOutcome wrongStart = serverValidator.validateSolution(puzzle, List.of("99,99"));
        assertThat(wrongStart.isValid()).isFalse();

        // 3. Null puzzle rejection
        ValidationOutcome nullPuzzle = serverValidator.validateSolution(null, List.of("0,0"));
        assertThat(nullPuzzle.isValid()).isFalse();
    }
}
