package com.zynpath.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthProvider;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.model.VerifiedProviderIdentity;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.websocket.MultiplayerWebSocketHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MultiplayerWebSocketTest {

    @Autowired
    private MultiplayerWebSocketHandler webSocketHandler;

    @Autowired
    private PlayerAccountService playerAccountService;

    @Autowired
    private SessionSecurityService sessionSecurityService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlayerSession createTestSession(String sub, String name) {
        VerifiedProviderIdentity identity = new VerifiedProviderIdentity(AuthProvider.GOOGLE, sub, name, sub + "@example.com", true);
        PlayerAccount account = playerAccountService.getOrCreateForExternalIdentity(identity, null);
        return sessionSecurityService.createSession(account.playerId());
    }

    @Test
    @DisplayName("WebSocket connection without token receives AUTH_REQUIRED error envelope")
    void connectionWithoutToken_receivesAuthRequired() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("ws_session_unauth_01");
        when(session.getUri()).thenReturn(URI.create("ws://localhost:8080/ws/multiplayer"));
        when(session.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(session);

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        String payload = captor.getValue().getPayload();
        assertThat(payload).contains("AUTH_REQUIRED");
    }

    @Test
    @DisplayName("WebSocket connection with valid token authenticates successfully")
    void connectionWithValidToken_authenticates() throws Exception {
        PlayerSession playerSession = createTestSession("sub_ws_auth_01", "WS Player 1");

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("ws_session_auth_01");
        when(session.getUri()).thenReturn(URI.create("ws://localhost:8080/ws/multiplayer?token=" + playerSession.sessionToken()));
        when(session.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(session);

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        String payload = captor.getValue().getPayload();
        assertThat(payload).contains("AUTHENTICATED");
    }

    @Test
    @DisplayName("Subscribing to a non-existent match returns error")
    void subscribeToNonExistentMatch_returnsError() throws Exception {
        PlayerSession playerSession = createTestSession("sub_ws_match_err", "WS Player 2");

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("ws_session_sub_err");
        when(session.getUri()).thenReturn(URI.create("ws://localhost:8080/ws/multiplayer?token=" + playerSession.sessionToken()));
        when(session.isOpen()).thenReturn(true);

        webSocketHandler.afterConnectionEstablished(session);

        // Send SUBSCRIBE_MATCH for non-existent match
        String subscribeMsg = objectMapper.writeValueAsString(Map.of(
                "type", "SUBSCRIBE_MATCH",
                "matchId", "match_non_existent_9999",
                "sequenceNumber", 1
        ));

        webSocketHandler.handleMessage(session, new TextMessage(subscribeMsg));

        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, atLeastOnce()).sendMessage(captor.capture());

        String lastMessage = captor.getValue().getPayload();
        assertThat(lastMessage).contains("MATCH_NOT_FOUND");
    }
}
