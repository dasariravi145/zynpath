package com.zynpath.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.GuestAuthRequest;
import com.zynpath.backend.auth.model.PersistedSession;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import com.zynpath.backend.persistence.port.SessionPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @Autowired
    private SessionPersistencePort sessionPersistencePort;

    @Autowired
    private MatchSessionService matchSessionService;

    private PlayerSession createSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("1. Unknown guest-prefixed token rejected (no implicit backdoor)")
    void unknownGuestPrefixedToken_isRejected() throws Exception {
        // Test tokens starting with guest prefixes that previously exploited lines 73-79
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer zyn_guest_victim_uuid_001"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));

        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer guest_session_fake_123"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));

        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer guest_arbitrary_456"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));
    }

    @Test
    @DisplayName("2. Forged bearer token rejected")
    void forgedBearerToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer zyn_forged_random_token_99999"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));
    }

    @Test
    @DisplayName("3. Valid server-issued token accepted")
    void validServerIssuedToken_isAccepted() throws Exception {
        PlayerSession session = createSession("sub_valid_001", "Valid User");
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Valid User"));
    }

    @Test
    @DisplayName("4. Expired session rejected")
    void expiredSession_isRejected() throws Exception {
        PlayerAccount account = playerAccountService.getOrCreateGuestAccount(null, "Expiring Guest");
        String expiredToken = "zyn_expired_test_token_" + UUID.randomUUID();
        String tokenHash = SessionSecurityService.hashToken(expiredToken);
        long now = System.currentTimeMillis();

        PersistedSession expiredPersisted = new PersistedSession(
                UUID.randomUUID().toString(),
                tokenHash,
                account.playerId(),
                now - 100000,
                now - 1000, // expired 1s ago
                now - 1000,
                false,
                null,
                null
        );
        sessionPersistencePort.save(expiredPersisted);

        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));
    }

    @Test
    @DisplayName("5. Revoked session rejected")
    void revokedSession_isRejected() throws Exception {
        PlayerSession session = createSession("sub_revoked_001", "Revoked User");
        sessionSecurityService.revokeSession(session.sessionToken());

        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));
    }

    @Test
    @DisplayName("6. Session persists across service reinitialization via backing store")
    void sessionPersistsAcrossServiceReinitialization() {
        PlayerAccount account = playerAccountService.getOrCreateGuestAccount(null, "Persistent Guest");
        PlayerSession originalSession = sessionSecurityService.createSession(account.playerId());

        // Reinitialize a new SessionSecurityService instance pointing to the same backing port
        SessionSecurityService reinitializedService = new SessionSecurityService(
                2592000L,
                playerAccountService,
                sessionPersistencePort
        );

        PlayerSession validated = reinitializedService.validateSession(originalSession.sessionToken());
        assertThat(validated).isNotNull();
        assertThat(validated.playerId()).isEqualTo(account.playerId());
    }

    @Test
    @DisplayName("7. Refresh rotates token and invalidates the previous token")
    void refreshRotatesTokenAndInvalidatesPrevious() throws Exception {
        PlayerSession session = createSession("sub_refresh_rot_01", "Refresh User");
        String originalToken = session.sessionToken();

        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh")
                .header("Authorization", "Bearer " + originalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").isNotEmpty())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String newToken = objectMapper.readTree(responseJson).get("sessionToken").asText();
        assertThat(newToken).isNotEqualTo(originalToken);

        // Previous token must be revoked and rejected
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + originalToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));

        // New token must succeed
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("8. Logout revokes session durably")
    void logoutRevokesSession() throws Exception {
        PlayerSession session = createSession("sub_logout_001", "Logout User");

        mockMvc.perform(post("/api/v1/auth/signout")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SIGNED_OUT"));

        // Subsequent call must fail
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));
    }

    @Test
    @DisplayName("9. Public route matching does not expose unintended endpoints")
    void publicRouteMatching_doesNotExposeUnintendedEndpoints() throws Exception {
        // /auth/refresh without valid token must return 401
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized());

        // /wallet endpoints without auth must return 401
        mockMvc.perform(get("/api/v1/wallet/balance"))
                .andExpect(status().isUnauthorized());

        // /sync endpoints without auth must return 401
        mockMvc.perform(post("/api/v1/sync/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());

        // /content/packs/download without auth must return 401
        mockMvc.perform(get("/api/v1/content/packs/pack_geometry_01/download"))
                .andExpect(status().isUnauthorized());

        // /actuator/metrics without auth must be inaccessible (never 200)
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertThat(status).isIn(401, 404);
                });

        // /actuator/health is explicitly whitelisted and returns 200
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("10. Guest account impersonation prevented (cannot claim existing guest account without session)")
    void guestAccountImpersonation_prevented() throws Exception {
        // Initial guest creation
        MvcResult firstResult = mockMvc.perform(post("/api/v1/auth/guest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayName\":\"Original Player\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String originalPlayerId = objectMapper.readTree(firstResult.getResponse().getContentAsString()).get("playerId").asText();

        // Attacker attempts to claim original account by supplying originalPlayerId as guestUuid without session
        GuestAuthRequest attackRequest = new GuestAuthRequest(originalPlayerId, "Imposter");
        MvcResult secondResult = mockMvc.perform(post("/api/v1/auth/guest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attackRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String attackerAssignedPlayerId = objectMapper.readTree(secondResult.getResponse().getContentAsString()).get("playerId").asText();

        // Must NOT assign the victim's player ID to unauthenticated attacker
        assertThat(attackerAssignedPlayerId).isNotEqualTo(originalPlayerId);
    }

    @Test
    @DisplayName("11. BOLA: Non-participant player cannot access private match session snapshot (403)")
    void brokenObjectLevelAuthorization_nonParticipant_isForbidden() throws Exception {
        PlayerSession participantSession = createSession("sub_bola_part", "Legit Participant");
        PlayerSession attackerSession = createSession("sub_bola_attacker", "Snooping Attacker");

        MatchSession match = matchSessionService.createMatchSession(GameMode.FRIEND_DUEL, participantSession.playerId());

        mockMvc.perform(get("/api/v1/multiplayer/matches/" + match.getMatchId())
                .header("Authorization", "Bearer " + attackerSession.sessionToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("12. Existing valid authentication flows remain functional")
    void existingValidAuthFlows_remainFunctional() throws Exception {
        MvcResult guestRes = mockMvc.perform(post("/api/v1/auth/guest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").isNotEmpty())
                .andExpect(jsonPath("$.playerId").isNotEmpty())
                .andExpect(jsonPath("$.publicZynpathId").isNotEmpty())
                .andExpect(jsonPath("$.accountType").value("GUEST"))
                .andReturn();

        String token = objectMapper.readTree(guestRes.getResponse().getContentAsString()).get("sessionToken").asText();

        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("GUEST"));
    }
}
