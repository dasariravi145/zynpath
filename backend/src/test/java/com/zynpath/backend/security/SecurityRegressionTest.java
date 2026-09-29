package com.zynpath.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @Autowired
    private MatchSessionService matchSessionService;

    private PlayerSession createSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("Protected account endpoint rejects request without Authorization header (401)")
    void unauthenticatedRequest_toProtectedEndpoint_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/account/settings"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("UNAUTHENTICATED"));
    }

    @Test
    @DisplayName("Protected endpoint rejects invalid/forged bearer token (401)")
    void invalidBearerToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer zyn_forged_invalid_token_99999"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("SESSION_EXPIRED"));
    }

    @Test
    @DisplayName("BOLA: Non-participant player cannot access private match session snapshot (403)")
    void brokenObjectLevelAuthorization_nonParticipant_isForbidden() throws Exception {
        PlayerSession participantSession = createSession("sub_bola_part", "Legit Participant");
        PlayerSession attackerSession = createSession("sub_bola_attacker", "Snooping Attacker");

        // Create match owned by legit participant
        MatchSession match = matchSessionService.createMatchSession(GameMode.FRIEND_DUEL, participantSession.playerId());

        // Attacker attempts to read match snapshot
        mockMvc.perform(get("/api/v1/multiplayer/matches/" + match.getMatchId())
                .header("Authorization", "Bearer " + attackerSession.sessionToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Token privacy: Authentication failure responses do not expose sensitive internal data")
    void tokenPrivacy_noSensitiveExposureInError() throws Exception {
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }
}
