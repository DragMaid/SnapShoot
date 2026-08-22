package com.snapshoot.gateway.common.websocket.game_server;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class GameServerWebSocketRegistry {

    private final Map<String, WebSocketSession> connections = new ConcurrentHashMap<>();

    public void add(String gameServerId, WebSocketSession session) {
        connections.put(gameServerId, session);
    }

    public void remove(String gameServerId) {
        connections.remove(gameServerId);
    }

    public Optional<WebSocketSession> get(String gameServerId) {
        return Optional.ofNullable(connections.get(gameServerId));
    }
}
