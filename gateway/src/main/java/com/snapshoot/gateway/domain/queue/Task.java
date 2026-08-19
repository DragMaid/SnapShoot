package com.snapshoot.gateway.domain.queue;

/**
 * A unit of processing work derived from a phone's "shot" (image +
 * orientation), routed first to a Vision worker and, if vision succeeds, on
 * to a Routing worker.
 */
public record Task(
    String taskId,
    String sessionId,
    String playerId,
    byte[] imageData,
    String orientation,
    boolean visionComputed,
    boolean routingComputed
) {
}
