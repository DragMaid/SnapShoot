package com.snapshoot.gateway.common.websocket.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapshoot.gateway.common.websocket.worker.dto.TaskAssignment;
import com.snapshoot.gateway.common.websocket.worker.dto.WorkerMessage;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.events.TaskPushedEvent;
import com.snapshoot.gateway.domain.queue.Task;
import com.snapshoot.gateway.services.TaskService;
import com.snapshoot.gateway.services.WorkerService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerWebSocketHandler extends AbstractWebSocketHandler {

    private final WorkerWebSocketRegistry registry;
    private final WorkerService workerService;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    /**
     * Connection established, add the worker to the WebSocket registry for management
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String workerId = (String) session.getAttributes().get("workerId");
        WorkerType workerType = (WorkerType) session
            .getAttributes()
            .get("workerType");

        registry.add(workerId, session);

        log.info("Worker {} ({}) connected", workerId, workerType);
    }

    /**
     * Connection closed, remove it from the WebSocket registry and delete the worker
     */
    @Override
    public void afterConnectionClosed(
        WebSocketSession session,
        CloseStatus status
    ) {
        String workerId = (String) session.getAttributes().get("workerId");
        WorkerType workerType = (WorkerType) session
            .getAttributes()
            .get("workerType");

        registry.remove(workerId);
        workerService.deleteWorker(workerId);
        taskService.workerDisconnected(workerType, workerId);

        log.info("Worker {} disconnected: {}", workerId, status);
    }

    @Override
    protected void handleTextMessage(
        WebSocketSession session,
        TextMessage message
    ) {
        String workerId = (String) session.getAttributes().get("workerId");
        WorkerType workerType = (WorkerType) session
            .getAttributes()
            .get("workerType");

        try {
            WorkerMessage workerMessage = objectMapper.readValue(
                message.getPayload(),
                WorkerMessage.class
            );

            switch (workerMessage.type()) {
                case READY -> handleReady(session, workerId, workerType);
                case RESULT -> handleResult(workerId, workerType, workerMessage);
            }
        } catch (JsonProcessingException e) {
            log.warn(
                "Invalid message from worker {}: {}",
                workerId,
                message.getPayload(),
                e
            );
        }
    }

    /**
     * A task was matched to a worker that was already parked as idle
     * — push it straight to that worker's socket.
     */
    @EventListener
    public void onTaskPushed(TaskPushedEvent event) {
        registry
            .get(event.workerId())
            .ifPresentOrElse(
                session -> sendTaskAssignment(session, event.task()),
                () ->
                    log.warn(
                        "Task {} assigned to worker {} but no active session found",
                        event.task().taskId(),
                        event.workerId()
                    )
            );
    }

    /**
     * Receive a READY message from a worker, find a task and then assign it to that worker.
     */
    private void handleReady(
        WebSocketSession session,
        String workerId,
        WorkerType workerType
    ) {
        Optional<Task> task =
            workerType == WorkerType.VISION
                ? taskService.nextVisionTask(workerId)
                : taskService.nextRoutingTask(workerId);

        task.ifPresent(t -> sendTaskAssignment(session, t));
    }

    /**
     * Send a task assignment to the worker.
     */
    private void sendTaskAssignment(WebSocketSession session, Task task) {
        TaskAssignment assignment = new TaskAssignment(
            task.taskId(),
            task.sessionId(),
            task.playerId(),
            task.imageData(),
            task.orientation()
        );

        try {
            session.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(assignment))
            );
        } catch (IOException e) {
            String workerId = (String) session.getAttributes().get("workerId");
            log.warn(
                "Failed to send task {} to worker {}",
                assignment.taskId(),
                workerId,
                e
            );
        }
    }

    /**
     * Receive the result from a worker (RESULT worker message) and pass it to the relevant result handler.
     */
    private void handleResult(
        String workerId,
        WorkerType workerType,
        WorkerMessage message
    ) {
        boolean computed = Boolean.TRUE.equals(message.computed());

        if (workerType == WorkerType.VISION) {
            taskService.handleVisionResult(
                workerId,
                message.taskId(),
                computed
            );
        } else {
            taskService.handleRoutingResult(
                workerId,
                message.taskId(),
                computed
            );
        }
    }
}
