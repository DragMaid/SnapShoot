# Services

RLPUBG is organized as a set of independently deployable services, each
living under its own directory in `apps/`. This page is the inventory; each
service has a dedicated page under [Services](../services/gateway.md) with
its detailed responsibilities.

## Current domains

These four domains exist today, in some stage of design or implementation:

- **[Vision](../services/vision.md)** — computer vision processing: human
  detection, tracking, pose/segmentation, hit candidate detection, confidence
  scoring, GPU inference.
- **[Routing](../services/routing.md)** — indoor localization using wireless
  infrastructure (RSSI, SNR, CSI, fingerprinting, Kalman filtering).
- **[Distributed Processing](../services/distributed-processing.md)** —
  worker management, load balancing, GPU scheduling, room assignment, job
  dispatch.
- **[Frontend](../services/frontend.md)** — the React player UI.

## Proposed services

These are designed but not yet built:

- **[Gateway](../services/gateway.md)** — the single entry point every phone
  talks to: auth, session management, WebSocket connections, frame/trigger
  forwarding, rate limiting.
- **[Game Server](../services/game-server.md)** — authoritative game logic:
  health, damage, teams, respawn, weapons, cooldowns, timers, scoring, rules.
- **[Attribution](../services/attribution.md)** — resolves a Vision hit
  candidate to a specific player identity using Routing's RF position
  estimates, before handing off to the Game Server.
- **[Recorder](../services/recorder.md)** — persists gameplay for replay,
  debugging, dataset generation, and future ML training.

## Ownership boundaries

A few rules keep responsibilities from blurring across services:

- **Vision decides *if* a hit is geometrically plausible** (crosshair
  intersects a body). It does not know or decide *who* that body is, and it
  does not apply damage.
- **Routing decides *where* players are**, using RF signals. It does not
  interpret camera frames and does not decide hits.
- **Attribution decides *who*** — it fuses a Vision hit candidate with
  Routing's player positions to attach a player identity to the candidate.
  See [ADR 0003](../adr/0003-attribution-service.md).
- **The Game Server decides *whether damage is actually applied***, and owns
  every rule that follows from that (health, respawn, scoring, match state).
  It is the only service with write authority over game state.
- **Distributed Processing schedules work; it does not own CV or
  localization logic.** Vision and Routing workers execute jobs dispatched to
  them — the scheduler doesn't know what's inside a job.
- **Phones never decide anything.** They capture and stream; the Gateway is
  the only service they talk to directly.
