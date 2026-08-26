package com.snapshoot.gateway.services;

import java.util.Optional;

import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootMetadata;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.queue.Task;


/**
 * Task flow:
 * Vision --> Routing --> Attribution
 */
public interface TaskService {

    /**
     * Push this task into the vision queue (vision computing is the first step)
     */
    void submitTaskToVisionQueue(
        String sessionId,
        String playerId,
        byte[] imageData,
        PlayerShootMetadata.Orientation orientation,
        double radius
    );

    /**
     * Get the next task for the worker. If not found, put the worker into the waitlist.
     */
    Optional<Task> nextTask(String workerId, WorkerType workerType);

    /**
     * Get any idle VISION/ROUTING worker.
     */
    Optional<String> getIdleWorker(WorkerType workerType);

    /**
     * Handle the result from the worker.
     */
     void handleWorkerResult(String workerId, String taskId, WorkerType workerType, Boolean success);

    /**
     * Clear a worker's idle registration, e.g. on disconnect, so it can no
     * longer be matched to a future task.
     */
    void workerDisconnected(WorkerType workerType, String workerId);
}
