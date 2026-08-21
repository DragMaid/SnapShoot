package com.snapshoot.gateway.common.websocket.phone.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/**
 * The binary image frame sent when the player hits shoot
 */
 public record PlayerShootTrigger(
     byte[] imageData,
     Orientation orientation,

     @DecimalMin("0.0")
     @DecimalMax("1.0")
     double radius
 ) {
     public record Orientation(
         double x,
         double y,
         double z
     ) {}
 }
