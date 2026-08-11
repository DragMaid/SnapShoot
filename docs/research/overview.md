# Research

`research/` is where experiments and comparisons get written down, so model
and algorithm choices are traceable decisions rather than folklore. This
supports the "research-driven development" design principle: nothing in
[Vision](../services/vision.md) or [Routing](../services/routing.md) should
be chosen just because it was the first thing that worked.

## Structure

```
research/
    papers/
    experiments/
    benchmarks/
    notes/
```

- **papers/** — reference papers/write-ups relevant to CV or RF localization
  approaches under consideration.
- **experiments/** — dated, reproducible comparisons (e.g. YOLO vs. MediaPipe
  for detection latency/accuracy; RSSI-only vs. RSSI+CSI fingerprinting
  accuracy).
- **benchmarks/** — raw performance numbers (GPU throughput, inference
  latency, localization error) that back claims made in
  [ADRs](../adr/index.md) or [service pages](../services/vision.md).
- **notes/** — informal working notes that haven't graduated into a full
  experiment write-up yet.

## What belongs here vs. an ADR

An **ADR** records a decision and its consequences once made. A **research
entry** records the comparison that informed it — the data, the method, the
result. A significant model or algorithm choice should usually have both: the
research entry showing the comparison, and (if it's structural enough) an ADR
referencing it as justification.

## Immediate candidates

- Vision: YOLO vs. MediaPipe for player detection/tracking under arena
  conditions (lighting, occlusion, multi-player density).
- Routing: RSSI-only vs. RSSI+CSI fingerprinting accuracy for indoor
  position estimation, and Kalman filter tuning for position smoothing.
- Attribution: matching algorithm accuracy — nearest-position vs.
  probabilistic fusion — once both Vision and Routing produce real output to
  test against.
