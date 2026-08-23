package com.snapshoot.gateway.repositories.queue;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Repository;

import com.snapshoot.gateway.domain.queue.Task;

/**
 * An in-memory task queue for Vision/Routing workers to pull from.
 *
 * A task always lands on the vision queue first. Once a worker pulls it (or
 * is matched to it directly, see {@link #putInFlight}), it moves into
 * {@code inFlight} (keyed by workerId, since a worker only ever holds one
 * task at a time) until that worker reports a result.
 */
@Repository
public class TaskQueueRepository {

    // Awaiting vision/routing tasks to be taken by the workers
    private final BlockingQueue<Task> visionTaskQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Task> routingTaskQueue = new LinkedBlockingQueue<>();

    // In progress tasks
    private final ConcurrentHashMap<String, Task> inFlight = new ConcurrentHashMap<>();

    /**
     * Add a task to the vision queue
     */
    public void enqueueVision(Task task) {
        visionTaskQueue.add(task);
    }

    /**
     * Add a task to the routing queue
     */
    public void enqueueRouting(Task task) {
        routingTaskQueue.add(task);
    }

    /**
     * Assign the task to a vision worker
     */
    public Optional<Task> pollVision(String workerId) {
        return poll(visionTaskQueue, workerId);
    }

    /**
     * Assign the task to a routing worker
     */
    public Optional<Task> pollRouting(String workerId) {
        return poll(routingTaskQueue, workerId);
    }

    /**
     * Register a task as in-flight for a worker that was matched directly
     * (e.g. it was already idle), without going through the queue.
     */
    public void putInFlight(String workerId, Task task) {
        inFlight.put(workerId, task);
    }

    public Optional<Task> takeInFlight(String workerId) {
        return Optional.ofNullable(inFlight.remove(workerId));
    }

    private Optional<Task> poll(BlockingQueue<Task> queue, String workerId) {
        Task task = queue.poll();
        if (task != null) {
            inFlight.put(workerId, task);
        }
        return Optional.ofNullable(task);
    }
}
