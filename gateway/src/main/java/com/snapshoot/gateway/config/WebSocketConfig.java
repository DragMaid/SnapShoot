package com.snapshoot.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.snapshoot.gateway.common.websocket.PhoneWebSocketHandler;
import com.snapshoot.gateway.common.websocket.WebSocketAuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final PhoneWebSocketHandler phoneWebSocketHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
            .addHandler(phoneWebSocketHandler, "/ws/phone-sessions/{sessionId}")
            .addInterceptors(webSocketAuthInterceptor)
            .setAllowedOrigins("*");
    }
}
