package com.snapshoot.gateway.domain.queue;

import com.snapshoot.gateway.common.websocket.phone.dto.ImageShot;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A unit of processing work derived from a phone's "shot" (image +
 * orientation), routed first to a Vision worker and, if vision succeeds, on
 * to a Routing worker.
 */
 @Data
 @AllArgsConstructor
 public class Task {
     private String taskId;
     private String sessionId;
     private String playerId;
     private byte[] imageData;
     private ImageShot.Orientation orientation;
     private boolean visionComputed;
     private boolean routingComputed;
 }
