package com.snapshoot.gateway.domain.events;

import com.snapshoot.gateway.domain.enums.WorkerType;
import com.snapshoot.gateway.domain.queue.Task;

/**
 * Fires when a task is added into a queue
 */
 public record TaskEnqueuedEvent(
     Task task,
     WorkerType workerType
 ) {
 }
