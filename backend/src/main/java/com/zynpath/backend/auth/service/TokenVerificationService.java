package com.zynpath.backend.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Validates external identity provider credentials against provider-specific rules.
 *
 * Implements Prompt 18 Sections 8, 9, 10, 11 & 37:
 * - Validates issuer, audience, expiration, subject ID.
 * - Cryptographically verifies Google ID token signatures using Google's trusted public keys.
 * - Rejects unverified tokens, arbitrary claims, and spoofed emails.
 * - Detects missing provider configuration and returns structured PROVIDER_UNAVAILABLE.
 * - Never fabricates fake provider successes.
 */
@Service
public class TokenVerificationService {

    private static final Logger log = LoggerFactory.getLogger(TokenVerificationService.class);

    private final String googleClientId;
    private final String facebookAppId;
    private final String facebookAppSecret;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Autowired
    public TokenVerificationService(
            @Value("${zynpath.auth.google.client-id:}") String googleClientId,
            @Value("${zynpath.auth.facebook.app-id:}") String facebookAppId,
            @Value("${zynpath.auth.facebook.app-secret:}") String facebookAppSecret,
            ObjectMapper objectMapper
    ) {
        this(googleClientId, facebookAppId, facebookAppSecret, objectMapper, null);
    }

    public TokenVerificationService(
            String googleClientId,
            String facebookAppId,
            String facebookAppSecret,
            ObjectMapper objectMapper,
            GoogleIdTokenVerifier customGoogleIdTokenVerifier
    ) {
        this.googleClientId = googleClientId != null ? googleClientId.trim() : "";
        this.facebookAppId = facebookAppId != null ? facebookAppId.trim() : "";
        this.facebookAppSecret = facebookAppSecret != null ? facebookAppSecret.trim() : "";
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        if (customGoogleIdTokenVerifier != null) {
            this.googleIdTokenVerifier = customGoogleIdTokenVerifier;
        } else if (!this.googleClientId.isEmpty()) {
            this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(this.googleClientId))
            .build();
        } else {
            this.googleIdTokenVerifier = null;
        }
    }

    /**
     * Verifies the provider credential token and extracts the verified subject identity.
     */
    public VerifiedProviderIdentity verifyProviderToken(AuthProvider provider, String providerToken) {
        if (providerToken == null || providerToken.isBlank()) {
            throw AuthException.invalidCredential("Provider credential token is missing or blank");
        }

        return switch (provider) {
            case GOOGLE -> verifyGoogleIdToken(providerToken);
            case FACEBOOK -> verifyFacebookToken(providerToken);
        };
    }

    /**
     * Validates a Google ID token JWT.
     * Enforces issuer, audience (GOOGLE_CLIENT_ID), expiration, and nonblank subject claims.
     * Cryptographically verifies Google's signature using Google's trusted public keys.
     */
    private VerifiedProviderIdentity verifyGoogleIdToken(String idToken) {
        if (googleClientId.isEmpty() || googleIdTokenVerifier == null) {
            log.warn("Google Sign-In attempted but GOOGLE_CLIENT_ID is not configured");
            throw AuthException.providerUnavailable("Google Sign-In is not configured on this server");
        }

        String[] parts = idToken.split("\\.", -1);
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
            throw AuthException.invalidCredential("Malformed Google ID token: expected 3 non-empty JWT segments");
        }

        GoogleIdToken googleIdToken;
        try {
            googleIdToken = GoogleIdToken.parse(GsonFactory.getDefaultInstance(), idToken);
        } catch (Exception e) {
            log.warn("Failed to parse Google ID token: {}", e.getMessage());
            throw AuthException.invalidCredential("Unable to parse Google credential: " + e.getMessage());
        }

        if (googleIdToken == null || googleIdToken.getHeader() == null || googleIdToken.getPayload() == null) {
            throw AuthException.invalidCredential("Malformed Google ID token: missing header or payload");
        }

        // 1. Validate header algorithm (must be RS256 for Google ID tokens; reject 'none' or symmetric algs)
        String algorithm = googleIdToken.getHeader().getAlgorithm();
        if (algorithm == null || !"RS256".equalsIgnoreCase(algorithm)) {
            log.warn("Rejected Google ID token with unsupported algorithm: {}", algorithm);
            throw AuthException.invalidCredential("Google ID token algorithm is unsupported or unsigned: " + algorithm);
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();

        // 2. Validate expiration and issued-at claims
        Long expSeconds = payload.getExpirationTimeSeconds();
        if (expSeconds == null) {
            throw AuthException.invalidCredential("Google ID token missing 'exp' claim");
        }
        Long iatSeconds = payload.getIssuedAtTimeSeconds();
        if (iatSeconds == null) {
            throw AuthException.invalidCredential("Google ID token missing 'iat' claim");
        }
        long nowSeconds = System.currentTimeMillis() / 1000L;
        if (nowSeconds > expSeconds) {
            throw AuthException.expiredCredential("Google ID token has expired");
        }

        // 3. Validate issuer claim
        String iss = payload.getIssuer();
        if (iss == null || iss.isBlank()) {
            throw AuthException.invalidCredential("Google ID token missing 'iss' claim");
        }
        if (!"https://accounts.google.com".equals(iss) && !"accounts.google.com".equals(iss)) {
            throw AuthException.invalidCredential("Invalid Google ID token issuer: " + iss);
        }

        // 4. Validate audience matches configured Google Client ID
        List<String> audiences = payload.getAudienceAsList();
        if (audiences == null || audiences.isEmpty()) {
            throw AuthException.invalidCredential("Google ID token missing 'aud' claim");
        }
        if (!audiences.contains(googleClientId)) {
            log.warn("Google ID token audience mismatch: expected {}, got {}", googleClientId, audiences);
            throw AuthException.invalidCredential("Google ID token audience does not match server configuration");
        }

        // 5. Validate subject claim is non-blank
        String sub = payload.getSubject();
        if (sub == null || sub.isBlank()) {
            throw AuthException.invalidCredential("Google ID token missing 'sub' claim");
        }

        // 6. Cryptographically verify signature using Google's trusted public keys
        boolean signatureVerified;
        try {
            signatureVerified = googleIdTokenVerifier.verify(googleIdToken);
        } catch (IOException e) {
            log.error("Failed to reach Google token verification service: {}", e.getMessage());
            throw AuthException.providerUnavailable("Failed to reach Google token verification service: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            log.warn("Google ID token cryptographic security exception: {}", e.getMessage());
            throw AuthException.invalidCredential("Google ID token cryptographic verification failed: " + e.getMessage());
        } catch (Exception e) {
            log.warn("Google ID token verification failed: {}", e.getMessage());
            throw AuthException.invalidCredential("Google ID token verification failed: " + e.getMessage());
        }

        if (!signatureVerified) {
            log.warn("Google ID token cryptographic signature verification failed");
            throw AuthException.invalidCredential("Google ID token signature verification failed");
        }

        // 7. Extract name, email, and verified-email status only from a successfully verified token
        String name = (String) payload.get("name");
        if (name == null || name.isBlank()) {
            name = "Google Player";
        }
        String email = payload.getEmail();
        Boolean emailVerified = payload.getEmailVerified();
        boolean isEmailVerified = Boolean.TRUE.equals(emailVerified);

        return new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, email, isEmailVerified);
    }

    /**
     * Validates a Facebook User Access Token via Facebook Graph API.
     */
    private VerifiedProviderIdentity verifyFacebookToken(String accessToken) {
        if (facebookAppId.isEmpty()) {
            log.warn("Facebook Sign-In attempted but FACEBOOK_APP_ID is not configured");
            throw AuthException.providerUnavailable("Facebook Login is not configured on this server");
        }

        try {
            // Verify with Graph API me endpoint
            URI uri = URI.create("https://graph.facebook.com/v19.0/me?fields=id,name,email&access_token=" + accessToken);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Facebook Graph API returned error HTTP {}: {}", response.statusCode(), response.body());
                if (response.statusCode() == 401 || response.statusCode() == 400) {
                    throw AuthException.invalidCredential("Facebook access token is invalid or expired");
                }
                throw AuthException.providerUnavailable("Facebook Graph API verification failed (HTTP " + response.statusCode() + ")");
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root.has("error")) {
                String errorMsg = root.get("error").path("message").asText("Facebook token error");
                throw AuthException.invalidCredential(errorMsg);
            }

            if (!root.hasNonNull("id")) {
                throw AuthException.invalidCredential("Facebook user ID missing from graph response");
            }

            String fbId = root.get("id").asText();
            String name = root.hasNonNull("name") ? root.get("name").asText() : "Facebook Player";
            String email = root.hasNonNull("email") ? root.get("email").asText() : null;

            return new VerifiedProviderIdentity(AuthProvider.FACEBOOK, fbId, name, email, email != null);

        } catch (AuthException ae) {
            throw ae;
        } catch (Exception e) {
            log.error("Facebook token verification exception: {}", e.getMessage());
            throw AuthException.providerUnavailable("Failed to reach Facebook verification service: " + e.getMessage());
        }
    }
}
