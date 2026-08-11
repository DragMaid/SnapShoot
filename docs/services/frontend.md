# Frontend

**Status:** In progress
**apps/ path:** `apps/frontend`

## Purpose

The React application players and operators interact with.

## Responsibilities

- Player UI
- Room creation
- Match interface
- HUD
- Settings
- Game status

## Design constraint

The frontend should remain thin. It renders game state pushed from the
[Game Server](game-server.md) via the [Gateway](gateway.md) — it does not
compute hits, positions, or damage, and does not hold gameplay logic of its
own.

## Inputs / Outputs

- **Input:** game state, session/auth responses from the
  [Gateway](gateway.md)
- **Output:** user actions (room creation, settings, match controls) sent to
  the Gateway
