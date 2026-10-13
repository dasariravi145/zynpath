package com.zynpath.backend.ads.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Validates Google AdMob Server-Side Verification (SSV) callback cryptographic signatures.
 * Verifies SHA256withECDSA signatures using Google's trusted verification public keys.
 */
@Component
public class AdMobSsvVerifier {

    private static final Logger log = LoggerFactory.getLogger(AdMobSsvVerifier.class);
    private static final String GOOGLE_KEYS_URL = "https://www.gstatic.com/admob/reward/verifier-keys.json";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final boolean enforceSsvSignature;
    private final boolean isProduction;
    private final Map<String, PublicKey> keyCache = new ConcurrentHashMap<>();
    private volatile long lastFetchTime = 0L;

    @Autowired
    public AdMobSsvVerifier(
            ObjectMapper objectMapper,
            Environment environment,
            @Value("${zynpath.ads.ssv.enforce-signature:true}") boolean enforceSsvSignature
    ) {
        this.objectMapper = objectMapper;
        this.isProduction = checkIsProduction(environment);
        if (this.isProduction && !enforceSsvSignature) {
            throw new IllegalStateException("AdMob SSV signature verification cannot be disabled in production profile");
        }
        this.enforceSsvSignature = enforceSsvSignature;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public AdMobSsvVerifier(ObjectMapper objectMapper, boolean enforceSsvSignature) {
        this(objectMapper, null, enforceSsvSignature);
    }

    public AdMobSsvVerifier(ObjectMapper objectMapper) {
        this(objectMapper, null, true);
    }

    private static boolean checkIsProduction(Environment environment) {
        if (environment != null) {
            if (environment.matchesProfiles("prod", "production")) {
                return true;
            }
            String envProp = environment.getProperty("zynpath.environment");
            if ("production".equalsIgnoreCase(envProp) || "prod".equalsIgnoreCase(envProp)) {
                return true;
            }
            String springProfiles = environment.getProperty("spring.profiles.active");
            if (springProfiles != null && (springProfiles.contains("prod") || springProfiles.contains("production"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts the raw URL-encoded message payload to verify by excluding signature and key_id.
     * Preserves original parameter ordering and encoding per Google AdMob SSV requirements.
     */
    public static String extractMessageToVerify(String rawQueryString) {
        if (rawQueryString == null || rawQueryString.isBlank()) {
            return "";
        }
        String query = rawQueryString.startsWith("?") ? rawQueryString.substring(1) : rawQueryString;
        String[] params = query.split("&");
        java.util.List<String> filtered = new java.util.ArrayList<>();
        for (String p : params) {
            if (!p.startsWith("signature=") && !p.startsWith("key_id=")) {
                filtered.add(p);
            }
        }
        return String.join("&", filtered);
    }

    /**
     * Verifies the cryptographic signature of an incoming AdMob SSV callback.
     *
     * @param queryString The query string (excluding signature & key_id)
     * @param keyId       Google key_id parameter
     * @param signature   Base64 or URL-safe base64 signature
     * @return true if cryptographically valid
     */
    public boolean verifySignature(String queryString, String keyId, String signature) {
        if (signature == null || signature.isBlank()) {
            log.warn("AdMob SSV verification failed: signature is missing");
            return false;
        }

        if (keyId == null || keyId.isBlank()) {
            log.warn("AdMob SSV verification failed: keyId is missing");
            return false;
        }

        if (queryString == null || queryString.isBlank()) {
            log.warn("AdMob SSV verification failed: queryString is missing");
            return false;
        }

        if (!enforceSsvSignature) {
            if (isProduction) {
                log.error("AdMob SSV signature verification cannot be disabled in production profile");
                return false;
            }
            log.info("AdMob SSV signature enforcement disabled in non-production profile");
            return true;
        }

        try {
            PublicKey publicKey = getPublicKey(keyId);
            if (publicKey == null) {
                log.warn("AdMob SSV verification failed: public key not found for keyId={}", keyId);
                return false;
            }

            byte[] signatureBytes = decodeSignature(signature);
            byte[] dataBytes = queryString.getBytes(StandardCharsets.UTF_8);

            Signature ecdsa = Signature.getInstance("SHA256withECDSA");
            ecdsa.initVerify(publicKey);
            ecdsa.update(dataBytes);

            boolean verified = ecdsa.verify(signatureBytes);
            if (!verified) {
                log.warn("AdMob SSV signature mismatch for keyId={}", keyId);
            }
            return verified;
        } catch (Exception e) {
            log.warn("Error verifying AdMob SSV signature: {}", e.getMessage());
            return false;
        }
    }

    public static boolean hasDuplicateParameters(String rawQueryString) {
        if (rawQueryString == null || rawQueryString.isBlank()) {
            return false;
        }
        String query = rawQueryString.startsWith("?") ? rawQueryString.substring(1) : rawQueryString;
        String[] params = query.split("&");
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (String p : params) {
            if (p.isBlank()) continue;
            int eqIdx = p.indexOf('=');
            String key = eqIdx > 0 ? p.substring(0, eqIdx) : p;
            if (!seen.add(key.toLowerCase(java.util.Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    public void registerKey(String keyId, PublicKey key) {
        if (isProduction) {
            throw new IllegalStateException("registerKey cannot be called in production profile");
        }
        keyCache.put(keyId, key);
    }

    private PublicKey getPublicKey(String keyId) {
        PublicKey key = keyCache.get(keyId);
        if (key != null) {
            return key;
        }

        long now = System.currentTimeMillis();
        if (lastFetchTime == 0L || (now - lastFetchTime > 60000L)) { // refresh cache at most once per minute on miss
            fetchGoogleKeys();
        }

        return keyCache.get(keyId);
    }

    private synchronized void fetchGoogleKeys() {
        try {
            lastFetchTime = System.currentTimeMillis();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GOOGLE_KEYS_URL))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.has("keys") && root.get("keys").isArray()) {
                    KeyFactory keyFactory = KeyFactory.getInstance("EC");
                    for (JsonNode keyNode : root.get("keys")) {
                        String id = keyNode.path("keyId").asText();
                        String base64 = keyNode.path("base64").asText();
                        if (!id.isBlank() && !base64.isBlank()) {
                            byte[] decodedKey = Base64.getDecoder().decode(base64);
                            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
                            keyCache.put(id, pubKey);
                        }
                    }
                    log.info("Loaded {} Google AdMob SSV verification keys", keyCache.size());
                }
            }
        } catch (Exception e) {
            log.warn("Unable to fetch Google AdMob verification keys: {}", e.getMessage());
        }
    }

    private byte[] decodeSignature(String signature) {
        String cleanSig = signature.trim();
        try {
            return Base64.getUrlDecoder().decode(cleanSig);
        } catch (IllegalArgumentException e) {
            return Base64.getDecoder().decode(cleanSig);
        }
    }
}
