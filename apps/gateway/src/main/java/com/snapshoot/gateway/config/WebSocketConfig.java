package com.snapshoot.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.snapshoot.gateway.common.websocket.game_server.GameServerWebSocketAuthInterceptor;
import com.snapshoot.gateway.common.websocket.game_server.GameServerWebSocketHandler;
import com.snapshoot.gateway.common.websocket.phone.PhoneWebSocketAuthInterceptor;
import com.snapshoot.gateway.common.websocket.phone.PhoneWebSocketHandler;
import com.snapshoot.gateway.common.websocket.worker.WorkerWebSocketAuthInterceptor;
import com.snapshoot.gateway.common.websocket.worker.WorkerWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    // Handle connection with the players
    private final PhoneWebSocketHandler phoneWebSocketHandler;
    private final PhoneWebSocketAuthInterceptor phoneWebSocketAuthInterceptor;

    // Handle connection with the workers from Vision and Routing services
    private final WorkerWebSocketHandler workerWebSocketHandler;
    private final WorkerWebSocketAuthInterceptor workerWebSocketAuthInterceptor;

    // Handle connection with the game servers
    private final GameServerWebSocketHandler gameServerWebSocketHandler;
    private final GameServerWebSocketAuthInterceptor gameServerWebSocketAuthInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(phoneWebSocketHandler, "/ws/phone/{sessionId}")
            .addInterceptors(phoneWebSocketAuthInterceptor)
            .setAllowedOrigins("*");

        registry
            .addHandler(workerWebSocketHandler, "/ws/workers/{workerId}")
            .addInterceptors(workerWebSocketAuthInterceptor)
            .setAllowedOrigins("*");

        registry
            .addHandler(gameServerWebSocketHandler, "/ws/game-servers/{gameServerId}")
            .addInterceptors(gameServerWebSocketAuthInterceptor)
            .setAllowedOrigins("*");
    }
}
