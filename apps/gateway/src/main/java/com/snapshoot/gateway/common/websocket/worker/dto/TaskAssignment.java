package com.snapshoot.gateway.common.websocket.worker.dto;

import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootTrigger;

/**
 * Outbound message handing a task to a worker in response to {@code READY}.
 * Deliberately excludes the task's computed flags — those are the
 * gateway's own pipeline bookkeeping, not something the worker needs.
 */
public record TaskAssignment(
    String taskId,
    String sessionId,
    String playerId,
    byte[] imageData,
    PlayerShootTrigger.Orientation orientation,
    double radius
) {}
