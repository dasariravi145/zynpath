package com.zynpath.backend.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.security.integrity.CompetitiveIntegrityGuard;
import com.zynpath.backend.subscription.model.EntitlementStatus;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionRestoreRequest;
import com.zynpath.backend.subscription.model.SubscriptionDto.SubscriptionVerificationRequest;
import com.zynpath.backend.subscription.model.SubscriptionEntitlement;
import com.zynpath.backend.subscription.model.SubscriptionTier;
import com.zynpath.backend.subscription.service.SubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SubscriptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private CompetitiveIntegrityGuard competitiveIntegrityGuard;

    private PlayerSession createTestSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("Default entitlement is FREE with no premium features")
    void getEntitlement_newAccount_returnsFree() throws Exception {
        PlayerSession session = createTestSession("sub_sub_free_01", "Free Tier Player");

        mockMvc.perform(get("/api/v1/subscription/entitlement")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier").value("FREE"))
                .andExpect(jsonPath("$.isPremiumActive").value(false))
                .andExpect(jsonPath("$.unlockedFeatureKeys").isEmpty());
    }

    @Test
    @DisplayName("Verification fails safely with BLOCKED_BY_CONFIGURATION when external Google Play key is unconfigured")
    void verifyPurchase_unconfiguredServiceAccount_failsSafely() throws Exception {
        PlayerSession session = createTestSession("sub_sub_verify_01", "Billing Verifier");

        SubscriptionVerificationRequest request = new SubscriptionVerificationRequest(
                "token_sample_123456789",
                "zynpath_premium",
                "premium-monthly",
                "order_play_001",
                "com.zynpath.game"
        );

        mockMvc.perform(post("/api/v1/subscription/verify")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.status").value("BLOCKED_BY_CONFIGURATION"));
    }

    @Test
    @DisplayName("Restore purchases returns free when no prior purchases exist")
    void restorePurchases_noPriorPurchases_returnsNotRestored() throws Exception {
        PlayerSession session = createTestSession("sub_sub_restore_01", "Restorer");

        SubscriptionRestoreRequest request = new SubscriptionRestoreRequest(
                List.of("token_sample_123456789"),
                "com.zynpath.game"
        );

        mockMvc.perform(post("/api/v1/subscription/restore")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restored").value(false));
    }

    @Test
    @DisplayName("Entitlement lifecycle transitions: Active, Expired, and Feature Grants")
    void entitlementLifecycle_activeAndExpired() {
        String testPlayerId = "player_entitlement_lifecycle_01";

        // 1. Manually grant active Premium entitlement
        long now = System.currentTimeMillis();
        long futureExp = now + 86_400_000L; // +1 day
        SubscriptionEntitlement activeEntitlement = new SubscriptionEntitlement(
                testPlayerId,
                SubscriptionTier.PREMIUM_MONTHLY,
                EntitlementStatus.ACTIVE,
                "zynpath_premium",
                "premium-monthly",
                futureExp,
                now,
                "token_hash_lifecycle_01",
                "GOOGLE_PLAY",
                true
        );

        var dto = subscriptionService.toDto(activeEntitlement);
        assertThat(dto.isPremiumActive()).isTrue();
        assertThat(dto.unlockedFeatureKeys()).contains("AD_FREE", "UNLIMITED_SOLO_HINTS");

        // 2. Expired entitlement
        long pastExp = now - 1000L;
        SubscriptionEntitlement expiredEntitlement = new SubscriptionEntitlement(
                testPlayerId,
                SubscriptionTier.PREMIUM_MONTHLY,
                EntitlementStatus.EXPIRED,
                "zynpath_premium",
                "premium-monthly",
                pastExp,
                now,
                "token_hash_lifecycle_01",
                "GOOGLE_PLAY",
                false
        );

        var expiredDto = subscriptionService.toDto(expiredEntitlement);
        assertThat(expiredDto.isPremiumActive()).isFalse();
        assertThat(expiredDto.unlockedFeatureKeys()).isEmpty();
    }

    @Test
    @DisplayName("Premium fairness: Competitive hints remain strictly prohibited in multiplayer even for Premium players")
    void premiumFairness_competitiveHintsProhibited() {
        // Assert that even if player is Premium, competitive hint usage is rejected by CompetitiveIntegrityGuard
        assertThatThrownBy(() -> competitiveIntegrityGuard.assertNoHintsAllowed(GameMode.QUICK_DUEL, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("COMPETITIVE_FAIRNESS_VIOLATION");

        assertThatThrownBy(() -> competitiveIntegrityGuard.assertNoHintsAllowed(GameMode.MINI_LEAGUE, 2))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("COMPETITIVE_FAIRNESS_VIOLATION");
    }
}
