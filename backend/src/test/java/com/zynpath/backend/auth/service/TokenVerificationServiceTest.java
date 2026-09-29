package com.zynpath.backend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Focused tests for TokenVerificationService verifying:
 * - Missing Google OAuth client ID configuration
 * - Rejection of invalid, expired, malformed, unsigned, and forged Google ID tokens
 * - Adherence to security requirements without fabricated successful Google credentials
 */
class TokenVerificationServiceTest {

    private static final String CONFIGURED_CLIENT_ID = "zynpath-prod-client-id.apps.googleusercontent.com";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TokenVerificationService service;

    @BeforeEach
    void setUp() {
        service = new TokenVerificationService(
                CONFIGURED_CLIENT_ID,
                "fb-app-id",
                "fb-app-secret",
                objectMapper
        );
    }

    private String encodeBase64Url(String input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private String createJwt(String headerJson, String payloadJson, String signature) {
        return encodeBase64Url(headerJson) + "." + encodeBase64Url(payloadJson) + "." + signature;
    }

    private String createValidClaimsPayloadJson(String sub, String aud, long expEpochSeconds) {
        long iatEpochSeconds = expEpochSeconds - 3600;
        return String.format(
                "{\"iss\":\"https://accounts.google.com\",\"aud\":\"%s\",\"sub\":\"%s\",\"name\":\"Test User\",\"email\":\"user@example.com\",\"email_verified\":true,\"exp\":%d,\"iat\":%d}",
                aud, sub, expEpochSeconds, iatEpochSeconds
        );
    }

    // =========================================================================
    // 1. Missing Configuration
    // =========================================================================

    @Test
    @DisplayName("Missing GOOGLE_CLIENT_ID rejects verification with PROVIDER_UNAVAILABLE (503)")
    void verifyProviderToken_missingGoogleClientId_throwsProviderUnavailable() {
        TokenVerificationService unconfiguredService = new TokenVerificationService(
                "",
                "fb-app-id",
                "fb-app-secret",
                objectMapper
        );

        assertThatThrownBy(() -> unconfiguredService.verifyProviderToken(AuthProvider.GOOGLE, "any.valid.jwt"))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("PROVIDER_UNAVAILABLE");
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                });
    }

    @Test
    @DisplayName("Blank GOOGLE_CLIENT_ID rejects verification with PROVIDER_UNAVAILABLE (503)")
    void verifyProviderToken_blankGoogleClientId_throwsProviderUnavailable() {
        TokenVerificationService unconfiguredService = new TokenVerificationService(
                "    ",
                "fb-app-id",
                "fb-app-secret",
                objectMapper
        );

        assertThatThrownBy(() -> unconfiguredService.verifyProviderToken(AuthProvider.GOOGLE, "any.valid.jwt"))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("PROVIDER_UNAVAILABLE");
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                });
    }

    // =========================================================================
    // 2. Missing, Blank, or Malformed Tokens
    // =========================================================================

    @Test
    @DisplayName("Null provider token throws INVALID_PROVIDER_CREDENTIAL (401)")
    void verifyProviderToken_nullToken_throwsInvalidCredential() {
        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, null))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }

    @Test
    @DisplayName("Blank provider token throws INVALID_PROVIDER_CREDENTIAL (401)")
    void verifyProviderToken_blankToken_throwsInvalidCredential() {
        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, "   \t\n "))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                });
    }

    @Test
    @DisplayName("Token with fewer than 3 segments throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_fewerThanThreeSegments_throwsInvalidCredential() {
        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, "header.payload"))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("expected 3 non-empty JWT segments");
                });
    }

    @Test
    @DisplayName("Token with empty segment throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_emptySegment_throwsInvalidCredential() {
        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, "header..signature"))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                });
    }

    @Test
    @DisplayName("Garbage string that cannot be parsed as JSON JWT throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_corruptBase64OrJson_throwsInvalidCredential() {
        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, "%%%not_base64%%%.###bad_json###.signature"))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                });
    }

    // =========================================================================
    // 3. Unsigned or Disallowed Algorithm Tokens
    // =========================================================================

    @Test
    @DisplayName("Unsigned token with empty signature segment throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_trailingDotUnsigned_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String header = encodeBase64Url("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
        String payload = encodeBase64Url(createValidClaimsPayloadJson("sub_1", CONFIGURED_CLIENT_ID, futureExp));
        String unsignedJwt = header + "." + payload + ".";

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, unsignedJwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                });
    }

    @Test
    @DisplayName("Token with alg: none is rejected with INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_algNone_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String header = "{\"alg\":\"none\",\"typ\":\"JWT\"}";
        String payload = createValidClaimsPayloadJson("sub_none", CONFIGURED_CLIENT_ID, futureExp);
        String jwt = createJwt(header, payload, "dGVzdF9zaWduYXR1cmU");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("unsupported or unsigned");
                });
    }

    @Test
    @DisplayName("Token with symmetric alg (HS256) is rejected with INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_symmetricAlg_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = createValidClaimsPayloadJson("sub_hs256", CONFIGURED_CLIENT_ID, futureExp);
        String jwt = createJwt(header, payload, "c3ltbWV0cmljX3NpZ25hdHVyZQ");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("unsupported or unsigned");
                });
    }

    // =========================================================================
    // 4. Expired Tokens
    // =========================================================================

    @Test
    @DisplayName("Expired Google ID token throws EXPIRED_CREDENTIAL (401)")
    void verifyProviderToken_expiredToken_throwsExpiredCredential() {
        long pastExp = (System.currentTimeMillis() / 1000L) - 300; // 5 minutes in past
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = createValidClaimsPayloadJson("sub_exp", CONFIGURED_CLIENT_ID, pastExp);
        String jwt = createJwt(header, payload, "c2lnbmF0dXJlX2J5dGVz");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("EXPIRED_CREDENTIAL");
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }

    @Test
    @DisplayName("Token missing exp claim throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_missingExpClaim_throwsInvalidCredential() {
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
                "{\"iss\":\"https://accounts.google.com\",\"aud\":\"%s\",\"sub\":\"sub_no_exp\",\"iat\":%d}",
                CONFIGURED_CLIENT_ID, (System.currentTimeMillis() / 1000L) - 60
        );
        String jwt = createJwt(header, payload, "c2lnbmF0dXJl");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("missing 'exp' claim");
                });
    }

    @Test
    @DisplayName("Token missing iat claim throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_missingIatClaim_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
                "{\"iss\":\"https://accounts.google.com\",\"aud\":\"%s\",\"sub\":\"sub_no_iat\",\"exp\":%d}",
                CONFIGURED_CLIENT_ID, futureExp
        );
        String jwt = createJwt(header, payload, "c2lnbmF0dXJl");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("missing 'iat' claim");
                });
    }

    // =========================================================================
    // 5. Wrong Audience & Issuer
    // =========================================================================

    @Test
    @DisplayName("Token with wrong audience throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_wrongAudience_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = createValidClaimsPayloadJson("sub_wrong_aud", "unauthorized-client-id.apps.googleusercontent.com", futureExp);
        String jwt = createJwt(header, payload, "c2lnbmF0dXJlX2J5dGVz");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("audience does not match");
                });
    }

    @Test
    @DisplayName("Token with untrusted issuer throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_untrustedIssuer_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        long iat = futureExp - 3600;
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
                "{\"iss\":\"https://rogue-issuer.example.com\",\"aud\":\"%s\",\"sub\":\"sub_1\",\"exp\":%d,\"iat\":%d}",
                CONFIGURED_CLIENT_ID, futureExp, iat
        );
        String jwt = createJwt(header, payload, "c2lnbmF0dXJlX2J5dGVz");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("Invalid Google ID token issuer");
                });
    }

    // =========================================================================
    // 6. Subject Claim Validation
    // =========================================================================

    @Test
    @DisplayName("Token missing sub claim throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_missingSub_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        long iat = futureExp - 3600;
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
                "{\"iss\":\"https://accounts.google.com\",\"aud\":\"%s\",\"exp\":%d,\"iat\":%d}",
                CONFIGURED_CLIENT_ID, futureExp, iat
        );
        String jwt = createJwt(header, payload, "c2lnbmF0dXJlX2J5dGVz");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("missing 'sub' claim");
                });
    }

    @Test
    @DisplayName("Token with blank sub claim throws INVALID_PROVIDER_CREDENTIAL")
    void verifyProviderToken_blankSub_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        long iat = futureExp - 3600;
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = String.format(
                "{\"iss\":\"https://accounts.google.com\",\"aud\":\"%s\",\"sub\":\"   \",\"exp\":%d,\"iat\":%d}",
                CONFIGURED_CLIENT_ID, futureExp, iat
        );
        String jwt = createJwt(header, payload, "c2lnbmF0dXJlX2J5dGVz");

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, jwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("missing 'sub' claim");
                });
    }

    // =========================================================================
    // 7. Cryptographic Signature Rejection (Forged / Incorrect Signature)
    // =========================================================================

    @Test
    @DisplayName("Token with forged signature is rejected by cryptographic verification")
    void verifyProviderToken_forgedSignature_throwsInvalidCredential() {
        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
        String payload = createValidClaimsPayloadJson("attacker_sub_01", CONFIGURED_CLIENT_ID, futureExp);
        String forgedSignature = encodeBase64Url("forged_signature_not_signed_by_google");
        String forgedJwt = createJwt(header, payload, forgedSignature);

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, forgedJwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("signature verification failed");
                });
    }

    @Test
    @DisplayName("Token signed with untrusted custom RSA key is rejected against Google public keys")
    void verifyProviderToken_untrustedRsaKeySignature_throwsInvalidCredential() throws Exception {
        // Generate an arbitrary local RSA keypair (not recognized by Google)
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        long futureExp = (System.currentTimeMillis() / 1000L) + 3600;
        String headerBase64 = encodeBase64Url("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
        String payloadBase64 = encodeBase64Url(createValidClaimsPayloadJson("attacker_sub_rsa", CONFIGURED_CLIENT_ID, futureExp));
        byte[] signingInput = (headerBase64 + "." + payloadBase64).getBytes(StandardCharsets.US_ASCII);

        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(keyPair.getPrivate());
        signer.update(signingInput);
        byte[] rawSignature = signer.sign();
        String signatureBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(rawSignature);

        String untrustedJwt = headerBase64 + "." + payloadBase64 + "." + signatureBase64;

        assertThatThrownBy(() -> service.verifyProviderToken(AuthProvider.GOOGLE, untrustedJwt))
                .isInstanceOf(AuthException.class)
                .satisfies(ex -> {
                    AuthException ae = (AuthException) ex;
                    assertThat(ae.getErrorCode()).isEqualTo("INVALID_PROVIDER_CREDENTIAL");
                    assertThat(ae.getMessage()).contains("signature verification failed");
                });
    }
}
