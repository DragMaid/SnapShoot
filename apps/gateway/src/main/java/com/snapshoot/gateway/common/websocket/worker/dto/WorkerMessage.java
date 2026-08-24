package com.snapshoot.gateway.common.websocket.worker.dto;

/**
 * Inbound message from a worker: either a request for the next task
 * ({@code READY}) or a completed job's result ({@code RESULT}).
 */
public record WorkerMessage(
    WorkerMessageType type,
    String taskId,
    Boolean success
) {
    public enum WorkerMessageType {
        READY,
        RESULT
    }
}
