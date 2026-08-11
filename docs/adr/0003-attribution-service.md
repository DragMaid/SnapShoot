# ADR 0003: Attribution as a Separate Service

**Status:** Proposed
**Date:** 2026-08-04

## Context

Determining whether a player was hit requires two independent signals:
whether a crosshair intersects a body in frame ([Vision](../services/vision.md)),
and where each player physically is, via RF signals
([Routing](../services/routing.md)). Neither service can resolve player
identity on its own — Vision has no access to per-player RF data, and
Routing has no access to camera frames or aim geometry. Something has to
fuse a Vision hit candidate with Routing's position estimates to answer
*which player got hit*.

The question this ADR settles: should that fusion logic live inside
Routing (since it's the service with player-position data), inside Vision,
or as its own service.

## Decision

Introduce a dedicated **Attribution** service (`apps/attribution`) that
consumes hit candidates from Vision and player position estimates from
Routing, and resolves player identity with a combined confidence score,
before handing off to the [Game Server](../services/game-server.md).

## Consequences

- [Routing](../services/routing.md) stays scoped to localization only —
  RSSI/CSI ingestion, fingerprinting, smoothing — and doesn't need to know
  anything about Vision's output format or hit-candidate semantics.
- [Vision](../services/vision.md) stays scoped to frame-space detection and
  never needs RF data.
- Attribution becomes an explicit, testable fusion boundary: its
  input/output contract (hit candidate + position estimates in, attributed
  hit candidate out) is easy to unit-test independently of both upstream
  services, and its matching algorithm can evolve without touching Vision or
  Routing.
- One more service to deploy, monitor, and keep within the
  [hit detection pipeline's](../architecture/data-flow.md) latency budget —
  it sits directly on the real-time critical path between Vision/Routing and
  the Game Server.
- Both Vision and Routing must independently reach Attribution in time to
  match on a shared trigger timestamp; Attribution needs a clear policy for
  what happens when one input is late or missing (see open questions on the
  [Attribution service page](../services/attribution.md)).

## Alternatives considered

- **Fold into Routing** — Routing already owns RSSI/CSI/player-position
  logic, so identity resolution could be an extension of it. Rejected for
  now: it would couple Routing to Vision's hit-candidate schema and blur
  Routing's single responsibility (localization) with a second one
  (identity resolution), making both harder to test and iterate on
  independently.
- **Fold into Vision** — rejected: Vision has no natural access to RF data
  and centralized-GPU CV inference shouldn't also own RF fusion logic.
- **Fold into the Game Server** — rejected: would make the Game Server
  responsible for time-aligning and spatially matching two upstream
  streams in addition to its existing job of rule/damage validation,
  overloading the one service that must stay simple and auditable as the
  final authority.
