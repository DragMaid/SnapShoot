package com.snapshoot.gateway.common.websocket.phone;

import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootTrigger;
import com.snapshoot.gateway.services.TaskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneWebSocketHandler extends AbstractWebSocketHandler {

    private final ObjectMapper objectMapper;
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

        PlayerShootTrigger imageShot = objectMapper.readValue(message.getPayload().array(), PlayerShootTrigger.class);

        // Send image shot data into the vision service
        taskService.submitTask(sessionId, playerId, imageShot.imageData(), imageShot.orientation(), imageShot.radius());
    }
}
