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

## Training

[`apps/vision/src/snapshoot_train.ipynb`](../../apps/vision/src/snapshoot_train.ipynb)
is the current (Colab-based, exploratory) fine-tuning pipeline: it pulls the
`sebnae/IndoorCrowd` dataset from the Hugging Face Hub, converts its RLE
masks to YOLO segmentation labels, and fine-tunes a `yolov8n-seg` checkpoint
on it. It is not run in CI and is not itself a tracked research experiment
yet — see [apps/vision/README.md](../../apps/vision/README.md#training-notebook)
for the step-by-step, and log the resulting model's accuracy/latency under
[research](../research/overview.md) once it's evaluated against the stock
model.

## Inputs / Outputs

- **Input:** compressed camera frames, trigger timestamp, player/session ID
  (via [Gateway](gateway.md))
- **Output:** hit candidate — bounding box / body region, confidence score,
  trigger timestamp — sent to [Attribution](attribution.md)

## Testing

Unit tests live in `apps/vision/tests/` and run the real segmentation model
against `apps/vision/assets/example.jpg` — exercising `Detector.process()`
(well-formed masks/boxes/confidences) and `Detector.is_hit()` (crosshair
intersection math), per the [testing strategy](../development/testing.md).

CI (`.github/workflows/ci.yaml`) runs this suite automatically whenever a PR
touches `apps/vision/**` or shared `libs/**` code, via `uv sync` + `pytest`.
Pretrained weights (`*.pt`, gitignored) are downloaded by ultralytics on
first run and cached across CI runs.

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
