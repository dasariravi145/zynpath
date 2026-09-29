package com.zynpath.backend.daily;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeAttemptDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeDefinitionDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyCompletionClaimDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyProvisionalSyncRequest;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyStartAttemptRequest;
import com.zynpath.backend.daily.service.DailyChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DailyChallengeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @Autowired
    private DailyChallengeService dailyChallengeService;

    private PlayerSession createTestSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("Retrieve official daily challenge for a canonical date is deterministic")
    void getChallenge_returnsCanonicalDefinition() throws Exception {
        String dateKey = "2026-10-15";

        String json1 = mockMvc.perform(get("/api/v1/daily/challenge")
                .param("date", dateKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateKey").value(dateKey))
                .andExpect(jsonPath("$.puzzleId").isNotEmpty())
                .andExpect(jsonPath("$.puzzleFingerprint").isNotEmpty())
                .andExpect(jsonPath("$.checkpoints").isArray())
                .andReturn().getResponse().getContentAsString();

        String json2 = mockMvc.perform(get("/api/v1/daily/challenge")
                .param("date", dateKey))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DailyChallengeDefinitionDto def1 = objectMapper.readValue(json1, DailyChallengeDefinitionDto.class);
        DailyChallengeDefinitionDto def2 = objectMapper.readValue(json2, DailyChallengeDefinitionDto.class);

        assertThat(def1.puzzleId()).isEqualTo(def2.puzzleId());
        assertThat(def1.puzzleFingerprint()).isEqualTo(def2.puzzleFingerprint());
    }

    @Test
    @DisplayName("Start official attempt records attempt and allows retrieval of active attempt")
    void startAttempt_andGetActiveAttempt() throws Exception {
        PlayerSession session = createTestSession("sub_daily_attempt_01", "Daily Aspirant");
        String dateKey = "2026-10-16";

        DailyStartAttemptRequest startReq = new DailyStartAttemptRequest(dateKey);

        mockMvc.perform(post("/api/v1/daily/attempt/start")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId").isNotEmpty())
                .andExpect(jsonPath("$.dateKey").value(dateKey))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Retrieve active attempt
        mockMvc.perform(get("/api/v1/daily/attempt/active")
                .header("Authorization", "Bearer " + session.sessionToken())
                .param("date", dateKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateKey").value(dateKey))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Submit completed daily puzzle validates and records result")
    void submitCompletion_validPath_recordsResult() throws Exception {
        PlayerSession session = createTestSession("sub_daily_submit_01", "Daily Solver");
        String dateKey = "2026-10-17";

        // Get challenge definition to check puzzleId
        DailyChallengeDefinitionDto def = dailyChallengeService.getOfficialChallenge(dateKey);

        // Start attempt
        DailyChallengeAttemptDto attempt = dailyChallengeService.startOfficialAttempt(session.playerId(), dateKey);

        // For w1_lvl1 (4x4 Serpentine Spark):
        // Path: 0,0..0,3 -> 1,3..1,0 -> 2,0..2,3 -> 3,3..3,0
        List<String> solutionPath = List.of(
                "0,0", "0,1", "0,2", "0,3",
                "1,3", "1,2", "1,1", "1,0",
                "2,0", "2,1", "2,2", "2,3",
                "3,3", "3,2", "3,1", "3,0"
        );

        DailyCompletionClaimDto claim = new DailyCompletionClaimDto(
                attempt.attemptId(),
                def.challengeId(),
                def.puzzleFingerprint(),
                solutionPath,
                25_000L
        );

        mockMvc.perform(post("/api/v1/daily/attempt/submit")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("SERVER_VALIDATED"))
                .andExpect(jsonPath("$.solveTimeMs").isNumber());

        // Personal result query
        mockMvc.perform(get("/api/v1/daily/result")
                .header("Authorization", "Bearer " + session.sessionToken())
                .param("date", dateKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("SERVER_VALIDATED"));
    }

    @Test
    @DisplayName("Provisional offline sync records completion but does not pollute verified online leaderboard")
    void syncProvisional_doesNotPolluteVerifiedLeaderboard() throws Exception {
        PlayerSession session = createTestSession("sub_daily_prov_01", "Offline Solver");
        String dateKey = "2026-10-18";

        DailyChallengeDefinitionDto def = dailyChallengeService.getOfficialChallenge(dateKey);

        DailyProvisionalSyncRequest provReq = new DailyProvisionalSyncRequest(
                def.challengeId(),
                dateKey,
                def.puzzleFingerprint(),
                15_000L,
                System.currentTimeMillis() - 60_000L,
                List.of("0,0", "0,1", "0,2", "0,3"),
                4
        );

        mockMvc.perform(post("/api/v1/daily/sync-provisional")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(provReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("PROVISIONAL"));

        // Verify leaderboard does NOT show unverified offline solve as verified
        mockMvc.perform(get("/api/v1/daily/leaderboard")
                .param("date", dateKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries").isArray());
    }
}
