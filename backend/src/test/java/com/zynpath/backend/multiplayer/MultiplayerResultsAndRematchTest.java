package com.zynpath.backend.multiplayer;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.multiplayer.generator.CompetitiveDifficultyTier;
import com.zynpath.backend.multiplayer.generator.DynamicPuzzleInventory;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.social.model.InvitationStatus;
import com.zynpath.backend.multiplayer.model.MatchParticipant;
import com.zynpath.backend.multiplayer.model.MatchResult;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MatchState;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.service.FriendDuelService;
import com.zynpath.backend.multiplayer.service.FriendsArenaService;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MultiplayerResultsAndRematchTest {

    @Autowired
    private MatchSessionService matchSessionService;

    @Autowired
    private FriendDuelService friendDuelService;

    @Autowired
    private FriendsArenaService friendsArenaService;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private DynamicPuzzleInventory puzzleInventory;

    private PlayerAccount createTestAccount(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        return playerAccountService.getOrCreateForExternalIdentity(identity, null);
    }

    private void transitionSessionToActive(MatchSession session) {
        session.transitionTo(MatchState.READY);
        session.transitionTo(MatchState.COUNTDOWN);
        session.transitionTo(MatchState.ACTIVE);
    }

    @Test
    @DisplayName("Quick Duel: Authoritative finish timing fixes 330.5s duplicate bug - Winner gets authentic time, runner-up gets null DNF")
    void testQuickDuelDistinctTimingAndNoDuplicateBug() {
        String p1Sub = "p_win_" + UUID.randomUUID().toString().substring(0, 8);
        String p2Sub = "p_lose_" + UUID.randomUUID().toString().substring(0, 8);

        String p1 = createTestAccount(p1Sub, "Winner Player").playerId();
        String p2 = createTestAccount(p2Sub, "Runner Up Player").playerId();

        MatchSession session = matchSessionService.createMatchSession(
                GameMode.QUICK_DUEL, p1, null, CompetitiveDifficultyTier.EXPERT
        );
        long now = System.currentTimeMillis();
        session.addParticipant(new MatchParticipant(p1, "ZYN_W1", "Winner Player", null, now));
        session.addParticipant(new MatchParticipant(p2, "ZYN_L2", "Runner Up Player", null, now));
        transitionSessionToActive(session);

        long winnerSolveTime = 42500L; // 42.5 seconds
        // Winner submits valid completion
        matchSessionService.recordCompletion(session.getMatchId(), p1, winnerSolveTime);

        assertThat(session.getState()).isEqualTo(MatchState.COMPLETED);

        List<MatchResult> results = session.getResults();
        assertThat(results).hasSize(2);

        MatchResult winResult = results.stream().filter(r -> r.playerId().equals(p1)).findFirst().orElseThrow();
        assertThat(winResult.isWinner()).isTrue();
        assertThat(winResult.solveTimeMs()).isEqualTo(winnerSolveTime);
        assertThat(winResult.resultStatus()).isEqualTo("VICTORY");

        // Verify opponent result
        MatchResult opponentResult = results.stream().filter(r -> r.playerId().equals(p2)).findFirst().orElseThrow();
        assertThat(opponentResult.isWinner()).isFalse();
        // Opponent must have null solveTimeMs (DNF), NOT the winner's 42.5s!
        assertThat(opponentResult.solveTimeMs()).isNull();
        assertThat(opponentResult.resultStatus()).isEqualTo("DEFEAT");
    }

    @Test
    @DisplayName("Quick Duel: Forfeit authoritative outcome awards win to remaining player without duplicating elapsed time")
    void testForfeitResultsTiming() {
        String p1Sub = "p_forfeit_" + UUID.randomUUID().toString().substring(0, 8);
        String p2Sub = "p_stay_" + UUID.randomUUID().toString().substring(0, 8);

        String p1 = createTestAccount(p1Sub, "Forfeit Player").playerId();
        String p2 = createTestAccount(p2Sub, "Staying Player").playerId();

        MatchSession session = matchSessionService.createMatchSession(
                GameMode.QUICK_DUEL, p1, null, CompetitiveDifficultyTier.EXPERT
        );
        long now = System.currentTimeMillis();
        session.addParticipant(new MatchParticipant(p1, "ZYN_F1", "Forfeit Player", null, now));
        session.addParticipant(new MatchParticipant(p2, "ZYN_S2", "Staying Player", null, now));
        transitionSessionToActive(session);

        // Player 1 forfeits
        matchSessionService.forfeitMatch(session.getMatchId(), p1);

        assertThat(session.getState()).isEqualTo(MatchState.COMPLETED);

        List<MatchResult> results = session.getResults();
        assertThat(results).hasSize(2);

        MatchResult forfeiterRes = results.stream().filter(r -> r.playerId().equals(p1)).findFirst().orElseThrow();
        assertThat(forfeiterRes.isWinner()).isFalse();
        assertThat(forfeiterRes.resultStatus()).isEqualTo("ABANDONED");
        assertThat(forfeiterRes.solveTimeMs()).isNull();

        MatchResult winnerRes = results.stream().filter(r -> r.playerId().equals(p2)).findFirst().orElseThrow();
        assertThat(winnerRes.isWinner()).isTrue();
        assertThat(winnerRes.resultStatus()).isEqualTo("VICTORY_BY_FORFEIT");
    }

    @Test
    @DisplayName("Quick Duel: Server-authoritative Rematch state machine - Request, Cancel, Accept, and Fresh Puzzle")
    void testQuickDuelRematchLifecycle() {
        puzzleInventory.replenishTierBuffer(GameMode.QUICK_DUEL, CompetitiveDifficultyTier.EXPERT, 5);

        String p1Sub = "p_rem1_" + UUID.randomUUID().toString().substring(0, 8);
        String p2Sub = "p_rem2_" + UUID.randomUUID().toString().substring(0, 8);

        String p1 = createTestAccount(p1Sub, "Rematch Alpha").playerId();
        String p2 = createTestAccount(p2Sub, "Rematch Beta").playerId();

        MatchSession session = matchSessionService.createMatchSession(
                GameMode.QUICK_DUEL, p1, null, CompetitiveDifficultyTier.EXPERT
        );
        long now = System.currentTimeMillis();
        session.addParticipant(new MatchParticipant(p1, "ZYN_R1", "Rematch Alpha", null, now));
        session.addParticipant(new MatchParticipant(p2, "ZYN_R2", "Rematch Beta", null, now));
        transitionSessionToActive(session);
        matchSessionService.recordCompletion(session.getMatchId(), p1, 35000L);

        String oldPuzzleId = session.getPuzzleAssignment().puzzleId();

        // 1. Player 1 requests rematch
        MultiplayerDto.RematchStatusDto reqStatus = friendDuelService.requestRematch(session.getMatchId(), p1);
        assertThat(reqStatus.status()).isEqualTo("PENDING");
        assertThat(reqStatus.requesterPlayerId()).isEqualTo(p1);

        // 2. Duplicate rematch request is idempotent and returns existing
        MultiplayerDto.RematchStatusDto dupStatus = friendDuelService.requestRematch(session.getMatchId(), p1);
        assertThat(dupStatus.status()).isEqualTo("PENDING");

        // 3. Player 1 cancels rematch
        MultiplayerDto.RematchStatusDto cancelStatus = friendDuelService.cancelRematch(session.getMatchId(), p1);
        assertThat(cancelStatus.status()).isEqualTo("CANCELLED");

        // 4. Request rematch again after cancellation
        MultiplayerDto.RematchStatusDto newReq = friendDuelService.requestRematch(session.getMatchId(), p1);
        assertThat(newReq.status()).isEqualTo("PENDING");

        // 5. Player 2 accepts rematch -> creates new match session
        MultiplayerDto.RematchStatusDto acceptStatus = friendDuelService.respondToRematch(session.getMatchId(), p2, true);
        assertThat(acceptStatus.status()).isEqualTo("ACCEPTED");
        assertThat(acceptStatus.newMatchId()).isNotNull();

        // Verify the new match session has fresh puzzle and preserves both players
        MatchSession newSession = matchSessionService.getSession(acceptStatus.newMatchId());
        assertThat(newSession).isNotNull();
        assertThat(newSession.getParticipants()).hasSize(2);
        assertThat(newSession.getPuzzleAssignment()).isNotNull();
        // Fresh puzzle verification: must not reuse the immediate previous puzzle
        assertThat(newSession.getPuzzleAssignment().puzzleId()).isNotEqualTo(oldPuzzleId);
    }

    @Test
    @DisplayName("Friends Arena: Next Round Readiness tracking, minimum participants rule, and fresh puzzle progression")
    void testFriendsArenaReadinessAndNextRound() {
        puzzleInventory.replenishTierBuffer(GameMode.FRIENDS_ARENA, CompetitiveDifficultyTier.EXPERT, 5);

        String hostSub = "fa_host_" + UUID.randomUUID().toString().substring(0, 8);
        String g1Sub = "fa_g1_" + UUID.randomUUID().toString().substring(0, 8);
        String g2Sub = "fa_g2_" + UUID.randomUUID().toString().substring(0, 8);

        String host = createTestAccount(hostSub, "Arena Host").playerId();
        String g1 = createTestAccount(g1Sub, "Arena Guest One").playerId();
        String g2 = createTestAccount(g2Sub, "Arena Guest Two").playerId();

        // Create Room
        MultiplayerDto.FriendsArenaRoomDto room = friendsArenaService.createRoom(host, null);
        friendsArenaService.joinRoomByCode(room.roomCode(), g1);
        friendsArenaService.joinRoomByCode(room.roomCode(), g2);

        // Host starts Round 1
        MultiplayerDto.FriendsArenaRoomDto startedRoom = friendsArenaService.startMatch(room.roomId(), host);
        assertThat(startedRoom.activeMatchId()).isNotNull();

        MultiplayerDto.FriendsArenaMatchDto match1 = friendsArenaService.getMatch(startedRoom.activeMatchId(), host);
        assertThat(match1).isNotNull();
        String puzzleRound1 = match1.puzzleAssignment().puzzleId();

        // Conclude Round 1 match
        friendsArenaService.concludeMatch(match1.matchId());

        // 1. Only 1 player ready -> Host start must fail (min 2 required)
        friendsArenaService.setReady(room.roomId(), host, true);
        assertThatThrownBy(() -> friendsArenaService.requestRematch(room.roomId(), host))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("At least 2 ready players");

        // 2. Guest 1 marks ready -> Now 2 ready out of 3
        friendsArenaService.setReady(room.roomId(), g1, true);

        // Guest 2 is NOT ready -> remains in room but not active in round 2
        // 3. Host starts Round 2 with 2 ready players
        MultiplayerDto.FriendsArenaMatchDto match2 = friendsArenaService.requestRematch(room.roomId(), host);
        assertThat(match2).isNotNull();
        // Roster frozen to only ready participants
        assertThat(match2.participants()).hasSize(2);
        assertThat(match2.participants().stream().anyMatch(p -> p.playerId().equals(host))).isTrue();
        assertThat(match2.participants().stream().anyMatch(p -> p.playerId().equals(g1))).isTrue();
        assertThat(match2.participants().stream().anyMatch(p -> p.playerId().equals(g2))).isFalse();

        // Fresh puzzle progression: must not reuse previous puzzle
        assertThat(match2.puzzleAssignment().puzzleId()).isNotEqualTo(puzzleRound1);
    }
}
