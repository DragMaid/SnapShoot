package com.snapshoot.gateway.common.websocket.shared;

/**
 * Basic task assignment shared between different types of workers.
 */
public interface TaskAssignment {
    String taskId();
	String sessionId();
	String playerId();
}
