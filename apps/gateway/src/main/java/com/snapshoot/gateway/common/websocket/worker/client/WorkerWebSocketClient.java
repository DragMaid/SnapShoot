package com.snapshoot.gateway.common.websocket.worker.client;

import com.snapshoot.gateway.common.websocket.shared.AbstractWebSocketClient;
import com.snapshoot.gateway.common.websocket.worker.WorkerWebSocketRegistry;
import com.snapshoot.gateway.domain.enums.WebSocketPeerType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * A WebSocket client to send messages to Workers.
 */
@Component
public class WorkerWebSocketClient extends AbstractWebSocketClient {

    public WorkerWebSocketClient(
        ObjectMapper objectMapper,
        WorkerWebSocketRegistry registry
    ) {
        super(objectMapper, registry, WebSocketPeerType.WORKER);
    }
}
