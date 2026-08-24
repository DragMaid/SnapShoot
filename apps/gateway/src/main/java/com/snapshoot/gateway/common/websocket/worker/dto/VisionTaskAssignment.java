package com.snapshoot.gateway.common.websocket.worker.dto;

import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootMetadata;
import com.snapshoot.gateway.common.websocket.shared.TaskAssignment;

/**
 * DTO to send task to vision workers
 */
public record VisionTaskAssignment(
    String taskId,
    String sessionId,
    String playerId,
    byte[] imageData,
    PlayerShootMetadata.Orientation orientation,
    double radius
) implements TaskAssignment {}
