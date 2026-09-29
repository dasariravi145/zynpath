package com.zynpath.backend.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.account.model.PlayerPrivacySettings;
import com.zynpath.backend.account.model.ProfileVisibility;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    private PlayerSession createTestPlayerSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("Get account settings returns aggregated settings and privacy preferences")
    void getSettings_authenticatedPlayer_returnsAccountSettings() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_settings_01", "Settings Tester");

        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(session.playerId()))
                .andExpect(jsonPath("$.displayName").value("Settings Tester"))
                .andExpect(jsonPath("$.publicZynpathId").isNotEmpty())
                .andExpect(jsonPath("$.privacySettings").isNotEmpty());
    }

    @Test
    @DisplayName("Update privacy settings successfully saves preferences")
    void updatePrivacy_validPayload_updatesSettings() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_privacy_01", "Privacy Tester");

        PlayerPrivacySettings newSettings = new PlayerPrivacySettings(
                ProfileVisibility.FRIENDS_ONLY,
                false,
                false
        );

        mockMvc.perform(put("/api/v1/account/privacy")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSettings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profileVisibility").value("FRIENDS_ONLY"))
                .andExpect(jsonPath("$.allowZynpathIdSearch").value(false))
                .andExpect(jsonPath("$.allowFriendRequests").value(false));
    }

    @Test
    @DisplayName("List linked providers returns providers for the authenticated account")
    void getLinkedProviders_returnsProvidersList() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_providers_01", "Providers Tester");

        mockMvc.perform(get("/api/v1/account/providers")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("GOOGLE"));
    }

    @Test
    @DisplayName("Cannot unlink the sole remaining authentication provider")
    void unlinkProvider_soleProvider_returnsBadRequest() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_sole_prov_01", "Sole Prov Tester");

        mockMvc.perform(delete("/api/v1/account/providers/GOOGLE")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("LAST_SIGN_IN_METHOD"));
    }

    @Test
    @DisplayName("Data export endpoint returns comprehensive schema-versioned export")
    void exportData_authenticatedPlayer_returnsExportDto() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_export_01", "Export Tester");

        mockMvc.perform(post("/api/v1/account/export")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schemaVersion").value(1))
                .andExpect(jsonPath("$.account.playerId").value(session.playerId()))
                .andExpect(jsonPath("$.account.displayName").value("Export Tester"))
                .andExpect(jsonPath("$.exportedAt").isNumber());
    }

    @Test
    @DisplayName("Authoritative account deletion removes account, sessions, and data")
    void deleteAccount_authenticatedPlayer_purgesDataAndRevokesSession() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_del_01", "Delete Tester");

        mockMvc.perform(post("/api/v1/account/delete")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELETED"))
                .andExpect(jsonPath("$.playerId").value(session.playerId()));

        // Subsequent calls with that session token must be unauthorized
        mockMvc.perform(get("/api/v1/account/settings")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Account deletion prevents stale synchronization from accessing deleted account")
    void deleteAccount_staleAccess_fails() throws Exception {
        PlayerSession session = createTestPlayerSession("sub_del_stale_01", "Stale Tester");

        // Delete account
        mockMvc.perform(delete("/api/v1/account/delete")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk());

        // Attempt export
        mockMvc.perform(post("/api/v1/account/export")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isUnauthorized());
    }
}
