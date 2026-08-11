# Monorepo Structure

```
project/
├── apps/
├── libs/
├── tests/
├── datasets/
├── deployment/
├── docs/
├── tools/
└── research/
```

## apps/

Deployable services. Each is self-contained: its own source, tests, and
Dockerfile. Python services carry their own `pyproject.toml`.

```
apps/
    vision/
        src/
        tests/
        pyproject.toml
        Dockerfile
```

Current/planned services: [gateway](../services/gateway.md),
[vision](../services/vision.md), [routing](../services/routing.md),
[attribution](../services/attribution.md), [game](../services/game-server.md),
[distributed-processing](../services/distributed-processing.md),
[recorder](../services/recorder.md), [frontend](../services/frontend.md),
and an eventual admin app.

Each service has its own unit tests, scoped to that service's local
functionality — see [Testing Strategy](testing.md).

## libs/

Shared libraries used by every service:

- `protocol` — shared message schemas, see [Protocol](../protocol/overview.md)
- `common` — shared utilities
- `config` — configuration loading/validation
- `logging` — structured logging setup

## tests/

Repository-level testing — verifies interactions *between* services. Unit
tests stay inside each service's own directory.

```
tests/
    integration/
    e2e/
    performance/
    stress/
    fixtures/
    assets/
```

See [Testing Strategy](testing.md) for what belongs at each level.

## datasets/

Machine learning datasets, kept separate from `tests/`.

```
datasets/
    raw/
    processed/
    annotations/
    training/
    validation/
```

This is where [Recorder](../services/recorder.md) output and any
hand-annotated data end up once processed for training.

## tools/

Standalone developer utilities: dataset converters, annotation tools,
benchmarking, calibration, ONNX export, TensorRT export.

## deployment/

Infrastructure: Docker, Docker Compose, Kubernetes, CI/CD, infra scripts. See
[Deployment](../deployment/overview.md).

## research/

Research and experimentation — comparisons of localization methods, CV
models, and performance benchmarks, kept as a written record rather than
tribal knowledge.

```
research/
    papers/
    experiments/
    benchmarks/
    notes/
```

See [Research](../research/overview.md).
