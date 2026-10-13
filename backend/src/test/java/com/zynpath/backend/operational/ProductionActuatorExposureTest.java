package com.zynpath.backend.operational;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression tests confirming production Actuator exposure boundaries and configuration.
 *
 * Implements Step 17.20 Task 1:
 * - Production HTTP actuator exposure includes only 'health' and 'info'.
 * - /actuator/metrics is strictly inaccessible over public HTTP.
 * - Internal metrics collection remains unhindered.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductionActuatorExposureTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("1. application-prod.yml strictly limits actuator web exposure to health and info")
    @SuppressWarnings("unchecked")
    void applicationProdYml_exposesOnlyHealthAndInfo() throws Exception {
        ClassPathResource resource = new ClassPathResource("application-prod.yml");
        assertThat(resource.exists()).isTrue();

        Yaml yaml = new Yaml();
        try (InputStream is = resource.getInputStream()) {
            Map<String, Object> root = yaml.load(is);
            Map<String, Object> management = (Map<String, Object>) root.get("management");
            assertThat(management).isNotNull();

            Map<String, Object> endpoints = (Map<String, Object>) management.get("endpoints");
            assertThat(endpoints).isNotNull();

            Map<String, Object> web = (Map<String, Object>) endpoints.get("web");
            assertThat(web).isNotNull();

            Map<String, Object> exposure = (Map<String, Object>) web.get("exposure");
            assertThat(exposure).isNotNull();

            String include = (String) exposure.get("include");
            assertThat(include).isNotNull();
            assertThat(include.trim()).isEqualTo("health, info");
            assertThat(include).doesNotContain("metrics");
            assertThat(include).doesNotContain("*");

            Map<String, Object> endpoint = (Map<String, Object>) management.get("endpoint");
            assertThat(endpoint).isNotNull();

            Map<String, Object> health = (Map<String, Object>) endpoint.get("health");
            assertThat(health).isNotNull();
            assertThat(health.get("show-details")).isEqualTo("never");
            assertThat(health.get("probes")).isNotNull();
        }
    }

    @Test
    @DisplayName("2. /actuator/health is accessible and returns UP")
    void actuatorHealth_isAccessible() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("3. /actuator/metrics is inaccessible to unauthenticated requests (never 200)")
    void actuatorMetrics_isInaccessible() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assertThat(statusCode)
                            .withFailMessage("Expected /actuator/metrics to be inaccessible (401 or 404), got HTTP %d", statusCode)
                            .isIn(401, 404);
                });
    }

    @Test
    @DisplayName("4. Sensitive management endpoints (/env, /beans, /mappings) are inaccessible")
    void sensitiveActuatorEndpoints_areInaccessible() throws Exception {
        for (String endpoint : new String[]{"/actuator/env", "/actuator/beans", "/actuator/mappings", "/actuator/configprops"}) {
            mockMvc.perform(get(endpoint))
                    .andExpect(result -> {
                        int statusCode = result.getResponse().getStatus();
                        assertThat(statusCode)
                                .withFailMessage("Expected %s to be inaccessible (401 or 404), got HTTP %d", endpoint, statusCode)
                                .isIn(401, 404);
                    });
        }
    }
}
