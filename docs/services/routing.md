# Routing

**Status:** In progress
**apps/ path:** `apps/routing`

## Purpose

Indoor player localization using wireless infrastructure instead of GPS,
which doesn't work reliably indoors. Routing answers *where is each player,
right now* — it does not interpret camera frames and does not decide hits.

## Responsibilities

- Ingesting RF signal data from router infrastructure: RSSI, SNR, CSI
- Fingerprinting against known arena layouts
- Kalman filtering / position smoothing over time
- Producing a per-player position estimate

## Research areas

- RSSI (Received Signal Strength Indicator)
- SNR (Signal-to-Noise Ratio)
- CSI (Channel State Information)
- Fingerprinting techniques for a fixed indoor arena
- Kalman filters and other smoothing approaches for noisy RF data

These are open research questions, not settled implementation choices — track
comparisons in [research/](../research/overview.md).

## Boundaries

Routing localization is used to **identify where players are and provide
spatial awareness**, not to determine whether a bullet hit. The output feeds
[Attribution](attribution.md), which is the service responsible for turning
"a hit happened somewhere" (from [Vision](vision.md)) plus "here's where
everyone is" (from Routing) into "player X was hit."

## Inputs / Outputs

- **Input:** RSSI/SNR/CSI samples from arena router infrastructure
- **Output:** per-player position estimate (with uncertainty), streamed
  continuously to [Attribution](attribution.md)

## Scheduling

Routing workers execute jobs dispatched by
[Distributed Processing](distributed-processing.md), same as Vision.
