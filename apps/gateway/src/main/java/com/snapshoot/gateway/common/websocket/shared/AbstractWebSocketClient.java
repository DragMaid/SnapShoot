package com.snapshoot.gateway.common.websocket.shared;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebSocketClient {

    private final ObjectMapper objectMapper;
    private final AbstractWebSocketRegistry registry;
    private final String name;

    public void sendMessage(String id, Object message) {
        WebSocketSession session = registry.get(id).orElse(null);

        if (session == null || !session.isOpen()) {
            log.warn("No active WebSocket session for game server {}", gameServerId);
            return;
        }

        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.warn("Failed to send message to game server {}", gameServerId, e);
        }
    }

}
