package com.snapshoot.gateway.domain.events;

import com.snapshoot.gateway.domain.queue.Task;

/**
 * Published when a task is pushed straight to an idle worker (rather than
 * sitting in a queue), so the WebSocket layer can push it over that
 * worker's connection.
 */
public record TaskPushedEvent(String workerId, Task task) {
}
