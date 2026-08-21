package com.snapshoot.gateway.services;

import java.util.Optional;

import com.snapshoot.gateway.common.websocket.phone.dto.ImageShot;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.queue.Task;


/**
 * Task flow:
 * Vision --> Routing --> Attribution
 */
public interface TaskService {

    /**
     * Push this task into the idle vision worker (if any). Else, enqueue it into the vision tasks queue.
     */
    void submitTask(
        String sessionId,
        String playerId,
        byte[] imageData,
        ImageShot.Orientation orientation
    );

    /**
     * Get the next task for vision workers. If not found, put that vision worker into a waitlist.
     */
    Optional<Task> nextVisionTask(String workerId);

    /**
     * Get the next task for routing workers. If not found, put that routing worker into a waitlist.
     */
    Optional<Task> nextRoutingTask(String workerId);

    /**
     * Handle the result from the vision worker
     */
    void handleVisionResult(String workerId, String taskId, boolean visionComputed);

    /**
     * Handle the result from the routing worker
     */
    void handleRoutingResult(String workerId, String taskId, boolean routingComputed);

    /**
     * Clear a worker's idle registration, e.g. on disconnect, so it can no
     * longer be matched to a future task.
     */
    void workerDisconnected(WorkerType workerType, String workerId);
}
