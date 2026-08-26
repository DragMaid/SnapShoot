package com.snapshoot.gateway.services.impl;

import com.snapshoot.gateway.common.websocket.phone.dto.PlayerShootMetadata;
import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.events.TaskEnqueuedEvent;
import com.snapshoot.gateway.domain.queue.Task;
import com.snapshoot.gateway.repositories.queue.IdleWorkerRepository;
import com.snapshoot.gateway.repositories.queue.TaskQueueRepository;
import com.snapshoot.gateway.services.TaskService;
import java.util.Optional;
import java.util.UUID;
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
            radius
        );

        taskQueueRepository.enqueueTask(task, WorkerType.VISION);
        eventPublisher.publishEvent(
            new TaskEnqueuedEvent(task, WorkerType.VISION)
        );
    }

    @Override
    public Optional<Task> nextTask(String workerId, WorkerType workerType) {
        Optional<Task> task = taskQueueRepository.pollTask(
            workerId,
            workerType
        );

        if (task.isPresent()) {
            taskQueueRepository.putInProgress(workerId, task.get());
        } else {
            idleWorkerRepository.addToIdleWorkers(workerId, workerType);
        }

        return task;
    }

    @Override
    public Optional<String> getIdleWorker(WorkerType workerType) {
        return idleWorkerRepository.takeIdleWorker(workerType);
    }

    @Override
    public void handleWorkerResult(
        String workerId,
        String taskId,
        WorkerType workerType,
        Boolean success
    ) {
        Optional<Task> maybeTask = verifyWorkerResult(workerId, taskId);

        if (maybeTask.isEmpty()) {
            log.warn(
                "{} worker {} reported taskId {} but no task found in inProgress map",
                workerType, workerId, taskId
            );
            return;
        }

        Task task = maybeTask.get();

        // Stop the computing process due to failure result.
        if (Boolean.FALSE.equals(success)) {
            log.info(
                "Task {} discarded: {} worker reported failure",
                task.getTaskId(), workerType
            );
            return;
        }

        // Forward the task until done.
        switch (workerType) {
            case VISION:
                forwardTask(task, WorkerType.ROUTING);
                break;
            case ROUTING:
                // TODO: Forward the completed task to the Attribution service once it exists
                log.info(
                    "Task {} routing computed, ready for attribution",
                    task.getTaskId()
                );
                break;
        }
    }

    @Override
    public void workerDisconnected(WorkerType workerType, String workerId) {
        idleWorkerRepository.clearIdlWorker(workerId, workerType);
    }

    /**
     * Verify if the worker's result is legit by checking it with the inProgress map this gateway keeps.
     */
     private Optional<Task> verifyWorkerResult(String workerId, String taskId) {
         return taskQueueRepository.takeInProgress(workerId)
             .filter(t -> t.getTaskId().equals(taskId))
             .or(() -> {
                 return Optional.empty();
             });
     }

     /**
      * Move a task into the next stage's queue and announce it.
      */
     private void forwardTask(Task task, WorkerType nextWorkerType) {
         taskQueueRepository.enqueueTask(task, nextWorkerType);
         eventPublisher.publishEvent(new TaskEnqueuedEvent(task, nextWorkerType));
     }
}
