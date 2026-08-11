# Recorder

**Status:** Proposed
**apps/ path:** `apps/recorder`

## Purpose

Stores gameplay data for uses beyond the live match.

## Responsibilities

- Replay
- Debugging
- Dataset generation
- Performance analysis
- Future ML training

## Relationship to datasets/

Recorder is the *producer* of raw and processed gameplay data; the
repository-level [`datasets/`](../development/monorepo-structure.md#datasets)
directory is where that data is organized for ML use (raw, processed,
annotations, training, validation splits). Recorder itself is a service;
`datasets/` is where its output ultimately lands for training purposes.

## Inputs / Outputs

- **Input:** game state updates and events from the [Game Server](game-server.md)
  (and optionally raw frames from [Vision](vision.md) for dataset purposes)
- **Output:** persisted match records for replay, debugging, and dataset
  pipelines

## Open questions

- Storage format and backend (flat files vs. a database vs. object storage).
- What granularity gets recorded by default vs. opt-in (full frame streams
  are expensive to store at scale).
