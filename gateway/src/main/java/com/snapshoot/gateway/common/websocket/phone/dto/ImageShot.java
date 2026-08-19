package com.snapshoot.gateway.common.websocket.phone.dto;

/**
 * The binary image frame sent when the player hits shoot
 */
public record ImageShot(
    byte[] imageData
) {
}
