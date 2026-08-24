package com.snapshoot.gateway.common.websocket.phone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootTrigger;
import com.snapshoot.gateway.services.TaskService;

import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneWebSocketHandler extends AbstractWebSocketHandler {

    @Qualifier("msgpackObjectMapper")
    private final ObjectMapper msgpackObjectMapper;

    private final PhoneWebSocketRegistry registry;
    private final TaskService taskService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String playerId = (String) session.getAttributes().get("playerId");

        registry.add(playerId, session);

        log.info("Player {} connected", playerId);
    }

    @Override
    public void afterConnectionClosed(
        WebSocketSession session,
        CloseStatus status
    ) {
        String playerId = (String) session.getAttributes().get("playerId");

        registry.remove(playerId);

        log.info("Player {} disconnected: {}", playerId, status);
    }

    /**
     * Handle the image and orientation data sent from the phone when the user shoots.
     */
    @Override
    protected void handleBinaryMessage(
        WebSocketSession session,
        BinaryMessage message
    ) {
        Map<String, Object> sessionAttributes = session.getAttributes();
        String playerId = (String) sessionAttributes.get("playerId");
        String sessionId = (String) sessionAttributes.get("sessionId");

        // Get the binary message payload
        PlayerShootTrigger data;
        try {
            data = msgpackObjectMapper.readValue(
                message.getPayload().array(),
                PlayerShootTrigger.class
            );
        } catch (IOException e) {
            log.warn(
                "Failed to decode shoot trigger from player {}: {}",
                playerId,
                e.getMessage()
            );
            return;
        }

        // Send image shot data into the vision service
        taskService.submitTaskToVisionQueue(
            sessionId,
            playerId,
            data.imageData(),
            data.metadata().orientation(),
            data.metadata().radius()
        );
    }
}
