package com.snapshoot.gateway.common.websocket.game_server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameServerWebSocketHandler extends AbstractWebSocketHandler {

    private final GameServerWebSocketRegistry registry;

    /**
     * Connection established, add the game server to the WebSocket registry for management
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String gameServerId = (String) session.getAttributes().get("gameServerId");

        registry.add(gameServerId, session);

        log.info("Game server {} connected", gameServerId);
    }

    /**
     * Connection closed, remove it from the WebSocket registry
     */
    @Override
    public void afterConnectionClosed(
        WebSocketSession session,
        CloseStatus status
    ) {
        String gameServerId = (String) session.getAttributes().get("gameServerId");

        registry.remove(gameServerId);

        log.info("Game server {} disconnected: {}", gameServerId, status);
    }

    @Override
    protected void handleTextMessage(
        WebSocketSession session,
        TextMessage message
    ) {
        String gameServerId = (String) session.getAttributes().get("gameServerId");

        // TODO: Define protocol between game servers and gateway later
        log.info("Received message from game server {}: {}", gameServerId, message.getPayload());
    }
}
