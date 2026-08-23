package com.snapshoot.gateway.common.websocket.shared;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.snapshoot.gateway.domain.enums.WebSocketPeerType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;


/**
 * Abstract WebSocket client for sending messages to the peer.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebSocketClient {

    private final ObjectMapper objectMapper;
    private final AbstractWebSocketRegistry registry;
    private final WebSocketPeerType peerType;

    /**
     * Send the message to that WebSocket peer. Requires the peer's id to
     * identify which WebSocket session to be used.
     */
    public void sendMessage(String id, Object message) throws IOException {
        WebSocketSession session = registry.get(id).orElse(null);

        if (session == null || !session.isOpen()) {
            log.warn("No active WebSocket session for {} {}", peerType.toString(), id);
            return;
        }

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }
}
