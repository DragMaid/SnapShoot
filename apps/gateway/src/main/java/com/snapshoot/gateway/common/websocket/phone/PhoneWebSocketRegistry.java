package com.snapshoot.gateway.common.websocket.phone;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PhoneWebSocketRegistry {

    private final Map<String, WebSocketSession> connections = new ConcurrentHashMap<>();

    public void add(String playerId, WebSocketSession session) {
        connections.put(playerId, session);
    }

    public void remove(String playerId) {
        connections.remove(playerId);
    }

    public Optional<WebSocketSession> get(String playerId) {
        return Optional.ofNullable(connections.get(playerId));
    }
}
