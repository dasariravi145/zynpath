package com.zynpath.backend.ads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardRequest;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RewardedAdIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    private PlayerSession createTestSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("Verify valid rewarded ad completion grants +1 hint credit")
    void verifyReward_validRequest_grantsCredit() throws Exception {
        PlayerSession session = createTestSession("sub_ad_grant_01", "Ad Watcher 1");
        String eventId = "evt_" + UUID.randomUUID();
        String txnId = "txn_" + UUID.randomUUID();

        VerifyRewardRequest request = new VerifyRewardRequest(
                eventId,
                txnId,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );

        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(true))
                .andExpect(jsonPath("$.status").value("SERVER_VERIFIED"))
                .andExpect(jsonPath("$.newRewardedCredits").value(1));

        // Balance check
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(1));
    }

    @Test
    @DisplayName("Replaying the exact same reward event ID is idempotent and does not grant duplicate credits")
    void verifyReward_duplicateEventId_isIdempotent() throws Exception {
        PlayerSession session = createTestSession("sub_ad_dup_01", "Ad Watcher 2");
        String eventId = "evt_" + UUID.randomUUID();
        String txnId = "txn_" + UUID.randomUUID();

        VerifyRewardRequest request = new VerifyRewardRequest(
                eventId,
                txnId,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );

        // 1st request -> grants 1
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(true))
                .andExpect(jsonPath("$.newRewardedCredits").value(1));

        // 2nd request (duplicate event ID) -> rejected by anti-replay integrity guard
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("Daily rewarded ad cap limits claims to 5 per 24 hours")
    void verifyReward_exceedsDailyLimit_isRejected() throws Exception {
        PlayerSession session = createTestSession("sub_ad_limit_01", "Ad Watcher 3");

        // Submit 5 valid rewards
        for (int i = 0; i < 5; i++) {
            VerifyRewardRequest request = new VerifyRewardRequest(
                    "evt_limit_" + i + "_" + UUID.randomUUID(),
                    "txn_limit_" + i + "_" + UUID.randomUUID(),
                    "admob_unit_rewarded_001",
                    1,
                    RewardType.SOLO_HINT
            );

            mockMvc.perform(post("/api/v1/ads/reward/verify")
                    .header("Authorization", "Bearer " + session.sessionToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.granted").value(true));
        }

        // 6th reward exceeds daily limit (5) -> rejected
        VerifyRewardRequest overLimitRequest = new VerifyRewardRequest(
                "evt_limit_6_" + UUID.randomUUID(),
                "txn_limit_6_" + UUID.randomUUID(),
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );

        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overLimitRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.granted").value(false))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("AdMob SSV webhook callback accepts valid verification parameters")
    void handleSsvCallback_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("ad_unit", "1234567890")
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", "ssv_txn_" + UUID.randomUUID())
                .param("user_id", "test_user_ssv_001")
                .param("signature", "MEQCIBG3mock_signature_valid")
                .param("key_id", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
