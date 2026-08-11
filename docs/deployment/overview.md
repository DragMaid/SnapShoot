# Deployment

**Status:** Not yet implemented — this page records current direction.

## Scope

Infrastructure for running all services in `apps/` together: containerization,
orchestration, and CI/CD. Lives under `deployment/` at the repo root.

## Planned stack

- **Docker** — every service in `apps/` ships its own `Dockerfile`.
- **Docker Compose** — local multi-service development, so the full
  [hit detection pipeline](../architecture/data-flow.md) can run end-to-end
  on a dev machine.
- **Kubernetes** — target for arena/production deployment, where
  [Vision](../services/vision.md) and [Routing](../services/routing.md)
  workers need GPU scheduling and horizontal scaling under
  [Distributed Processing](../services/distributed-processing.md)'s
  direction.
- **CI/CD** — per-service pipelines (lint, unit tests, build/push image) plus
  repo-level pipelines for [integration/e2e/performance/stress tests](../development/testing.md).

## GPU considerations

Vision and Routing are the GPU-bound services. Deployment needs to account
for:

- GPU node pools distinct from CPU-only services (Gateway, Game Server,
  Frontend, Recorder)
- Scheduling coordination with [Distributed Processing](../services/distributed-processing.md)
  rather than relying on Kubernetes' default scheduler alone
- Per-arena capacity planning, since "room assignment" maps matches to
  physical or logical GPU capacity

## Open questions

- Single-cluster vs. per-arena cluster deployment.
- How `deployment/` config tracks environment differences (local, staging,
  arena/production).
- Secrets management for auth and session tokens at the
  [Gateway](../services/gateway.md).

This page should be filled in with real manifests/configs as
`deployment/` is built out — treat it as a placeholder for direction, not a
finished design.
