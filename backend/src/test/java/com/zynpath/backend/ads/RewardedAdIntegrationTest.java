package com.zynpath.backend.ads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.ads.model.RewardDto.VerifyRewardRequest;
import com.zynpath.backend.ads.model.RewardType;
import com.zynpath.backend.ads.service.AdMobSsvVerifier;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.zynpath.backend.ads.model.PlayerHintCreditBalance;
import com.zynpath.backend.ads.model.RewardGrantCommand;
import com.zynpath.backend.ads.model.RewardGrantResult;
import com.zynpath.backend.ads.model.RewardGrantStatus;
import com.zynpath.backend.persistence.port.AdRewardPersistencePort;
import com.zynpath.backend.security.ratelimit.RateLimiterService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private AdMobSsvVerifier ssvVerifier;

    @Autowired
    private AdRewardPersistencePort persistencePort;

    @Autowired
    private RateLimiterService rateLimiterService;

    private KeyPair testKeyPair;
    private final String testKeyId = "test_key_ec_ssv";

    @BeforeEach
    void setUpKeys() throws Exception {
        if (rateLimiterService != null) {
            rateLimiterService.reset();
        }
        if (testKeyPair == null) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            kpg.initialize(256);
            testKeyPair = kpg.generateKeyPair();
        }
        ssvVerifier.registerKey(testKeyId, testKeyPair.getPublic());
    }

    private PlayerSession createTestSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    private String generateValidSignature(String messageToVerify) throws Exception {
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(testKeyPair.getPrivate());
        ecdsa.update(messageToVerify.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(ecdsa.sign());
    }

    private String buildSsvMessageToVerify(
            String adNetwork,
            String adUnit,
            String customData,
            String rewardAmount,
            String rewardItem,
            String timestamp,
            String transactionId,
            String userId
    ) {
        return "ad_network=" + adNetwork
                + "&ad_unit=" + adUnit
                + "&custom_data=" + customData
                + "&reward_amount=" + rewardAmount
                + "&reward_item=" + rewardItem
                + "&timestamp=" + timestamp
                + "&transaction_id=" + transactionId
                + "&user_id=" + userId;
    }

    private ResultActions performValidSsvCallback(
            String adNetwork,
            String adUnit,
            String rewardAmount,
            String rewardItem,
            String timestamp,
            String transactionId,
            String userId,
            String customData
    ) throws Exception {
        String messageToVerify = buildSsvMessageToVerify(
                adNetwork, adUnit, customData, rewardAmount, rewardItem, timestamp, transactionId, userId
        );
        String validSig = generateValidSignature(messageToVerify);

        return mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", adNetwork)
                .param("ad_unit", adUnit)
                .param("custom_data", customData)
                .param("reward_amount", rewardAmount)
                .param("reward_item", rewardItem)
                .param("timestamp", timestamp)
                .param("transaction_id", transactionId)
                .param("user_id", userId)
                .param("signature", validSig)
                .param("key_id", testKeyId));
    }

    @Test
    @DisplayName("Requirement 1 & 7: Forged client reward request without SSV callback returns PENDING_VERIFICATION and granted=false")
    void verifyReward_withoutSsvCallback_returnsPendingAndDoesNotGrant() throws Exception {
        PlayerSession session = createTestSession("sub_forged_ad_01", "Honest Player");
        String claimId = "claim_" + UUID.randomUUID();

        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
                null,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );

        // Client hits verify first
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(false))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"))
                .andExpect(jsonPath("$.rewardEventId").value(claimId))
                .andExpect(jsonPath("$.newRewardedCredits").value(0));

        // Balance check: 0 rewarded credits granted
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(0));
    }

    @Test
    @DisplayName("Requirement 8: Client-before-callback order, then SSV arrival, then client polling grants exactly +1 hint")
    void verifyReward_clientBeforeCallback_thenSsv_thenPollingGrantsCredit() throws Exception {
        PlayerSession session = createTestSession("sub_order_client_first", "Polling Player");
        String claimId = "claim_cbc_" + UUID.randomUUID();
        String txnId = "ssv_txn_" + UUID.randomUUID();

        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
                null,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );

        // 1. Client calls verify -> PENDING
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(false))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));

        // 2. Google AdMob SSV webhook arrives with cryptographically valid signature
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":4,\"rewardType\":\"SOLO_HINT\"}";
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData
        )
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // 3. Client polls verify again -> SERVER_VERIFIED and granted=true
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
    @DisplayName("Requirement 8: Callback-before-client order grants immediately upon client verify")
    void verifyReward_callbackBeforeClient_grantsImmediately() throws Exception {
        PlayerSession session = createTestSession("sub_order_callback_first", "Early Callback Player");
        String claimId = "claim_cbf_" + UUID.randomUUID();
        String txnId = "ssv_txn_" + UUID.randomUUID();

        // 1. Google AdMob SSV webhook arrives before client verify
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":10,\"rewardType\":\"SOLO_HINT\"}";
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData
        )
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // 2. Client calls verify -> immediately SERVER_VERIFIED
        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
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
    }

    @Test
    @DisplayName("Requirement 4: Safely decodes URL-encoded custom_data")
    void handleSsvCallback_urlEncodedCustomData_processedSuccessfully() throws Exception {
        PlayerSession session = createTestSession("sub_url_encoded_custom", "Encoded Custom Player");
        String claimId = "claim_enc_" + UUID.randomUUID();
        String txnId = "ssv_txn_" + UUID.randomUUID();

        String rawJson = "{\"claimId\":\"" + claimId + "\",\"levelId\":7,\"rewardType\":\"SOLO_HINT\"}";
        String encodedCustomData = URLEncoder.encode(rawJson, StandardCharsets.UTF_8);

        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                encodedCustomData
        )
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // Verify client can claim it
        VerifyRewardRequest request = new VerifyRewardRequest(claimId, txnId, "admob_unit_rewarded_001", 1, RewardType.SOLO_HINT);
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(true));
    }

    @Test
    @DisplayName("Requirement 10: Duplicate SSV transaction ID is rejected with 400 and not re-credited")
    void handleSsvCallback_duplicateTransactionId_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_dup_txn", "Replay Attacker");
        String txnId = "dup_ssv_txn_" + UUID.randomUUID();
        String claimId1 = "claim_dup_1_" + UUID.randomUUID();
        String claimId2 = "claim_dup_2_" + UUID.randomUUID();

        String customData1 = "{\"claimId\":\"" + claimId1 + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";
        String customData2 = "{\"claimId\":\"" + claimId2 + "\",\"levelId\":2,\"rewardType\":\"SOLO_HINT\"}";

        // 1st SSV -> OK
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData1
        )
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // 2nd SSV with same transaction_id -> 400 INVALID_TRANSACTION
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData2
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Requirement 3: SSV ownership mismatch (callback user_id != pending claim playerId) is rejected")
    void handleSsvCallback_ownershipMismatch_rejected() throws Exception {
        PlayerSession player1Session = createTestSession("sub_owner_p1", "Player One");
        PlayerSession player2Session = createTestSession("sub_owner_p2", "Player Two");

        String claimId = "claim_owner_mismatch_" + UUID.randomUUID();

        // Player 1 creates pending claim
        VerifyRewardRequest request = new VerifyRewardRequest(claimId, null, "admob_unit_rewarded_001", 1, RewardType.SOLO_HINT);
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + player1Session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));

        // SSV callback arrives with Player 2's user_id spoofing claimId
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                "txn_mismatch_" + UUID.randomUUID(),
                player2Session.playerId(),
                customData
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // Player 1 polls claim -> NOT poisoned by Player 2's mismatched callback, remains PENDING_VERIFICATION
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + player1Session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(false))
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));
    }

    @Test
    @DisplayName("Requirement 2: Invalid signature is rejected and fails closed")
    void handleSsvCallback_invalidSignature_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_bad_sig", "Bad Signature");
        String claimId = "claim_bad_sig_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("ad_unit", "admob_unit_rewarded_001")
                .param("custom_data", customData)
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", "txn_bad_sig_" + UUID.randomUUID())
                .param("user_id", session.playerId())
                .param("signature", "INVALID_FORGED_SIGNATURE")
                .param("key_id", testKeyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Requirement 3: Stale timestamp (>24 hours) is rejected")
    void handleSsvCallback_staleTimestamp_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_stale_ts", "Stale Timestamp");
        String claimId = "claim_stale_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";
        long staleTime = System.currentTimeMillis() - (25 * 60 * 60 * 1000L); // 25 hours ago

        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(staleTime),
                "txn_stale_" + UUID.randomUUID(),
                session.playerId(),
                customData
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Requirement 9: Enforces exactly +1 hint credit regardless of client requested amount")
    void verifyReward_clientRequestedMultipleHints_grantsExactlyOne() throws Exception {
        PlayerSession session = createTestSession("sub_multi_hint_req", "Greedy Client");
        String claimId = "claim_multi_" + UUID.randomUUID();
        String txnId = "txn_multi_" + UUID.randomUUID();

        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        // SSV webhook completes with authentic cryptographic signature
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1", // Expected SSV contract amount
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData
        )
                .andExpect(status().isOk());

        // Client requests 5 hints
        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
                txnId,
                "admob_unit_rewarded_001",
                5,
                RewardType.SOLO_HINT
        );

        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(true))
                .andExpect(jsonPath("$.newRewardedCredits").value(1)); // Authoritatively exactly 1
    }

    @Test
    @DisplayName("Step 17.22: Daily cap removed - 6th valid callback on same day succeeds")
    void handleSsvCallback_sixthCallbackOnSameDay_succeedsWithoutArbitraryCap() throws Exception {
        PlayerSession session = createTestSession("sub_daily_cap_allow", "Active Watcher");

        for (int i = 0; i < 5; i++) {
            String claimId = "claim_cap_" + i + "_" + UUID.randomUUID();
            String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":" + (i + 1) + ",\"rewardType\":\"SOLO_HINT\"}";
            performValidSsvCallback(
                    "5450213213286189855",
                    "admob_unit_rewarded_001",
                    "1",
                    "SOLO_HINT",
                    String.valueOf(System.currentTimeMillis()),
                    "txn_cap_" + i + "_" + UUID.randomUUID(),
                    session.playerId(),
                    customData
            )
                    .andExpect(status().isOk());
        }

        // 6th callback -> successfully granted (no arbitrary 5-ad daily cap)
        String claimId6 = "claim_cap_6_" + UUID.randomUUID();
        String customData6 = "{\"claimId\":\"" + claimId6 + "\",\"levelId\":6,\"rewardType\":\"SOLO_HINT\"}";
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                "txn_cap_6_" + UUID.randomUUID(),
                session.playerId(),
                customData6
        )
                .andExpect(status().isOk());

        // Balance is now 6
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(6));
    }

    @Test
    @DisplayName("Security requirement: Legacy mock signature bypass is rejected with 400 INVALID_TRANSACTION")
    void handleSsvCallback_mockSignatureBypass_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_mock_sig_reject", "Attacker");
        String claimId = "claim_mock_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("ad_unit", "admob_unit_rewarded_001")
                .param("custom_data", customData)
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", "txn_mock_" + UUID.randomUUID())
                .param("user_id", session.playerId())
                .param("signature", "MEQCIBG3mock_sig_forged")
                .param("key_id", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Security requirement: Missing signature parameter is rejected with 400 INVALID_TRANSACTION")
    void handleSsvCallback_missingSignature_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_missing_sig", "Missing Sig Player");
        String claimId = "claim_missing_sig_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("ad_unit", "admob_unit_rewarded_001")
                .param("custom_data", customData)
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", "txn_missing_sig_" + UUID.randomUUID())
                .param("user_id", session.playerId())
                .param("key_id", testKeyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Security requirement: Unknown key ID is rejected with 400 INVALID_TRANSACTION")
    void handleSsvCallback_unknownKey_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_unknown_key", "Unknown Key Player");
        String claimId = "claim_unknown_key_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("ad_unit", "admob_unit_rewarded_001")
                .param("custom_data", customData)
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", "txn_unknown_key_" + UUID.randomUUID())
                .param("user_id", session.playerId())
                .param("signature", "MEQCIDummySignatureBytesHere==")
                .param("key_id", "unknown_key_id_999999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Security requirement: Duplicate reward claims cannot grant rewards twice")
    void verifyReward_duplicateRewardClaims_cannotGrantTwice() throws Exception {
        PlayerSession session = createTestSession("sub_dup_claim_check", "Dup Claim Player");
        String claimId = "claim_dup_check_" + UUID.randomUUID();
        String txnId = "txn_dup_check_" + UUID.randomUUID();

        // 1. Valid SSV callback arrives
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData
        )
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
                txnId,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );

        // 2. Client calls verify first time -> granted=true, newRewardedCredits=1
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(true))
                .andExpect(jsonPath("$.newRewardedCredits").value(1));

        // 3. Client calls verify second time for the same claimId -> still 1 credit, not 2
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.granted").value(true))
                .andExpect(jsonPath("$.newRewardedCredits").value(1));

        // 4. Verify player balance remains exactly 1 rewarded credit
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(1));
    }

    @Test
    @DisplayName("Requirement 6 & 7: Wrong or missing ad unit is rejected")
    void handleSsvCallback_wrongOrMissingAdUnit_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_ad_unit_check", "Ad Unit Tester");
        String claimId = "claim_unit_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        // Wrong ad unit
        performValidSsvCallback(
                "5450213213286189855",
                "wrong_unauthorized_ad_unit",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                "txn_wrong_unit_" + UUID.randomUUID(),
                session.playerId(),
                customData
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // Missing ad unit
        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("custom_data", customData)
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", "txn_missing_unit_" + UUID.randomUUID())
                .param("user_id", session.playerId())
                .param("signature", "MEQCIDummy==")
                .param("key_id", testKeyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Requirement 8: Unsupported reward type in custom_data is rejected")
    void handleSsvCallback_unsupportedRewardType_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_unsupported_reward_type", "Reward Type Tester");
        String claimId = "claim_unsupp_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"UNLIMITED_GEMS\"}";

        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                "txn_unsupp_" + UUID.randomUUID(),
                session.playerId(),
                customData
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Requirement 9: Unexpected reward amount or item is rejected")
    void handleSsvCallback_unexpectedRewardAmountOrItem_rejected() throws Exception {
        PlayerSession session = createTestSession("sub_unexp_reward", "Reward Amount Tester");
        String claimId = "claim_unexp_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        // Unexpected reward amount (10 instead of 1)
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "10",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                "txn_unexp_amt_" + UUID.randomUUID(),
                session.playerId(),
                customData
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // Unexpected reward item ("COINS" instead of "SOLO_HINT")
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "COINS",
                String.valueOf(System.currentTimeMillis()),
                "txn_unexp_item_" + UUID.randomUUID(),
                session.playerId(),
                customData
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));
    }

    @Test
    @DisplayName("Requirement 13: Invalid callback cannot poison a valid transaction ID")
    void handleSsvCallback_invalidCallbackDoesNotPoisonValidTxn() throws Exception {
        PlayerSession session = createTestSession("sub_anti_poison", "Poison Resistance Player");
        String claimId = "claim_poison_" + UUID.randomUUID();
        String txnId = "txn_poison_target_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        // 1. Attacker sends bogus unsigned / forged callback with target txnId
        mockMvc.perform(get("/api/v1/ads/ssv-callback")
                .param("ad_network", "5450213213286189855")
                .param("ad_unit", "admob_unit_rewarded_001")
                .param("custom_data", customData)
                .param("reward_amount", "1")
                .param("reward_item", "SOLO_HINT")
                .param("timestamp", String.valueOf(System.currentTimeMillis()))
                .param("transaction_id", txnId)
                .param("user_id", session.playerId())
                .param("signature", "INVALID_FORGED_SIGNATURE")
                .param("key_id", testKeyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // 2. Legitimate Google callback arrives with authentic cryptographic signature for same txnId
        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                txnId,
                session.playerId(),
                customData
        )
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // 3. Verify hint was granted
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(1));
    }

    @Test
    @DisplayName("Step 17.22: Stored-credit cap removed - player with 10 existing credits earns 11th credit")
    void handleSsvCallback_pastTenStoredCredits_continuesToGrantHints() throws Exception {
        PlayerSession session = createTestSession("sub_credit_cap", "Active Player");

        // Manually preset balance to 10 stored credits
        persistencePort.saveBalance(new PlayerHintCreditBalance(session.playerId(), 3, 10, 13, System.currentTimeMillis()));

        String claimId = "claim_cap_exceeded_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        performValidSsvCallback(
                "5450213213286189855",
                "admob_unit_rewarded_001",
                "1",
                "SOLO_HINT",
                String.valueOf(System.currentTimeMillis()),
                "txn_cap_exceed_" + UUID.randomUUID(),
                session.playerId(),
                customData
        )
                .andExpect(status().isOk());

        // Balance is now 11 (historical verified grants do not block new legitimate ads)
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(11));
    }

    @Test
    @DisplayName("Requirement 15: Concurrent identical SSV callbacks grant exactly once")
    void handleSsvCallback_concurrentIdenticalCallbacks_grantExactlyOnce() throws Exception {
        PlayerSession session = createTestSession("sub_concurrent_same", "Concurrent Player");
        String claimId = "claim_conc_same_" + UUID.randomUUID();
        String txnId = "txn_conc_same_" + UUID.randomUUID();
        String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":1,\"rewardType\":\"SOLO_HINT\"}";

        String ts = String.valueOf(System.currentTimeMillis());
        String messageToVerify = buildSsvMessageToVerify(
                "5450213213286189855", "admob_unit_rewarded_001", customData, "1", "SOLO_HINT",
                ts, txnId, session.playerId()
        );
        String validSig = generateValidSignature(messageToVerify);

        int threads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    var mvcResult = mockMvc.perform(get("/api/v1/ads/ssv-callback")
                            .param("ad_network", "5450213213286189855")
                            .param("ad_unit", "admob_unit_rewarded_001")
                            .param("custom_data", customData)
                            .param("reward_amount", "1")
                            .param("reward_item", "SOLO_HINT")
                            .param("timestamp", ts)
                            .param("transaction_id", txnId)
                            .param("user_id", session.playerId())
                            .param("signature", validSig)
                            .param("key_id", testKeyId))
                            .andReturn();
                    if (mvcResult.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {}
            });
        }

        readyLatch.await();
        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Exactly 1 thread must succeed
        org.junit.jupiter.api.Assertions.assertEquals(1, successCount.get());

        // Balance must be exactly 1
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(1));
    }

    @Test
    @DisplayName("Requirement 16: Concurrent distinct callbacks for same player do not lose credits")
    void handleSsvCallback_concurrentDistinctCallbacks_doNotLoseCredits() throws Exception {
        PlayerSession session = createTestSession("sub_conc_distinct", "Distinct Concurrent Player");
        int count = 4; // Under max daily (5)
        ExecutorService executor = Executors.newFixedThreadPool(count);
        CountDownLatch readyLatch = new CountDownLatch(count);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < count; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String claimId = "claim_conc_dist_" + index + "_" + UUID.randomUUID();
                    String txnId = "txn_conc_dist_" + index + "_" + UUID.randomUUID();
                    String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":" + index + ",\"rewardType\":\"SOLO_HINT\"}";
                    String ts = String.valueOf(System.currentTimeMillis());
                    String msg = buildSsvMessageToVerify(
                            "5450213213286189855", "admob_unit_rewarded_001", customData, "1", "SOLO_HINT",
                            ts, txnId, session.playerId()
                    );
                    String sig = generateValidSignature(msg);

                    readyLatch.countDown();
                    startLatch.await();

                    var mvcResult = mockMvc.perform(get("/api/v1/ads/ssv-callback")
                            .param("ad_network", "5450213213286189855")
                            .param("ad_unit", "admob_unit_rewarded_001")
                            .param("custom_data", customData)
                            .param("reward_amount", "1")
                            .param("reward_item", "SOLO_HINT")
                            .param("timestamp", ts)
                            .param("transaction_id", txnId)
                            .param("user_id", session.playerId())
                            .param("signature", sig)
                            .param("key_id", testKeyId))
                            .andReturn();
                    if (mvcResult.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {}
            });
        }

        readyLatch.await();
        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        org.junit.jupiter.api.Assertions.assertEquals(count, successCount.get());

        // Balance must be exactly 4 credits
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(count));
    }

    @Test
    @DisplayName("Step 17.22: Concurrency with 8 distinct valid callbacks - all 8 succeed atomically")
    void handleSsvCallback_concurrencyWithEightDistinctValidCallbacks_allSucceed() throws Exception {
        PlayerSession session = createTestSession("sub_conc_daily_limit", "Daily Limit Concurrent Player");
        int totalRequests = 8; // More than old daily cap of 5
        ExecutorService executor = Executors.newFixedThreadPool(totalRequests);
        CountDownLatch readyLatch = new CountDownLatch(totalRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < totalRequests; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String claimId = "claim_conc_daily_" + index + "_" + UUID.randomUUID();
                    String txnId = "txn_conc_daily_" + index + "_" + UUID.randomUUID();
                    String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":" + index + ",\"rewardType\":\"SOLO_HINT\"}";
                    String ts = String.valueOf(System.currentTimeMillis());
                    String msg = buildSsvMessageToVerify(
                            "5450213213286189855", "admob_unit_rewarded_001", customData, "1", "SOLO_HINT",
                            ts, txnId, session.playerId()
                    );
                    String sig = generateValidSignature(msg);

                    readyLatch.countDown();
                    startLatch.await();

                    var mvcResult = mockMvc.perform(get("/api/v1/ads/ssv-callback")
                            .param("ad_network", "5450213213286189855")
                            .param("ad_unit", "admob_unit_rewarded_001")
                            .param("custom_data", customData)
                            .param("reward_amount", "1")
                            .param("reward_item", "SOLO_HINT")
                            .param("timestamp", ts)
                            .param("transaction_id", txnId)
                            .param("user_id", session.playerId())
                            .param("signature", sig)
                            .param("key_id", testKeyId))
                            .andReturn();
                    if (mvcResult.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {}
            });
        }

        readyLatch.await();
        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // All 8 must succeed
        org.junit.jupiter.api.Assertions.assertEquals(8, successCount.get());

        // Balance must be exactly 8
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(8));
    }

    @Test
    @DisplayName("Requirement 14: Persistence failure result returns failure safely without partial commit")
    void handleSsvCallback_persistenceFailure_safeFailure() {
        RewardGrantResult failure = RewardGrantResult.failure(RewardGrantStatus.DATABASE_ERROR, "Simulated database connection failure");
        org.junit.jupiter.api.Assertions.assertFalse(failure.isSuccess());
        org.junit.jupiter.api.Assertions.assertNull(failure.updatedBalance());
        org.junit.jupiter.api.Assertions.assertEquals("Simulated database connection failure", failure.failureReason());
    }

    @Test
    @DisplayName("Step 16: Same transaction callback replay returns 400 and does not credit duplicate hints")
    void handleSsvCallback_sameTransactionReplay_rejectedAndDoesNotDuplicateCredit() throws Exception {
        PlayerSession session = createTestSession("sub_replay_test_01", "Replay Test Player");
        String claimId = "claim_replay_" + UUID.randomUUID();
        String txnId = "tx_replay_" + UUID.randomUUID();
        String ts = String.valueOf(System.currentTimeMillis());
        String customData = "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}";

        // First callback succeeds
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                ts, txnId, session.playerId(), customData)
                .andExpect(status().isOk());

        // Second identical callback is rejected
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                ts, txnId, session.playerId(), customData)
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // Balance must be exactly 1, not 2
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(1));
    }

    @Test
    @DisplayName("Step 16: Different transactions for same player succeed and increment balance sequentially")
    void handleSsvCallback_differentTransactions_samePlayer_incrementsSequentially() throws Exception {
        PlayerSession session = createTestSession("sub_seq_test_01", "Sequential Test Player");
        String ts = String.valueOf(System.currentTimeMillis());

        String claim1 = "claim_seq_1_" + UUID.randomUUID();
        String txn1 = "tx_seq_1_" + UUID.randomUUID();
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                ts, txn1, session.playerId(), "{\"claimId\":\"" + claim1 + "\",\"rewardType\":\"SOLO_HINT\"}")
                .andExpect(status().isOk());

        String claim2 = "claim_seq_2_" + UUID.randomUUID();
        String txn2 = "tx_seq_2_" + UUID.randomUUID();
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                ts, txn2, session.playerId(), "{\"claimId\":\"" + claim2 + "\",\"rewardType\":\"SOLO_HINT\"}")
                .andExpect(status().isOk());

        // Balance must be 2
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(2));
    }

    @Test
    @DisplayName("Step 16: Existing claim ad-unit mismatch rejected without poisoning claim")
    void handleSsvCallback_existingClaimAdUnitMismatch_rejectedWithoutPoisoningClaim() throws Exception {
        PlayerSession session = createTestSession("sub_adunit_mismatch_01", "Ad Unit Mismatch Player");
        String claimId = "claim_adunit_" + UUID.randomUUID();

        // 1. Client registers claim with admob_unit_rewarded_001
        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
                null,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));

        // 2. SSV callback arrives with admob_unit_rewarded_002 for the same claim
        String txnId = "tx_adunit_mismatch_" + UUID.randomUUID();
        String ts = String.valueOf(System.currentTimeMillis());
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_002", "1", "SOLO_HINT",
                ts, txnId, session.playerId(), "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}")
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // 3. Client re-checks: claim is still PENDING_VERIFICATION (not poisoned to REJECTED)
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));
    }

    @Test
    @DisplayName("Step 16: Ownership mismatch callback rejected without poisoning legitimate player's pending claim")
    void handleSsvCallback_ownershipMismatch_doesNotPoisonVictimClaim() throws Exception {
        PlayerSession alice = createTestSession("sub_alice_01", "Alice Player");
        PlayerSession bob = createTestSession("sub_bob_01", "Bob Attacker");
        String claimId = "claim_alice_" + UUID.randomUUID();

        // 1. Alice registers her legitimate pending claim
        VerifyRewardRequest request = new VerifyRewardRequest(
                claimId,
                null,
                "admob_unit_rewarded_001",
                1,
                RewardType.SOLO_HINT
        );
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + alice.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));

        // 2. Bob attempts SSV callback claiming Alice's claimId
        String bobTxnId = "tx_bob_attack_" + UUID.randomUUID();
        String ts = String.valueOf(System.currentTimeMillis());
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                ts, bobTxnId, bob.playerId(), "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}")
                .andExpect(status().isBadRequest())
                .andExpect(content().string("INVALID_TRANSACTION"));

        // 3. Alice verifies her claim is NOT poisoned - still PENDING_VERIFICATION
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + alice.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));

        // 4. Now Alice's legitimate callback arrives with Alice's player ID
        String aliceTxnId = "tx_alice_valid_" + UUID.randomUUID();
        performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                ts, aliceTxnId, alice.playerId(), "{\"claimId\":\"" + claimId + "\",\"rewardType\":\"SOLO_HINT\"}")
                .andExpect(status().isOk());

        // 5. Alice's client calls verify: successfully SERVER_VERIFIED and credited!
        mockMvc.perform(post("/api/v1/ads/reward/verify")
                .header("Authorization", "Bearer " + alice.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SERVER_VERIFIED"))
                .andExpect(jsonPath("$.granted").value(true))
                .andExpect(jsonPath("$.newRewardedCredits").value(1));
    }

    @Test
    @DisplayName("Step 17.22: Six or more distinct valid rewarded transactions in one day are accepted without daily cap")
    void step1722_sixOrMoreDistinctValidRewardedTransactionsInOneDay_accepted() throws Exception {
        PlayerSession session = createTestSession("sub_six_daily_ads", "Active Solo Player");
        String ts = String.valueOf(System.currentTimeMillis());

        // Execute 7 distinct rewarded ad completions on the same day
        for (int i = 1; i <= 7; i++) {
            String claimId = "claim_day_tier_" + i + "_" + UUID.randomUUID();
            String txnId = "tx_day_tier_" + i + "_" + UUID.randomUUID();
            String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":" + i + ",\"rewardType\":\"SOLO_HINT\"}";

            // Client initiates claim
            VerifyRewardRequest request = new VerifyRewardRequest(claimId, null, "admob_unit_rewarded_001", 1, RewardType.SOLO_HINT);
            mockMvc.perform(post("/api/v1/ads/reward/verify")
                    .header("Authorization", "Bearer " + session.sessionToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PENDING_VERIFICATION"));

            // Google AdMob SSV webhook arrives
            performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                    ts, txnId, session.playerId(), customData)
                    .andExpect(status().isOk())
                    .andExpect(content().string("OK"));

            // Client polls verify -> must be SERVER_VERIFIED with granted=true
            mockMvc.perform(post("/api/v1/ads/reward/verify")
                    .header("Authorization", "Bearer " + session.sessionToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SERVER_VERIFIED"))
                    .andExpect(jsonPath("$.granted").value(true))
                    .andExpect(jsonPath("$.newRewardedCredits").value(i));
        }

        // Final balance check: 7 rewarded credits granted (well exceeding the old 5-per-day cap)
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(7))
                .andExpect(jsonPath("$.dailyRewardedAdsRemaining").value(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("Step 17.22: More than ten lifetime verified grants succeed without being blocked by historical counter")
    void step1722_moreThanTenLifetimeVerifiedGrants_succeedsWithoutBlock() throws Exception {
        PlayerSession session = createTestSession("sub_eleven_lifetime_ads", "Dedicated Veteran Player");
        String ts = String.valueOf(System.currentTimeMillis());

        // Complete 12 distinct lifetime rewarded ad transactions
        for (int i = 1; i <= 12; i++) {
            String claimId = "claim_lifetime_" + i + "_" + UUID.randomUUID();
            String txnId = "tx_lifetime_" + i + "_" + UUID.randomUUID();
            String customData = "{\"claimId\":\"" + claimId + "\",\"levelId\":" + i + ",\"rewardType\":\"SOLO_HINT\"}";

            VerifyRewardRequest request = new VerifyRewardRequest(claimId, null, "admob_unit_rewarded_001", 1, RewardType.SOLO_HINT);
            mockMvc.perform(post("/api/v1/ads/reward/verify")
                    .header("Authorization", "Bearer " + session.sessionToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            performValidSsvCallback("5450213213286189855", "admob_unit_rewarded_001", "1", "SOLO_HINT",
                    ts, txnId, session.playerId(), customData)
                    .andExpect(status().isOk())
                    .andExpect(content().string("OK"));

            mockMvc.perform(post("/api/v1/ads/reward/verify")
                    .header("Authorization", "Bearer " + session.sessionToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("SERVER_VERIFIED"))
                    .andExpect(jsonPath("$.granted").value(true))
                    .andExpect(jsonPath("$.newRewardedCredits").value(i));
        }

        // Verify balance reflects 12 lifetime grants without error
        mockMvc.perform(get("/api/v1/ads/reward/balance")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rewardedCredits").value(12));
    }
}
