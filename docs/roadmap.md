# Roadmap

This reflects current direction, not a committed schedule. Update it as
services move between stages.

## In progress

- **[Vision](services/vision.md)** — `apps/vision` exists; model evaluation
  (YOLO, MediaPipe) not yet settled.
- **[Routing](services/routing.md)** — RF localization approach (RSSI/CSI,
  fingerprinting, Kalman filtering) under research.
- **[Distributed Processing](services/distributed-processing.md)** — worker
  scheduling design in progress.
- **[Frontend](services/frontend.md)** — React app scaffold.

## Proposed, not yet started

- **[Gateway](services/gateway.md)** — needed before any real phone can
  connect end-to-end.
- **[Attribution](services/attribution.md)** — needed to close the loop
  between Vision and the Game Server; see
  [ADR 0003](adr/0003-attribution-service.md).
- **[Game Server](services/game-server.md)** — authoritative gameplay logic.
- **[Recorder](services/recorder.md)** — gameplay persistence for replay and
  ML datasets.

## Suggested near-term milestones

1. Define `libs/protocol` message schemas for the hit detection pipeline
   (frame upload, trigger event, hit candidate, position estimate,
   attributed hit, game state update).
2. Stand up Gateway with a minimal WebSocket passthrough, so an end-to-end
   phone → Vision → Attribution → Game Server → phone loop is testable, even
   with stub logic in each service.
3. Settle the Vision model choice via a documented
   [research](research/overview.md) comparison, not ad hoc trial.
4. Settle the Routing localization approach the same way.
5. Build out `tests/integration/` for the Gateway → Vision and
   Vision/Routing → Attribution boundaries as soon as those services have
   real contracts.
6. Stand up `deployment/` for local Docker Compose before targeting
   Kubernetes.

## Longer-term / future

- Temporal interpolation, IMU-assisted aiming, multi-frame analysis (see
  [Hit Detection Pipeline](architecture/data-flow.md#planned-improvements)).
- Recorder-driven ML training loop feeding back into Vision model
  improvements.
- Admin app under `apps/admin` for arena/match operators.
