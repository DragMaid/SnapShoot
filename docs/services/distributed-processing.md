# Distributed Processing

**Status:** In progress
**apps/ path:** `apps/distributed-processing` (naming TBD)

## Purpose

Computation scheduling across the GPU compute services. Owns *where and when*
inference jobs run — not *what* those jobs do.

## Responsibilities

- Worker management
- Load balancing
- GPU scheduling
- Room assignment (mapping matches/arenas to compute capacity)
- Job dispatch

## Boundary

Workers schedule inference jobs rather than owning computer vision or
localization logic directly. [Vision](vision.md) and [Routing](routing.md)
own their own algorithms; this service only decides which worker runs a
given job and when, based on GPU availability and current load.

## Inputs / Outputs

- **Input:** job requests from Vision and Routing (or from the Gateway on
  their behalf), worker health/capacity signals
- **Output:** job assignments to specific GPU workers

## Open questions

- Scheduling algorithm (simple round-robin vs. load-aware vs.
  latency-aware).
- How room/arena assignment interacts with physical GPU server placement in
  a real arena deployment.
