package com.snapshoot.gateway.dto.session;

public record SessionResponse(
    String token,
    String sessionId
) {}
