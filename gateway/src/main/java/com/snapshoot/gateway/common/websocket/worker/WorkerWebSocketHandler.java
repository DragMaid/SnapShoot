package com.snapshoot.gateway.common.websocket.worker;

import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.services.WorkerService;

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

    private final WorkerService workerService;
    private final WorkerWebSocketRegistry registry;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String workerId = (String) session.getAttributes().get("workerId");
        WorkerType workerType = (WorkerType) session.getAttributes().get("workerType");

        registry.add(workerId, session);

        log.info("Worker {} ({}) connected", workerId, workerType);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String workerId = (String) session.getAttributes().get("workerId");

        registry.remove(workerId);
        workerService.deleteWorker(workerId);

        log.info("Worker {} disconnected: {}", workerId, status);
    }

    @Override
    protected void handleTextMessage(
        WebSocketSession session,
        TextMessage message
    ) {
        String workerId = (String) session.getAttributes().get("workerId");

        // TODO: Handle results/status coming back from the Vision/Routing worker
        log.info("Received message from worker {}", workerId);
    }
}
