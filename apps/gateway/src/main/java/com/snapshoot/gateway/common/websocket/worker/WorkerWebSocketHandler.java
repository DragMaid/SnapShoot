package com.snapshoot.gateway.common.websocket.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snapshoot.gateway.common.websocket.worker.client.WorkerWebSocketClient;
import com.snapshoot.gateway.common.websocket.worker.dto.WorkerMessage;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.queue.Task;
import com.snapshoot.gateway.services.TaskService;
import com.snapshoot.gateway.services.WorkerService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final WorkerWebSocketClient client;

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

        task.ifPresent(t -> client.sendTask(workerId, t, workerType));
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
