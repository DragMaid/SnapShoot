# Attribution

**Status:** Proposed
**apps/ path:** `apps/attribution`

## Purpose

Resolves a Vision hit candidate to a specific player identity, using
Routing's RF-based position estimates. This is the fusion point between
"something was hit" and "who was hit" — it exists as its own service rather
than being folded into either Vision or Routing. See
[ADR 0003](../adr/0003-attribution-service.md) for the reasoning.

## Why it's needed

[Vision](vision.md) can tell you a crosshair intersects *a* body in the
frame, but in a multi-player arena it has no reliable way to know *whose*
body that is — bodies look similar, players occlude each other, and Vision
has no access to RF/player-ID data. [Routing](routing.md) knows where every
player's phone is via RSSI/CSI, but has no notion of aim, line of sight, or
what's in a camera frame. Attribution combines both:

1. Takes a hit candidate from Vision (frame-space body position + confidence
   + trigger timestamp).
2. Takes concurrent per-player position estimates from Routing.
3. Projects/matches the hit candidate's real-world position against the
   nearest player position estimate.
4. Emits a hit candidate with an attached player identity and a combined
   confidence score.

## Responsibilities

- Time-aligning Vision hit candidates with Routing position estimates
  (matching on trigger timestamp)
- Spatial matching / nearest-neighbor resolution between a detected body and
  known player positions
- Producing a combined confidence score (detection confidence × localization
  confidence)
- Rejecting or flagging low-confidence matches rather than guessing

## Inputs / Outputs

- **Input:** hit candidates from [Vision](vision.md), player position
  estimates from [Routing](routing.md)
- **Output:** hit candidate + player identity + confidence, sent to the
  [Game Server](game-server.md)

## Boundaries

Attribution decides *who*, not *whether damage applies*. The
[Game Server](game-server.md) still owns damage validation — Attribution
just makes sure it's evaluating the right player.

## Open questions

- Matching algorithm: simple nearest-position vs. probabilistic fusion of
  frame-space geometry with RF uncertainty.
- Behavior when no player position confidently matches a hit candidate
  (reject vs. pass through with low confidence and let the Game Server
  decide the threshold).
- Whether Attribution needs its own state, or is purely a stateless fusion
  step over two incoming streams.
