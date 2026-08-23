package com.snapshoot.gateway.common.websocket.worker.client;

import com.snapshoot.gateway.common.websocket.shared.AbstractWebSocketClient;
import com.snapshoot.gateway.common.websocket.worker.WorkerWebSocketRegistry;
import com.snapshoot.gateway.common.websocket.worker.dto.TaskAssignment;
import com.snapshoot.gateway.domain.enums.WebSocketPeerType;
import com.snapshoot.gateway.domain.events.TaskEnqueuedEvent;
import com.snapshoot.gateway.domain.queue.Task;
import com.snapshoot.gateway.services.TaskService;

import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * A WebSocket client to send messages to Workers.
 */
@Slf4j
@Component
public class WorkerWebSocketClient extends AbstractWebSocketClient {

    private final TaskService taskService;

    public WorkerWebSocketClient(
        ObjectMapper objectMapper,
        WorkerWebSocketRegistry registry,
        TaskService taskService
    ) {
        super(objectMapper, registry, WebSocketPeerType.WORKER);
        this.taskService = taskService;
    }

    /**
     * Catches the TaskQueuedEvent:
     * If there is an available worker
     *  -> Push that task to that worker.
     * Else
     *  -> Do nothing, leave that task in its queue.
     */
    @EventListener
    public void onTaskQueued(TaskEnqueuedEvent event) {
        Optional<String> workerId = taskService.getIdleWorker(
            event.workerType()
        );

        workerId.ifPresent(id -> sendTask(id, event.task()));
    }

    /**
     * Sends the task to the available worker.
     */
    public void sendTask(String workerId, Task task) {
        TaskAssignment assignment = new TaskAssignment(
            task.getTaskId(),
            task.getSessionId(),
            task.getPlayerId(),
            task.getImageData(),
            task.getOrientation(),
            task.getRadius()
        );

        try {
            super.sendMessage(workerId, assignment);
        } catch (IOException e) {
            log.warn(
                "Failed to send task {} to worker {}",
                assignment.taskId(),
                workerId
            );
        }
    }
}
