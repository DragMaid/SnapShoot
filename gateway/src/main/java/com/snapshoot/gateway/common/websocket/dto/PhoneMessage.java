package com.snapshoot.gateway.common.websocket.dto;

// TODO: Mock Phone message for now, which simulates image and location data
public record PhoneMessage(
    MessageType type,
    String text,
    byte[] data
) {
    public enum MessageType {
        TEXT,
        BINARY
    }
}
