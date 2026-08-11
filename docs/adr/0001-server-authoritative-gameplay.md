# ADR 0001: Server-Authoritative Gameplay

**Status:** Accepted
**Date:** 2026-08-04

## Context

RLPUBG runs on player-owned phones acting as both gun and camera. Phones are
inherently untrusted: they can be modified, their sensors can be spoofed, and
their local computation cannot be verified. A laser-tag-style game where
clients self-report hits is trivially cheatable, and even without malice,
phone-to-phone clock/sensor drift would make client-reported hits
inconsistent across players.

## Decision

The server is the sole authority over gameplay outcomes. Phones capture
camera frames, trigger events, IMU data, orientation, and timestamps, and
send them upstream. They never compute whether a hit occurred, never apply
damage locally, and never mutate game state. All of that happens in
[Vision](../services/vision.md) → [Attribution](../services/attribution.md)
→ [Game Server](../services/game-server.md), and the result is pushed back
down to the phone to render.

## Consequences

- Cheating by modifying client code cannot affect match outcomes, only the
  data a cheater's own phone reports (which the server can still validate or
  discount).
- All gameplay logic must run server-side, which means the server pipeline
  (Vision → Attribution → Game Server) must be fast enough to feel real-time
  — this is a hard latency constraint on every service in the
  [hit detection pipeline](../architecture/data-flow.md).
- Phones can be genuinely thin clients, simplifying the mobile app and
  keeping it portable across devices.
- The server becomes a single point of failure for gameplay; server
  availability and scaling ([Distributed Processing](../services/distributed-processing.md))
  become critical-path concerns rather than nice-to-haves.

## Alternatives considered

- **Client-authoritative with server validation** — phone computes the hit,
  server spot-checks. Rejected: still exploitable between spot-checks, and
  spot-checking requires most of the same server-side pipeline anyway,
  without the cheat-resistance benefit.
- **Peer-to-peer consensus** — phones vote on outcomes. Rejected: far more
  complex, worse latency, and still manipulable by a majority of compromised
  clients in a small match.
