# Vision

**Status:** In progress
**apps/ path:** `apps/vision`

## Purpose

Computer vision processing for hit detection. Determines whether a player's
crosshair geometrically intersects another player's body at the moment of
trigger pull — nothing more. It does not know player identity and does not
decide whether damage is applied.

## Responsibilities

- Human detection
- Human tracking
- Pose estimation and/or segmentation
- Hit (crosshair/body intersection) detection
- Confidence scoring
- GPU inference

## Direction

Inference runs on centralized GPU servers, not on-device. Phones stream
compressed camera frames to the server; the phone does no CV work itself.

Under evaluation:

- **YOLO** — for detection/tracking
- **MediaPipe** — for pose estimation
- Pose estimation vs. instance segmentation as the basis for body-region
  matching against the crosshair

Model choice, accuracy, and latency tradeoffs should be tracked as
[research experiments](../research/overview.md), not decided informally.

## Inputs / Outputs

- **Input:** compressed camera frames, trigger timestamp, player/session ID
  (via [Gateway](gateway.md))
- **Output:** hit candidate — bounding box / body region, confidence score,
  trigger timestamp — sent to [Attribution](attribution.md)

## Boundaries

Vision explicitly does **not**:

- Decide player identity (that's [Attribution](attribution.md), using
  [Routing](routing.md) position data)
- Decide whether damage is applied (that's the [Game Server](game-server.md))
- Run on the phone

## Scheduling

Vision workers execute inference jobs dispatched by
[Distributed Processing](distributed-processing.md); Vision owns the CV
logic, the scheduler owns placement and load balancing.
