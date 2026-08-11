# Getting Started

## Documentation site

This site is built with [MkDocs](https://www.mkdocs.org/) and the
[Material](https://squidfunk.github.io/mkdocs-material/) theme.

```bash
pip install -r docs/requirements.txt
mkdocs serve
```

Then open `http://127.0.0.1:8000`.

## Per-service endpoint reference

MkDocs covers architecture, protocol concepts, and design rationale — it
does not duplicate HTTP/WebSocket endpoint reference. Each FastAPI service
exposes its own OpenAPI docs at `/docs` when running locally, which is the
source of truth for request/response shapes for that service.

## Repository layout

See [Monorepo Structure](monorepo-structure.md) for what lives where.

## Running a service

Each service under `apps/` is self-contained with its own `pyproject.toml`
and `Dockerfile`. As services move from proposed to implemented, this page
should gain concrete run instructions per service (local dev server,
required environment variables, GPU requirements for
[Vision](../services/vision.md)).

## Contributing docs

- Architecture/design pages go under `docs/architecture/`.
- A new or changed service gets its own page under `docs/services/`.
- A structural decision (new service, protocol change, technology choice)
  gets an [ADR](../adr/index.md), not just a mention in another page.
