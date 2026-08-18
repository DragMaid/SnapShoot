package com.snapshoot.gateway.common.websocket;

import com.snapshoot.gateway.common.websocket.dto.PhoneMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
public class PhoneWebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        PhoneWebSocketHandler.class
    );

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

    @Override
    protected void handleBinaryMessage(
        WebSocketSession session,
        BinaryMessage message
    ) {
        String playerId = (String) session.getAttributes().get("playerId");

        PhoneMessage phoneMessage = new PhoneMessage(
            PhoneMessage.MessageType.BINARY,
            null,
            message.getPayload().array()
        );

        // TODO: Send to Vision Service
        logger.info("Send image data to Vision Service");
    }
}
