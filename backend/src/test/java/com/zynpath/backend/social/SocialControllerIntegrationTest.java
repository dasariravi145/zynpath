package com.zynpath.backend.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.social.model.SocialDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SocialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    private record TestUser(PlayerAccount account, PlayerSession session) {}

    private TestUser createTestUser(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        PlayerSession session = sessionSecurityService.createSession(account.playerId());
        return new TestUser(account, session);
    }

    @Test
    @DisplayName("Search player by public Zynpath ID returns public profile")
    void searchPlayer_validPublicId_returnsPublicProfile() throws Exception {
        TestUser user1 = createTestUser("sub_social_user1", "Search Subject");
        TestUser user2 = createTestUser("sub_social_user2", "Searcher");

        mockMvc.perform(get("/api/v1/social/players/search")
                .header("Authorization", "Bearer " + user2.session().sessionToken())
                .param("publicId", user1.account().publicZynpathId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publicZynpathId").value(user1.account().publicZynpathId()))
                .andExpect(jsonPath("$.displayName").value("Search Subject"));
    }

    @Test
    @DisplayName("Search with invalid/unknown public Zynpath ID returns 404")
    void searchPlayer_unknownPublicId_returnsNotFound() throws Exception {
        TestUser user = createTestUser("sub_social_srch_err", "Error Searcher");

        mockMvc.perform(get("/api/v1/social/players/search")
                .header("Authorization", "Bearer " + user.session().sessionToken())
                .param("publicId", "ZYN-XXXX-ZZZZ"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Complete friend invitation lifecycle: send, incoming view, accept, mutual friends")
    void friendRequest_sendAndAccept_establishesFriendship() throws Exception {
        TestUser sender = createTestUser("sub_friend_sender", "Sender Alice");
        TestUser receiver = createTestUser("sub_friend_receiver", "Receiver Bob");

        // 1. Sender sends friend request
        SocialDto.SendFriendRequestPayload payload = new SocialDto.SendFriendRequestPayload(receiver.account().publicZynpathId());
        String postResponse = mockMvc.perform(post("/api/v1/social/friends/requests")
                .header("Authorization", "Bearer " + sender.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> respMap = objectMapper.readValue(postResponse, Map.class);
        String requestId = (String) respMap.get("requestId");

        // 2. Receiver checks incoming requests
        mockMvc.perform(get("/api/v1/social/friends/requests/incoming")
                .header("Authorization", "Bearer " + receiver.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value(requestId))
                .andExpect(jsonPath("$[0].otherPublicZynpathId").value(sender.account().publicZynpathId()));

        // 3. Receiver accepts request
        mockMvc.perform(post("/api/v1/social/friends/requests/" + requestId + "/accept")
                .header("Authorization", "Bearer " + receiver.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.establishedAt").isNumber());

        // 4. Both players verify mutual friendship
        mockMvc.perform(get("/api/v1/social/friends")
                .header("Authorization", "Bearer " + sender.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicZynpathId").value(receiver.account().publicZynpathId()));

        mockMvc.perform(get("/api/v1/social/friends")
                .header("Authorization", "Bearer " + receiver.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].publicZynpathId").value(sender.account().publicZynpathId()));
    }

    @Test
    @DisplayName("Rejecting a friend request updates state and does not create friendship")
    void friendRequest_reject_removesRequest() throws Exception {
        TestUser sender = createTestUser("sub_fr_reject_sender", "Rejection Sender");
        TestUser receiver = createTestUser("sub_fr_reject_rcv", "Rejection Receiver");

        SocialDto.SendFriendRequestPayload payload = new SocialDto.SendFriendRequestPayload(receiver.account().publicZynpathId());
        String postResponse = mockMvc.perform(post("/api/v1/social/friends/requests")
                .header("Authorization", "Bearer " + sender.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> respMap = objectMapper.readValue(postResponse, Map.class);
        String requestId = (String) respMap.get("requestId");

        // Receiver rejects request
        mockMvc.perform(post("/api/v1/social/friends/requests/" + requestId + "/reject")
                .header("Authorization", "Bearer " + receiver.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        // Friends list remains empty
        mockMvc.perform(get("/api/v1/social/friends")
                .header("Authorization", "Bearer " + receiver.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Cancelling an outgoing friend request succeeds")
    void friendRequest_cancel_cancelsRequest() throws Exception {
        TestUser sender = createTestUser("sub_fr_cancel_sender", "Canceller");
        TestUser receiver = createTestUser("sub_fr_cancel_rcv", "Cancellee");

        SocialDto.SendFriendRequestPayload payload = new SocialDto.SendFriendRequestPayload(receiver.account().publicZynpathId());
        String postResponse = mockMvc.perform(post("/api/v1/social/friends/requests")
                .header("Authorization", "Bearer " + sender.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> respMap = objectMapper.readValue(postResponse, Map.class);
        String requestId = (String) respMap.get("requestId");

        mockMvc.perform(post("/api/v1/social/friends/requests/" + requestId + "/cancel")
                .header("Authorization", "Bearer " + sender.session().sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("Self friend request is rejected with 400 Bad Request")
    void friendRequest_selfRequest_fails() throws Exception {
        TestUser user = createTestUser("sub_fr_self", "Self Tester");

        SocialDto.SendFriendRequestPayload payload = new SocialDto.SendFriendRequestPayload(user.account().publicZynpathId());
        mockMvc.perform(post("/api/v1/social/friends/requests")
                .header("Authorization", "Bearer " + user.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Blocking a player prevents them from sending friend requests")
    void blocking_playerBlocksTarget_blocksRequests() throws Exception {
        TestUser blocker = createTestUser("sub_blocker", "Blocker");
        TestUser target = createTestUser("sub_blocked", "Blocked");

        // Blocker blocks target
        mockMvc.perform(post("/api/v1/social/blocks/" + target.account().playerId())
                .header("Authorization", "Bearer " + blocker.session().sessionToken()))
                .andExpect(status().isOk());

        // Target attempts to send friend request to blocker -> forbidden / rejected
        SocialDto.SendFriendRequestPayload payload = new SocialDto.SendFriendRequestPayload(blocker.account().publicZynpathId());
        mockMvc.perform(post("/api/v1/social/friends/requests")
                .header("Authorization", "Bearer " + target.session().sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }
}
