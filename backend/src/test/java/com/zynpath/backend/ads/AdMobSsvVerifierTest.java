package com.zynpath.backend.ads;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.ads.service.AdMobSsvVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Base64;

import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdMobSsvVerifierTest {

    private AdMobSsvVerifier verifier;
    private KeyPair keyPair;

    @BeforeEach
    void setUp() throws Exception {
        verifier = new AdMobSsvVerifier(new ObjectMapper(), true);
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        keyPair = kpg.generateKeyPair();
    }

    @Test
    @DisplayName("extractMessageToVerify correctly excludes signature and key_id while preserving parameter order and URL-encoding")
    void extractMessageToVerify_stripsSignatureAndKeyId() {
        String rawQuery = "ad_network=5450213213286189855&ad_unit=6659667211&custom_data=%7B%22claimId%22%3A%22test%22%7D&reward_amount=1&reward_item=SOLO_HINT&timestamp=1700000000000&transaction_id=txn_001&user_id=player_001&signature=MEQCIBG3...&key_id=334455";

        String extracted = AdMobSsvVerifier.extractMessageToVerify(rawQuery);
        assertEquals("ad_network=5450213213286189855&ad_unit=6659667211&custom_data=%7B%22claimId%22%3A%22test%22%7D&reward_amount=1&reward_item=SOLO_HINT&timestamp=1700000000000&transaction_id=txn_001&user_id=player_001", extracted);
    }

    @Test
    @DisplayName("extractMessageToVerify handles signature and key_id anywhere in the query string")
    void extractMessageToVerify_signatureInMiddle() {
        String rawQuery = "ad_network=123&signature=abc&custom_data=foo&key_id=456&user_id=bar";
        String extracted = AdMobSsvVerifier.extractMessageToVerify(rawQuery);
        assertEquals("ad_network=123&custom_data=foo&user_id=bar", extracted);
    }

    @Test
    @DisplayName("Real ECDSA cryptographic signature verification succeeds with authentic key and payload")
    void verifySignature_validEcdsa_succeeds() throws Exception {
        String testKeyId = "test_key_ecdsa_01";
        verifier.registerKey(testKeyId, keyPair.getPublic());

        String message = "ad_network=123&ad_unit=456&custom_data=%7B%22claimId%22%3A%22abc%22%7D&user_id=user1";

        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(keyPair.getPrivate());
        ecdsa.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] sigBytes = ecdsa.sign();
        String base64Sig = Base64.getUrlEncoder().encodeToString(sigBytes);

        boolean verified = verifier.verifySignature(message, testKeyId, base64Sig);
        assertTrue(verified, "Signature must verify successfully with authentic ECDSA key");
    }

    @Test
    @DisplayName("Mock signature bypass is completely removed and rejected")
    void verifySignature_mockSignature_strictlyRejected() {
        // Test keyId 123456 and MEQCIBG3mock signature prefix without registered key
        boolean verifiedWithoutKey = verifier.verifySignature("ad_network=123&user_id=u1", "123456", "MEQCIBG3mock_fake_sig");
        assertFalse(verifiedWithoutKey, "Mock signature bypass must be completely rejected");

        // Even if key 123456 is registered with valid EC public key, mock signature string must fail
        verifier.registerKey("123456", keyPair.getPublic());
        boolean verifiedWithKey = verifier.verifySignature("ad_network=123&user_id=u1", "123456", "MEQCIBG3mock_fake_sig");
        assertFalse(verifiedWithKey, "Mock signature string must fail real ECDSA verification");
    }

    @Test
    @DisplayName("Tampered query payload fails signature verification")
    void verifySignature_tamperedMessage_fails() throws Exception {
        String testKeyId = "test_key_ecdsa_02";
        verifier.registerKey(testKeyId, keyPair.getPublic());

        String originalMessage = "ad_network=123&reward_amount=1&user_id=user1";
        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(keyPair.getPrivate());
        ecdsa.update(originalMessage.getBytes(StandardCharsets.UTF_8));
        String base64Sig = Base64.getUrlEncoder().encodeToString(ecdsa.sign());

        // Tamper with reward_amount from 1 to 10
        String tamperedMessage = "ad_network=123&reward_amount=10&user_id=user1";

        boolean verified = verifier.verifySignature(tamperedMessage, testKeyId, base64Sig);
        assertFalse(verified, "Tampered payload must fail verification");
    }

    @Test
    @DisplayName("Verification fails closed when key is unknown or signature is missing/invalid")
    void verifySignature_failClosed() {
        assertFalse(verifier.verifySignature("msg", null, "sig"), "Null keyId must fail");
        assertFalse(verifier.verifySignature("msg", "", "sig"), "Blank keyId must fail");
        assertFalse(verifier.verifySignature("msg", "key1", null), "Null signature must fail");
        assertFalse(verifier.verifySignature("msg", "key1", ""), "Blank signature must fail");
        assertFalse(verifier.verifySignature(null, "key1", "sig"), "Null queryString must fail");
        assertFalse(verifier.verifySignature("", "key1", "sig"), "Blank queryString must fail");
        assertFalse(verifier.verifySignature("msg", "unknown_key_9999", "sig"), "Unknown keyId must fail");
        assertFalse(verifier.verifySignature("msg", "test_key_01", "not_a_valid_base64_sig"), "Invalid signature format must fail");
    }

    @Test
    @DisplayName("Production profile cannot disable signature verification - constructor fails fast")
    void constructor_productionProfileCannotDisableSignatureEnforcement() {
        Environment prodEnv = mock(Environment.class);
        when(prodEnv.matchesProfiles("prod", "production")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                new AdMobSsvVerifier(new ObjectMapper(), prodEnv, false),
                "Disabling signature verification in prod profile must throw IllegalStateException"
        );
    }

    @Test
    @DisplayName("Production environment property cannot disable signature verification - constructor fails fast")
    void constructor_productionEnvironmentPropertyCannotDisableSignatureEnforcement() {
        Environment prodEnv = mock(Environment.class);
        when(prodEnv.getProperty("zynpath.environment")).thenReturn("production");

        assertThrows(IllegalStateException.class, () ->
                new AdMobSsvVerifier(new ObjectMapper(), prodEnv, false),
                "Disabling signature verification when zynpath.environment=production must throw IllegalStateException"
        );
    }

    @Test
    @DisplayName("registerKey in production profile throws IllegalStateException")
    void registerKey_inProduction_throwsIllegalStateException() {
        Environment prodEnv = mock(Environment.class);
        when(prodEnv.matchesProfiles("prod", "production")).thenReturn(true);
        AdMobSsvVerifier prodVerifier = new AdMobSsvVerifier(new ObjectMapper(), prodEnv, true);

        assertThrows(IllegalStateException.class, () ->
                prodVerifier.registerKey("key123", keyPair.getPublic()),
                "registerKey must throw in production"
        );
    }

    @Test
    @DisplayName("hasDuplicateParameters correctly identifies duplicate parameters in query string")
    void hasDuplicateParameters_detectsDuplicates() {
        assertTrue(AdMobSsvVerifier.hasDuplicateParameters("a=1&b=2&a=3"));
        assertTrue(AdMobSsvVerifier.hasDuplicateParameters("transaction_id=1&transaction_id=2"));
        assertFalse(AdMobSsvVerifier.hasDuplicateParameters("a=1&b=2&c=3"));
        assertFalse(AdMobSsvVerifier.hasDuplicateParameters(null));
        assertFalse(AdMobSsvVerifier.hasDuplicateParameters(""));
    }
}
