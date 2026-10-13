package com.zynpath.backend.operational;

import com.zynpath.backend.common.config.ProductionStartupValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductionStartupValidatorTest {

    private final ApplicationReadyEvent mockEvent = mock(ApplicationReadyEvent.class);

    private ProductionStartupValidator createValidator(
            String activeProfile,
            String backendBaseUrl,
            List<String> corsOrigins,
            String wsOrigins,
            String googleClientId,
            String datasourceUrl,
            String datasourceUsername,
            String datasourcePassword,
            String persistenceMode
    ) {
        Environment env = mock(Environment.class);
        when(env.matchesProfiles("prod", "production")).thenReturn("prod".equalsIgnoreCase(activeProfile));
        when(env.getActiveProfiles()).thenReturn(new String[]{activeProfile});

        ProductionStartupValidator validator = new ProductionStartupValidator(env);
        ReflectionTestUtils.setField(validator, "backendBaseUrl", backendBaseUrl);
        ReflectionTestUtils.setField(validator, "corsOrigins", corsOrigins);
        ReflectionTestUtils.setField(validator, "wsOrigins", wsOrigins);
        ReflectionTestUtils.setField(validator, "googleClientId", googleClientId);
        ReflectionTestUtils.setField(validator, "sessionTtlSeconds", 2592000L);
        ReflectionTestUtils.setField(validator, "datasourceUrl", datasourceUrl);
        ReflectionTestUtils.setField(validator, "datasourceUsername", datasourceUsername);
        ReflectionTestUtils.setField(validator, "datasourcePassword", datasourcePassword);
        ReflectionTestUtils.setField(validator, "persistenceMode", persistenceMode);
        return validator;
    }

    @Test
    @DisplayName("Dev profile skips production endpoint and persistence validation")
    void nonProdProfile_skipsValidation() {
        ProductionStartupValidator validator = createValidator(
                "dev", "", null, "*", "", "", "", "", "in-memory"
        );
        // Should execute smoothly without throwing
        validator.onApplicationEvent(mockEvent);
    }

    @Test
    @DisplayName("Production profile fails fast when BACKEND_BASE_URL is missing")
    void prodProfile_missingBackendBaseUrl_fails() {
        ProductionStartupValidator validator = createValidator(
                "prod",
                "",
                List.of("https://zynpath-backend-xyz.a.run.app"),
                "https://zynpath-backend-xyz.a.run.app",
                "915374872001-valid.apps.googleusercontent.com",
                "jdbc:postgresql://db.cloud.internal:5432/zynpath",
                "zynpath_prod",
                "SecretPass123",
                "jpa"
        );

        assertThatThrownBy(() -> validator.onApplicationEvent(mockEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("BACKEND_BASE_URL (zynpath.api.base-url) must be configured with a verified HTTPS endpoint");
    }

    @Test
    @DisplayName("Production profile rejects cleartext HTTP backend URL")
    void prodProfile_cleartextBackendUrl_fails() {
        ProductionStartupValidator validator = createValidator(
                "prod",
                "http://insecure-backend.run.app/api/v1",
                List.of("https://zynpath-backend-xyz.a.run.app"),
                "https://zynpath-backend-xyz.a.run.app",
                "915374872001-valid.apps.googleusercontent.com",
                "jdbc:postgresql://db.cloud.internal:5432/zynpath",
                "zynpath_prod",
                "SecretPass123",
                "jpa"
        );

        assertThatThrownBy(() -> validator.onApplicationEvent(mockEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cleartext HTTP is prohibited");
    }

    @Test
    @DisplayName("Production profile rejects unverified placeholder domains")
    void prodProfile_placeholderDomain_fails() {
        ProductionStartupValidator validator = createValidator(
                "prod",
                "https://api.zynpath.com/api/v1",
                List.of("https://api.zynpath.com"),
                "https://api.zynpath.com",
                "915374872001-valid.apps.googleusercontent.com",
                "jdbc:postgresql://db.cloud.internal:5432/zynpath",
                "zynpath_prod",
                "SecretPass123",
                "jpa"
        );

        assertThatThrownBy(() -> validator.onApplicationEvent(mockEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unverified placeholder domain");
    }

    @Test
    @DisplayName("Production profile rejects localhost or emulator addresses")
    void prodProfile_localhostBackendUrl_fails() {
        ProductionStartupValidator validator = createValidator(
                "prod",
                "https://10.0.2.2:8080/api/v1",
                List.of("https://10.0.2.2:8080"),
                "https://10.0.2.2:8080",
                "915374872001-valid.apps.googleusercontent.com",
                "jdbc:postgresql://db.cloud.internal:5432/zynpath",
                "zynpath_prod",
                "SecretPass123",
                "jpa"
        );

        assertThatThrownBy(() -> validator.onApplicationEvent(mockEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot use localhost, emulator, or private LAN addresses");
    }

    @Test
    @DisplayName("Production profile passes with verified Google Cloud Run generated HTTPS URL")
    void prodProfile_validCloudRunHttpsUrl_passes() {
        ProductionStartupValidator validator = createValidator(
                "prod",
                "https://zynpath-backend-7849102-uc.a.run.app/api/v1",
                List.of("https://zynpath-backend-7849102-uc.a.run.app"),
                "https://zynpath-backend-7849102-uc.a.run.app",
                "915374872001-valid.apps.googleusercontent.com",
                "jdbc:postgresql://db.cloud.internal:5432/zynpath",
                "zynpath_prod",
                "SecretPass123",
                "jpa"
        );

        // All checks should pass without exception
        validator.onApplicationEvent(mockEvent);
    }

    @Test
    @DisplayName("Production profile fails fast when AdMob SSV signature verification is disabled")
    void prodProfile_disabledAdMobSsvSignature_fails() {
        ProductionStartupValidator validator = createValidator(
                "prod",
                "https://zynpath-backend-7849102-uc.a.run.app/api/v1",
                List.of("https://zynpath-backend-7849102-uc.a.run.app"),
                "https://zynpath-backend-7849102-uc.a.run.app",
                "915374872001-valid.apps.googleusercontent.com",
                "jdbc:postgresql://db.cloud.internal:5432/zynpath",
                "zynpath_prod",
                "SecretPass123",
                "jpa"
        );
        ReflectionTestUtils.setField(validator, "enforceSsvSignature", false);

        assertThatThrownBy(() -> validator.onApplicationEvent(mockEvent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("AdMob SSV signature verification");
    }
}
