package com.zynpath.backend.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.sync.model.SyncDto.BatchSyncRequest;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SyncIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    private PlayerSession createTestSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("Submit batch progress sync records operation and returns latest progress")
    void submitBatch_validOperations_recordsProgress() throws Exception {
        PlayerSession session = createTestSession("sub_sync_01", "Sync Player 1");

        String payloadJson = "{\"levelId\":1,\"worldId\":1,\"stars\":3,\"bestTimeMs\":12500,\"movesCount\":16,\"isCompleted\":true,\"bestHintCount\":0}";

        SyncOperationDto op1 = new SyncOperationDto(
                "op_sync_01_a",
                "SOLO_LEVEL_COMPLETION",
                "level_1",
                1,
                payloadJson,
                System.currentTimeMillis()
        );

        BatchSyncRequest batch = new BatchSyncRequest(session.playerId(), List.of(op1));

        mockMvc.perform(post("/api/v1/sync/batch")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].operationId").value("op_sync_01_a"))
                .andExpect(jsonPath("$.results[0].status").value("SUCCESS"));

        // Retrieve remote progress
        mockMvc.perform(get("/api/v1/sync/progress")
                .header("Authorization", "Bearer " + session.sessionToken())
                .param("playerId", session.playerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress[0].levelId").value(1))
                .andExpect(jsonPath("$.progress[0].stars").value(3));
    }

    @Test
    @DisplayName("Sync batch operation deduplication: replaying same operationId returns IGNORED_DUPLICATE")
    void submitBatch_duplicateOperationId_isIgnored() throws Exception {
        PlayerSession session = createTestSession("sub_sync_dup_01", "Sync Player 2");

        String payloadJson = "{\"levelId\":2,\"worldId\":1,\"stars\":2,\"bestTimeMs\":20000,\"movesCount\":16,\"isCompleted\":true,\"bestHintCount\":0}";

        SyncOperationDto op = new SyncOperationDto(
                "op_unique_dup_101",
                "SOLO_LEVEL_COMPLETION",
                "level_2",
                1,
                payloadJson,
                System.currentTimeMillis()
        );

        BatchSyncRequest batch = new BatchSyncRequest(session.playerId(), List.of(op));

        // 1st submission -> SUCCESS
        mockMvc.perform(post("/api/v1/sync/batch")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].status").value("SUCCESS"));

        // 2nd submission (replayed operationId) -> IGNORED_DUPLICATE
        mockMvc.perform(post("/api/v1/sync/batch")
                .header("Authorization", "Bearer " + session.sessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].status").value("IGNORED_DUPLICATE"));
    }
}
