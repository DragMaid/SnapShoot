package com.snapshoot.gateway.common.websocket.worker.dto;

import com.snapshoot.gateway.common.websocket.shared.TaskAssignment;

// TODO: Mock Routing Task Assignment DTO
public record RoutingTaskAssigment(
    String taskId,
    String sessionId,
    String playerId
) implements TaskAssignment {
}
