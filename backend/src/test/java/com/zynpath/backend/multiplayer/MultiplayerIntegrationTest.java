package com.zynpath.backend.multiplayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.ReconnectionSnapshot;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import com.zynpath.backend.multiplayer.service.MatchmakingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MultiplayerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @Autowired
    private MatchmakingService matchmakingService;

    @Autowired
    private MatchSessionService matchSessionService;

    @Autowired
    private com.zynpath.backend.social.service.SocialService socialService;

    private record TestPlayer(PlayerAccount account, PlayerSession session) {}

    private TestPlayer createPlayer(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        PlayerSession session = sessionSecurityService.createSession(account.playerId());
        return new TestPlayer(account, session);
    }

    @Test
    @DisplayName("Quick Duel matchmaking pairs 2 eligible players and issues identical shared puzzle")
    void quickDuelMatchmaking_twoPlayers_pairedWithSharedPuzzle() throws Exception {
        TestPlayer p1 = createPlayer("sub_qd_p1", "Duelist One");
        TestPlayer p2 = createPlayer("sub_qd_p2", "Duelist Two");

        // Player 1 enqueues -> state SEARCHING
        mockMvc.perform(post("/api/v1/multiplayer/matchmaking/quick-duel/enqueue")
                .header("Authorization", "Bearer " + p1.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SEARCHING"))
                .andExpect(jsonPath("$.ticketId").isNotEmpty());

        // Player 2 enqueues -> MATCH_FOUND
        String p2Response = mockMvc.perform(post("/api/v1/multiplayer/matchmaking/quick-duel/enqueue")
                .header("Authorization", "Bearer " + p2.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MATCH_FOUND"))
                .andExpect(jsonPath("$.matchId").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> p2Map = objectMapper.readValue(p2Response, Map.class);
        String matchId = (String) p2Map.get("matchId");

        // Player 1 polls status -> MATCH_FOUND with same matchId
        mockMvc.perform(get("/api/v1/multiplayer/matchmaking/quick-duel/status")
                .header("Authorization", "Bearer " + p1.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MATCH_FOUND"))
                .andExpect(jsonPath("$.matchId").value(matchId));

        // Both players inspect match snapshot -> identical puzzle
        String p1SnapshotJson = mockMvc.perform(get("/api/v1/multiplayer/matches/" + matchId)
                .header("Authorization", "Bearer " + p1.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andExpect(jsonPath("$.puzzleAssignment").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        String p2SnapshotJson = mockMvc.perform(get("/api/v1/multiplayer/matches/" + matchId)
                .header("Authorization", "Bearer " + p2.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andReturn().getResponse().getContentAsString();

        ReconnectionSnapshot s1 = objectMapper.readValue(p1SnapshotJson, ReconnectionSnapshot.class);
        ReconnectionSnapshot s2 = objectMapper.readValue(p2SnapshotJson, ReconnectionSnapshot.class);

        assertThat(s1.puzzleAssignment().puzzleId()).isEqualTo(s2.puzzleAssignment().puzzleId());
        assertThat(s1.puzzleAssignment().fingerprint()).isEqualTo(s2.puzzleAssignment().fingerprint());
        assertThat(s1.puzzleAssignment().gridRows()).isEqualTo(s2.puzzleAssignment().gridRows());
        assertThat(s1.puzzleAssignment().gridCols()).isEqualTo(s2.puzzleAssignment().gridCols());
        assertThat(s1.puzzleAssignment().checkpoints().size()).isEqualTo(s2.puzzleAssignment().checkpoints().size());
    }

    @Test
    @DisplayName("Quick Duel cancel removes waiting player from queue")
    void quickDuelCancel_removesFromQueue() throws Exception {
        TestPlayer player = createPlayer("sub_qd_cancel", "Canceller");

        mockMvc.perform(post("/api/v1/multiplayer/matchmaking/quick-duel/enqueue")
                .header("Authorization", "Bearer " + player.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SEARCHING"));

        mockMvc.perform(post("/api/v1/multiplayer/matchmaking/quick-duel/cancel")
                .header("Authorization", "Bearer " + player.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelled").value(true));
    }

    @Test
    @DisplayName("Friend Duel invitation flow: create invitation, accept, enter match")
    void friendDuel_inviteAndAccept_createsMatch() throws Exception {
        TestPlayer host = createPlayer("sub_fd_host", "Friend Host");
        TestPlayer friend = createPlayer("sub_fd_friend", "Friend Invitee");

        // Establish friendship first
        com.zynpath.backend.social.model.FriendRequest fr = socialService.sendFriendRequest(host.account().playerId(), friend.account().publicZynpathId());
        socialService.acceptFriendRequest(friend.account().playerId(), fr.requestId());

        // Send invite
        MultiplayerDto.FriendDuelInvitationRequest inviteReq = new MultiplayerDto.FriendDuelInvitationRequest(friend.account().publicZynpathId());
        String inviteResp = mockMvc.perform(post("/api/v1/multiplayer/invitations")
                .header("Authorization", "Bearer " + host.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inviteReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invitationId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> inviteMap = objectMapper.readValue(inviteResp, Map.class);
        String invitationId = (String) inviteMap.get("invitationId");

        // Friend accepts
        mockMvc.perform(post("/api/v1/multiplayer/invitations/" + invitationId + "/accept")
                .header("Authorization", "Bearer " + friend.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId").isNotEmpty())
                .andExpect(jsonPath("$.gameMode").value("FRIEND_DUEL"));
    }

    @Test
    @DisplayName("Mini League room creation, joining by code, readying up, and start requirements")
    void miniLeague_roomLifecycle() throws Exception {
        TestPlayer host = createPlayer("sub_ml_host", "League Host");
        TestPlayer player2 = createPlayer("sub_ml_p2", "League Player 2");

        // 1. Create Mini League room (3 max players)
        MultiplayerDto.CreateMiniLeagueRequest createReq = new MultiplayerDto.CreateMiniLeagueRequest("Champion Cup", 3);
        String roomResp = mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms")
                .header("Authorization", "Bearer " + host.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").isNotEmpty())
                .andExpect(jsonPath("$.roomCode").isNotEmpty())
                .andExpect(jsonPath("$.maxParticipants").value(3))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> roomMap = objectMapper.readValue(roomResp, Map.class);
        String roomId = (String) roomMap.get("roomId");
        String roomCode = (String) roomMap.get("roomCode");

        // 2. Player 2 joins room by code
        MultiplayerDto.JoinRoomRequest joinReq = new MultiplayerDto.JoinRoomRequest(roomCode);
        mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms/join")
                .header("Authorization", "Bearer " + player2.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants.length()").value(2));

        // 3. Ready up
        mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms/" + roomId + "/ready")
                .header("Authorization", "Bearer " + host.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MultiplayerDto.UpdateReadyRequest(true))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms/" + roomId + "/ready")
                .header("Authorization", "Bearer " + player2.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MultiplayerDto.UpdateReadyRequest(true))))
                .andExpect(status().isOk());

        // 4. Host starts match
        mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms/" + roomId + "/start")
                .header("Authorization", "Bearer " + host.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameMode").value("MINI_LEAGUE"))
                .andExpect(jsonPath("$.puzzleAssignment").isNotEmpty());
    }

    @Test
    @DisplayName("Mini League room rejects invalid capacity outside 2..5 range")
    void miniLeague_invalidCapacity_rejected() throws Exception {
        TestPlayer host = createPlayer("sub_ml_bad_cap", "Bad Cap Host");

        // Request 1 player room (too small)
        MultiplayerDto.CreateMiniLeagueRequest req1 = new MultiplayerDto.CreateMiniLeagueRequest("Small", 1);
        mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms")
                .header("Authorization", "Bearer " + host.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isBadRequest());

        // Request 10 players room (too large)
        MultiplayerDto.CreateMiniLeagueRequest req2 = new MultiplayerDto.CreateMiniLeagueRequest("Huge", 10);
        mockMvc.perform(post("/api/v1/multiplayer/mini-league/rooms")
                .header("Authorization", "Bearer " + host.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Competitive match history and leaderboards are accessible")
    void competitiveHistoryAndLeaderboards_accessible() throws Exception {
        TestPlayer player = createPlayer("sub_comp_hist", "Comp Viewer");

        // Match history
        mockMvc.perform(get("/api/v1/multiplayer/history")
                .header("Authorization", "Bearer " + player.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());

        // Player competitive stats
        mockMvc.perform(get("/api/v1/multiplayer/stats")
                .header("Authorization", "Bearer " + player.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(player.account().playerId()))
                .andExpect(jsonPath("$.quickDuelWins").isNumber());

        // Global leaderboards
        mockMvc.perform(get("/api/v1/multiplayer/leaderboard")
                .param("category", "QUICK_DUEL_WINS")
                .param("period", "ALL_TIME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("QUICK_DUEL_WINS"))
                .andExpect(jsonPath("$.entries").isArray());
    }
}
