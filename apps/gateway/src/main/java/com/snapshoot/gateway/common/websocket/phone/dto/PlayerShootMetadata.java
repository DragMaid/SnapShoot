package com.snapshoot.gateway.common.websocket.phone.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record PlayerShootMetadata(
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
