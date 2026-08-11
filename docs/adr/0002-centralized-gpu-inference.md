# ADR 0002: Centralized GPU Inference

**Status:** Accepted
**Date:** 2026-08-04

## Context

Hit detection depends on real-time computer vision — detection, tracking,
pose estimation/segmentation — which is computationally expensive. Phone
hardware varies wildly in GPU/NPU capability, and on-device inference would
tie game fairness and latency to whatever phone a player happens to own.

## Decision

CV inference for [Vision](../services/vision.md) (and any other heavy ML
work) runs on centralized GPU servers, not on the phone. Phones stream
compressed camera frames to the server via the
[Gateway](../services/gateway.md); [Distributed Processing](../services/distributed-processing.md)
schedules inference jobs across available GPU workers.

## Consequences

- Inference quality and latency are consistent across all players,
  regardless of phone hardware — directly supports
  [server-authoritative gameplay](0001-server-authoritative-gameplay.md)
  being fair.
- Network bandwidth and latency between phone and server become a hard
  constraint; frame compression and streaming efficiency matter a lot.
- The arena needs real GPU infrastructure and a scheduling layer
  ([Distributed Processing](../services/distributed-processing.md)) that can
  scale with concurrent player count — this is now infrastructure the game
  depends on, not an optional accelerator.
- Model iteration (swapping YOLO variants, MediaPipe versions, etc.) happens
  server-side and can be rolled out without touching the client.

## Alternatives considered

- **On-device inference** — rejected: inconsistent across phone hardware,
  harder to update models, and conflicts with keeping clients thin and
  non-authoritative.
- **Hybrid (on-device pre-filter, server confirm)** — deferred, not rejected
  outright; may be revisited once latency data from centralized inference is
  available, but adds client-side complexity and a second code path to keep
  in sync with server models.
