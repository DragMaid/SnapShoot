package com.snapshoot.gateway.common.websocket.worker;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WorkerWebSocketRegistry {

    private final Map<String, WebSocketSession> connections = new ConcurrentHashMap<>();

    public void add(String workerId, WebSocketSession session) {
        connections.put(workerId, session);
    }

    public void remove(String workerId) {
        connections.remove(workerId);
    }

    public Optional<WebSocketSession> get(String workerId) {
        return Optional.ofNullable(connections.get(workerId));
    }
}
