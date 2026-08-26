package com.snapshoot.gateway.repositories.queue;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Repository;

import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.queue.Task;

/**
 * An in-memory tasks queue for the distributed Vision/Routing workers system.
 */
@Repository
public class TaskQueueRepository {

    // Awaiting vision/routing tasks to be taken by the workers
    private final BlockingQueue<Task> visionTaskQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Task> routingTaskQueue = new LinkedBlockingQueue<>();

    // In progress tasks (workerId -> Task). Use this to check whether the result the workers give back are legitimate
    // We don't want to receive any shit out of no where.
    private final ConcurrentHashMap<String, Task> inProgress = new ConcurrentHashMap<>();

    /**
     * Enqueue a task into the correct queue.
     */
    public void enqueueTask(Task task, WorkerType workerType) {
        switch (workerType) {
           	case VISION:
          		visionTaskQueue.add(task);
          		break;
            case ROUTING:
                routingTaskQueue.add(task);
                break;
        }
    }

    /**
     * Poll a task from its queue.
     */
    public Optional<Task> pollTask(String workerId, WorkerType workerType) {
        BlockingQueue<Task> queue = switch (workerType) {
            case VISION -> visionTaskQueue;
            case ROUTING -> routingTaskQueue;
        };

        Task task = queue.poll();

        return Optional.ofNullable(task);
    }

    /**
     * Register a task as in-progress for the worker.
     */
    public void putInProgress(String workerId, Task task) {
        inProgress.put(workerId, task);
    }

    /**
     * Stop tracking that task as in-progress from that worker.
     */
    public Optional<Task> takeInProgress(String workerId) {
        return Optional.ofNullable(inProgress.remove(workerId));
    }
}
