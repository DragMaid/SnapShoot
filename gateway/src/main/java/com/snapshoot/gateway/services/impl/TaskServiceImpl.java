package com.snapshoot.gateway.services.impl;

import com.snapshoot.gateway.common.websocket.phone.dto.ImageShot;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.events.TaskPushedEvent;
import com.snapshoot.gateway.domain.queue.Task;
import com.snapshoot.gateway.repositories.queue.IdleWorkerRepository;
import com.snapshoot.gateway.repositories.queue.TaskQueueRepository;
import com.snapshoot.gateway.services.TaskService;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskQueueRepository taskQueueRepository;
    private final IdleWorkerRepository idleWorkerRepository;
    private final ApplicationEventPublisher eventPublisher;

    /*
        TaskQueueRepository and IdleWorkerRepository are not aware of each other.
        For example:
        - Thread A: Hits submitTask()
        - Thread B: Hits nextVisionTask()
        Without locking one, it could lead to the state where a task is enqueue and a worker is marked as idle
        => 1 idle worker is not aware of the current available task

        With the lock, thread B will wait for thread A to finish. For instance:
        - Thread A: Finished with one task enqueued.
        - Thread B: Get started. Then sees that there is one task in the queue. Return the task so the gateway can assign that task to that worker.
    */
    private final Object visionLock = new Object();
    private final Object routingLock = new Object();

    @Override
    public void submitTask(
        String sessionId,
        String playerId,
        byte[] imageData,
        ImageShot.Orientation orientation
    ) {
        Task task = new Task(
            UUID.randomUUID().toString(),
            sessionId,
            playerId,
            imageData,
            orientation,
            false,
            false
        );

        synchronized (visionLock) {
            matchOrEnqueue(
                task,
                idleWorkerRepository.takeIdleVisionWorker(),
                taskQueueRepository::enqueueVision
            );
        }
    }

    @Override
    public Optional<Task> nextVisionTask(String workerId) {
        synchronized (visionLock) {
            Optional<Task> task = taskQueueRepository.pollVision(workerId);

            // If no task found for the vision worker, add that vision worker into the idle vision worker waitlist.
            if (task.isEmpty()) {
                idleWorkerRepository.markIdleVision(workerId);
            }
            return task;
        }
    }

    @Override
    public Optional<Task> nextRoutingTask(String workerId) {
        synchronized (routingLock) {
            Optional<Task> task = taskQueueRepository.pollRouting(workerId);

            // If no task found for the routing worker, add that routing worker into the idle routing worker waitlist.
            if (task.isEmpty()) {
                idleWorkerRepository.markIdleRouting(workerId);
            }
            return task;
        }
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

            synchronized (routingLock) {
                matchOrEnqueue(
                    task,
                    idleWorkerRepository.takeIdleRoutingWorker(),
                    taskQueueRepository::enqueueRouting
                );
            }
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

    /**
     * If an idle worker was waiting, hand the task straight to it and
     * publish an event so the WebSocket layer can push it; otherwise fall
     * back to queuing the task normally. Must be called under the lock
     * matching {@code task}'s queue (vision/routing).
     */
    private void matchOrEnqueue(
        Task task,
        Optional<String> idleWorkerId,
        Consumer<Task> enqueue
    ) {
        if (idleWorkerId.isPresent()) {
            String workerId = idleWorkerId.get();
            taskQueueRepository.putInFlight(workerId, task);
            eventPublisher.publishEvent(new TaskPushedEvent(workerId, task));
        } else {
            enqueue.accept(task);
        }
    }
}
