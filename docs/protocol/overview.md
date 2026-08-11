# Protocol

This page covers protocol *concepts and ownership*, not endpoint reference.
Each FastAPI service generates its own OpenAPI schema for request/response
reference — see [Development → Getting Started](../development/getting-started.md)
for how to reach a running service's `/docs`. MkDocs is for the *why* behind
the protocol, not a copy of the *what*.

## Where protocol definitions live

Shared message schemas live in `libs/protocol`, not duplicated per service.
Any service that sends or receives a cross-service message (frame payloads,
trigger events, hit candidates, game state updates) depends on
`libs/protocol` rather than defining its own copy of the shape.

## Why a shared protocol library

[Vision](../services/vision.md), [Routing](../services/routing.md),
[Attribution](../services/attribution.md), and the
[Game Server](../services/game-server.md) all pass structured messages
between each other on the hot path (see
[Hit Detection Pipeline](../architecture/data-flow.md)). If each service
defined its own version of "hit candidate" or "trigger event," they would
drift independently and fail silently at the boundary. A single shared
definition means a schema change is a single-library change, and
incompatibilities show up as a build/type error rather than a runtime bug
discovered mid-match.

## Message categories (current thinking)

- **Client → Gateway:** frame upload, trigger event, IMU/orientation stream,
  session/auth messages
- **Gateway → Vision / Routing:** forwarded, timestamped, session-tagged
  versions of the above
- **Vision → Attribution:** hit candidate (bbox/pose region, confidence,
  trigger timestamp)
- **Routing → Attribution:** player position estimate (position, uncertainty,
  timestamp)
- **Attribution → Game Server:** attributed hit candidate (player identity,
  combined confidence, trigger timestamp)
- **Game Server → Gateway → Client:** game state update, hit confirmation,
  damage

This list will firm up into concrete schemas in `libs/protocol` as each
service moves from proposed to implemented; treat it as the current
direction, not a frozen contract.

## Versioning

Not yet decided. Once `libs/protocol` has real schemas, this page should
document how breaking changes are versioned and rolled out across services
that don't necessarily deploy in lockstep.
