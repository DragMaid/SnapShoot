# RLPUBG

RLPUBG is a distributed, real-time laser tag / AR shooting platform. A player's
smartphone acts as both gun and camera: it captures the scene and sensor data,
streams that data to centralized servers, and renders whatever game state the
servers hand back. It never decides anything on its own.

## Core idea

The system is **server authoritative**. Clients never determine whether a hit
occurred — phones capture camera frames, trigger events, and sensor data, send
them upstream, and render the result. Every gameplay decision — was it a hit,
who got hit, how much damage, is the match over — is made by servers.

## Core goals

- Real-time hit detection using computer vision
- Indoor player localization using wireless infrastructure (no GPS indoors)
- Server-authoritative gameplay
- Modular microservice architecture
- GPU-accelerated inference
- Distributed processing across many concurrent players
- Replayability and future analytics

## Where to start

- [Architecture Overview](architecture/overview.md) — the big picture and how services fit together
- [Hit Detection Pipeline](architecture/data-flow.md) — how a trigger pull becomes a damage event
- [Services](services/gateway.md) — responsibilities of each deployable service
- [Monorepo Structure](development/monorepo-structure.md) — how the repository is organized
- [Roadmap](roadmap.md) — what's built, what's designed, what's still open

## Documentation scope

This MkDocs site covers architecture, design rationale, protocol concepts, and
developer guides. Per-service HTTP/WebSocket endpoint reference is generated
separately from each FastAPI service's OpenAPI schema and is not duplicated
here.
