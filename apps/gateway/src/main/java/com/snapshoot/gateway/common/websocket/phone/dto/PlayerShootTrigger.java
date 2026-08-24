package com.snapshoot.gateway.common.websocket.phone.dto;

/**
 * The payload that is sent over WebSocket when a player hits "shoot"
 */
 public record PlayerShootTrigger(
     PlayerShootMetadata metadata,
     byte[] imageData
 ) {
 }
