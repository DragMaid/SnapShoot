package com.snapshoot.gateway.common.websocket.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.snapshoot.gateway.common.websocket.phone.dto.ImageShot;
import com.snapshoot.gateway.common.websocket.phone.dto.PhoneMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhoneWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        PhoneWebSocketHandler.class
    );

    private final PhoneWebSocketRegistry registry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String playerId = (String) session.getAttributes().get("playerId");

        registry.add(playerId, session);

        logger.info("Player {} connected", playerId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String playerId = (String) session.getAttributes().get("playerId");

        registry.remove(playerId);

        logger.info("Player {} disconnected: {}", playerId, status);
    }

    /**
     * Handle text messages from the phones (position, orientation, etc)
     */
    @Override
    protected void handleTextMessage(
        WebSocketSession session,
        TextMessage message
    ) {
        String playerId = (String) session.getAttributes().get("playerId");

        PhoneMessage phoneMessage = new PhoneMessage(
            PhoneMessage.MessageType.TEXT,
            message.getPayload(),
            null
        );

        // TODO: Send to Routing service
        logger.info("Send location data to Routing Service");
    }

    /**
     * Handle binary data (image frame)
     */
    @Override
    protected void handleBinaryMessage(
        WebSocketSession session,
        BinaryMessage message
    ) {
        String playerId = (String) session.getAttributes().get("playerId");

        ImageShot imageShot = new ImageShot(message.getPayload().array());

        // TODO: Send to Vision Service
        logger.info("Send image data to Vision Service");
    }
}
