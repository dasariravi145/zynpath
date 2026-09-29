package com.zynpath.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthLinkRequest;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.AuthTokenExchangeRequest;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.auth.service.TokenVerificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "zynpath.auth.google.client-id=test-zynpath-client-id.apps.googleusercontent.com"
})
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @org.springframework.test.context.bean.override.mockito.MockitoSpyBean
    private TokenVerificationService tokenVerificationService;

    private String createMockGoogleJwt(String sub, String name, String email, String aud, long expEpochSeconds) {
        long iatEpochSeconds = expEpochSeconds - 3600;
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payloadJson = String.format(
                "{\"iss\":\"https://accounts.google.com\",\"aud\":\"%s\",\"sub\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"email_verified\":true,\"exp\":%d,\"iat\":%d}",
                aud, sub, name, email, expEpochSeconds, iatEpochSeconds
        );
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString("dummy_signature_bytes".getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + "." + signature;
    }

    @Test
    @DisplayName("Google exchange succeeds with valid Google ID token JWT")
    void exchangeToken_validGoogleToken_createsAccountAndSession() throws Exception {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String googleJwt = createMockGoogleJwt("google_sub_001", "Alice Runner", "alice@example.com", "test-zynpath-client-id.apps.googleusercontent.com", futureExp);

        org.mockito.Mockito.doReturn(new VerifiedProviderIdentity(AuthProvider.GOOGLE, "google_sub_001", "Alice Runner", "alice@example.com", true))
                .when(tokenVerificationService).verifyProviderToken(org.mockito.ArgumentMatchers.eq(AuthProvider.GOOGLE), org.mockito.ArgumentMatchers.eq(googleJwt));

        AuthTokenExchangeRequest request = new AuthTokenExchangeRequest(AuthProvider.GOOGLE, googleJwt, "guest-uuid-101");

        mockMvc.perform(post("/api/v1/auth/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").isNotEmpty())
                .andExpect(jsonPath("$.playerId").isNotEmpty())
                .andExpect(jsonPath("$.publicZynpathId").isNotEmpty())
                .andExpect(jsonPath("$.displayName").value("Alice Runner"))
                .andExpect(jsonPath("$.accountType").value("AUTHENTICATED"))
                .andExpect(jsonPath("$.expiresAt").isNumber());
    }

    @Test
    @DisplayName("Google exchange fails when token is expired")
    void exchangeToken_expiredToken_returnsUnauthorized() throws Exception {
        long pastExp = (System.currentTimeMillis() / 1000L) - 3600;
        String expiredJwt = createMockGoogleJwt("google_sub_exp", "Expired User", "expired@example.com", "test-zynpath-client-id.apps.googleusercontent.com", pastExp);

        AuthTokenExchangeRequest request = new AuthTokenExchangeRequest(AuthProvider.GOOGLE, expiredJwt, null);

        mockMvc.perform(post("/api/v1/auth/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("EXPIRED_CREDENTIAL"));
    }

    @Test
    @DisplayName("Google exchange fails when audience does not match server configuration")
    void exchangeToken_wrongAudience_returnsUnauthorized() throws Exception {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String wrongAudJwt = createMockGoogleJwt("google_sub_wrong_aud", "Hacker", "bad@example.com", "wrong-aud.apps.googleusercontent.com", futureExp);

        AuthTokenExchangeRequest request = new AuthTokenExchangeRequest(AuthProvider.GOOGLE, wrongAudJwt, null);

        mockMvc.perform(post("/api/v1/auth/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_PROVIDER_CREDENTIAL"));
    }

    @Test
    @DisplayName("Google exchange fails when token format is malformed")
    void exchangeToken_malformedToken_returnsUnauthorized() throws Exception {
        AuthTokenExchangeRequest request = new AuthTokenExchangeRequest(AuthProvider.GOOGLE, "not-a-valid-jwt", null);

        mockMvc.perform(post("/api/v1/auth/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_PROVIDER_CREDENTIAL"));
    }

    @Test
    @DisplayName("Guest account linking links guest identity and issues authenticated session")
    void linkAccount_validGuestLinking_preservesProgressAndIssuesSession() throws Exception {
        String guestUuid = "guest-uuid-link-202";
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String googleJwt = createMockGoogleJwt("google_sub_link_01", "Linked Guest", "linked@example.com", "test-zynpath-client-id.apps.googleusercontent.com", futureExp);

        org.mockito.Mockito.doReturn(new VerifiedProviderIdentity(AuthProvider.GOOGLE, "google_sub_link_01", "Linked Guest", "linked@example.com", true))
                .when(tokenVerificationService).verifyProviderToken(org.mockito.ArgumentMatchers.eq(AuthProvider.GOOGLE), org.mockito.ArgumentMatchers.eq(googleJwt));

        AuthLinkRequest linkRequest = new AuthLinkRequest(AuthProvider.GOOGLE, googleJwt, guestUuid, "Custom Linked Name");

        mockMvc.perform(post("/api/v1/auth/link")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(linkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").isNotEmpty())
                .andExpect(jsonPath("$.displayName").value("Custom Linked Name"))
                .andExpect(jsonPath("$.accountType").value("LINKED"));
    }

    @Test
    @DisplayName("Guest linking fails with ACCOUNT_LINK_CONFLICT when provider identity is already claimed")
    void linkAccount_alreadyLinkedProvider_returnsConflict() throws Exception {
        // Pre-link an identity
        VerifiedProviderIdentity existing = new VerifiedProviderIdentity(AuthProvider.GOOGLE, "google_sub_conflict_01", "Existing User", "existing@example.com", true);
        playerAccountService.getOrCreateForExternalIdentity(existing, null);

        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String googleJwt = createMockGoogleJwt("google_sub_conflict_01", "Duplicate Claimer", "existing@example.com", "test-zynpath-client-id.apps.googleusercontent.com", futureExp);

        org.mockito.Mockito.doReturn(new VerifiedProviderIdentity(AuthProvider.GOOGLE, "google_sub_conflict_01", "Duplicate Claimer", "existing@example.com", true))
                .when(tokenVerificationService).verifyProviderToken(org.mockito.ArgumentMatchers.eq(AuthProvider.GOOGLE), org.mockito.ArgumentMatchers.eq(googleJwt));

        AuthLinkRequest conflictRequest = new AuthLinkRequest(AuthProvider.GOOGLE, googleJwt, "new-guest-uuid-999", "Another Name");

        mockMvc.perform(post("/api/v1/auth/link")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(conflictRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_LINK_CONFLICT"));
    }

    @Test
    @DisplayName("/me returns current authenticated player profile")
    void getCurrentPlayer_authenticatedSession_returnsPlayerProfile() throws Exception {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, "sub_me_test_01", "Bob Smith", "bob@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        PlayerSession session = sessionSecurityService.createSession(account.playerId());

        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value(account.playerId()))
                .andExpect(jsonPath("$.displayName").value(account.displayName()))
                .andExpect(jsonPath("$.publicZynpathId").value(account.publicZynpathId()));
    }

    @Test
    @DisplayName("/me rejects unauthenticated request with 401")
    void getCurrentPlayer_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Session refresh rotates token and revokes previous session token")
    void refreshSession_validToken_rotatesAndInvalidatesOldToken() throws Exception {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, "sub_refresh_01", "Refresher", "refresher@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        PlayerSession oldSession = sessionSecurityService.createSession(account.playerId());

        String responseContent = mockMvc.perform(post("/api/v1/auth/refresh")
                .header("Authorization", "Bearer " + oldSession.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> responseMap = objectMapper.readValue(responseContent, Map.class);
        String newToken = (String) responseMap.get("sessionToken");

        assertThat(newToken).isNotEqualTo(oldSession.sessionToken());

        // Previous token must now be invalid
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + oldSession.sessionToken()))
                .andExpect(status().isUnauthorized());

        // New token must work
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Sign out revokes active session")
    void signOut_validToken_revokesSession() throws Exception {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, "sub_signout_01", "Signout User", "signout@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        PlayerSession session = sessionSecurityService.createSession(account.playerId());

        mockMvc.perform(post("/api/v1/auth/signout")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SIGNED_OUT"));

        // Subsequent call must fail
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + session.sessionToken()))
                .andExpect(status().isUnauthorized());
    }
}
