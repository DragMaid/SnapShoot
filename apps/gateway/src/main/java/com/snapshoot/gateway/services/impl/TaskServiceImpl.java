package com.snapshoot.gateway.services.impl;

import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootMetadata;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.queue.Task;
import com.snapshoot.gateway.repositories.queue.IdleWorkerRepository;
import com.snapshoot.gateway.repositories.queue.TaskQueueRepository;
import com.snapshoot.gateway.services.TaskService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.snapshoot.gateway.domain.events.TaskEnqueuedEvent;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskQueueRepository taskQueueRepository;
    private final IdleWorkerRepository idleWorkerRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void submitTaskToVisionQueue(
        String sessionId,
        String playerId,
        byte[] imageData,
        PlayerShootMetadata.Orientation orientation,
        double radius
    ) {
        Task task = new Task(
            UUID.randomUUID().toString(),
            sessionId,
            playerId,
            imageData,
            orientation,
            radius,
            false,
            false
        );

        taskQueueRepository.enqueueVision(task);
        eventPublisher.publishEvent(new TaskEnqueuedEvent(task, WorkerType.VISION));
    }

    @Override
    public Optional<Task> nextVisionTask(String workerId) {
        Optional<Task> task = taskQueueRepository.pollVision(workerId);

        // If no task found for the vision worker, add that vision worker into the idle vision worker waitlist.
        if (task.isEmpty()) {
            idleWorkerRepository.addToIdleVisionWorkers(workerId);
        }
        return task;
    }

    @Override
    public Optional<Task> nextRoutingTask(String workerId) {
        Optional<Task> task = taskQueueRepository.pollRouting(workerId);

        // If no task found for the routing worker, add that routing worker into the idle routing worker waitlist.
        if (task.isEmpty()) {
            idleWorkerRepository.addToIdleRoutingWorkers(workerId);
        }
        return task;
    }

    @Override
    public Optional<String> getIdleWorker(WorkerType workerType) {
        return idleWorkerRepository.takeIdleWorker(workerType);
    }

    @Override
    public void handleVisionResult(
        String workerId,
        String taskId,
        boolean visionComputed
    ) {
        // Non-existent task warning
        Optional<Task> inFlight = taskQueueRepository.takeInFlight(workerId);
        if (inFlight.isEmpty()) {
            log.warn(
                "Vision worker {} reported a result but had no task in flight",
                workerId
            );
            return;
        }

        // Wrong task warning
        Task task = inFlight.get();
        if (!task.getTaskId().equals(taskId)) {
            log.warn(
                "Vision worker {} reported taskId {} but was holding {}",
                workerId,
                taskId,
                task.getTaskId()
            );
        }

        // Pass the task to the routing queue
        if (visionComputed) {
            // Update the task to be
            task.setVisionComputed(true);
            taskQueueRepository.enqueueRouting(task);
            eventPublisher.publishEvent(new TaskEnqueuedEvent(task, WorkerType.ROUTING));
        } else {
            log.info("Task {} discarded: vision found nothing", task.getTaskId());
        }
    }

    @Override
    public void handleRoutingResult(
        String workerId,
        String taskId,
        boolean routingComputed
    ) {
        // Non-existent task warning
        Optional<Task> inFlight = taskQueueRepository.takeInFlight(workerId);
        if (inFlight.isEmpty()) {
            log.warn(
                "Routing worker {} reported a result but had no task in flight",
                workerId
            );
            return;
        }

        // Wrong task warning
        Task task = inFlight.get();
        if (!task.getTaskId().equals(taskId)) {
            log.warn(
                "Routing worker {} reported taskId {} but was holding {}",
                workerId,
                taskId,
                task.getTaskId()
            );
        }

        // TODO: Forward the completed task to the Attribution service once it exists
        log.info(
            "Task {} routing computed: {}",
            task.getTaskId(),
            routingComputed
        );
    }

    @Override
    public void workerDisconnected(WorkerType workerType, String workerId) {
        if (workerType == WorkerType.VISION) {
            idleWorkerRepository.clearIdleVisionWorker(workerId);
        } else {
            idleWorkerRepository.clearIdleRoutingWorker(workerId);
        }
    }
}
