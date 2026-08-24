package com.snapshoot.gateway.common.websocket.game_server.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapshoot.gateway.common.websocket.game_server.GameServerWebSocketRegistry;
import com.snapshoot.gateway.common.websocket.shared.AbstractWebSocketClient;
import com.snapshoot.gateway.domain.enums.WebSocketPeerType;
import org.springframework.stereotype.Component;

/**
 * A WebSocket client to message to the Game Server.
 */
@Component
public class GameServerWebSocketClient extends AbstractWebSocketClient {

    public GameServerWebSocketClient(
        ObjectMapper objectMapper,
        GameServerWebSocketRegistry registry
    ) {
        super(objectMapper, registry, WebSocketPeerType.GAME_SERVER);
    }
}
