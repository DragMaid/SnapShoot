# Testing Strategy

Testing is split by scope: local correctness lives with the service; anything
that spans a service boundary lives at the repo level.

## Unit tests

Location: inside each service, e.g. `apps/vision/tests/`.

Scope: local functionality only — a single detector, a single tracker, a
single localization algorithm. No network calls to other services.

Examples: detector output shape, tracker ID continuity, a localization
algorithm's math in isolation.

## Integration tests

Location: `tests/integration/` at the repo root.

Scope: verify communication *between* services — that the message one
service sends is the message the next one can actually consume. Examples
follow the [hit detection pipeline](../architecture/data-flow.md):

- Gateway → Vision
- Vision → Attribution
- Routing → Attribution
- Attribution → Game Server
- Game Server → Frontend

## End-to-end tests

Location: `tests/e2e/`.

Scope: entire gameplay scenarios, phone-simulated input through to game state
output — a full pass through every service in the pipeline.

## Performance tests

Location: `tests/performance/`.

Scope: GPU throughput, latency, FPS — is [Vision](../services/vision.md)
keeping up with the frame rate the pipeline needs.

## Stress tests

Location: `tests/stress/`.

Scope: many simultaneous players, large numbers of concurrent connections,
GPU saturation — does the system degrade gracefully or fall over.

## Shared fixtures and assets

`tests/fixtures/` and `tests/assets/` hold data shared across integration,
e2e, performance, and stress tests (sample frames, recorded RF traces,
synthetic match scenarios) so it isn't duplicated per test suite.
