<!--
  Keep the title short and imperative, e.g. "vision: fix mask projection off-by-one".
  Prefix it with the primary service/dir it touches when that's not obvious from context.
-->

## Description

<!-- What does this PR do, and why? Link a tracking issue if one exists. -->

## Affected service(s)

<!-- Check every apps/ directory (or libs/) this PR actually changes. -->

- [ ] `apps/vision`
- [ ] `apps/routing`
- [ ] `apps/gateway`
- [ ] `apps/attribution`
- [ ] `apps/game-server`
- [ ] `apps/distributed-processing`
- [ ] `apps/recorder`
- [ ] `apps/frontend`
- [ ] `libs/` (shared — impacts every service above)
- [ ] Other (docs, CI, tooling, deployment — describe below)

## Type of change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change (existing behavior/API/message schema changes)
- [ ] Refactor / cleanup (no behavior change)
- [ ] Docs / research notes
- [ ] CI / tooling / deployment

## Related docs / ADR

<!--
  Link the relevant docs/services/<service>.md page, an ADR under docs/adr/,
  or open one via docs/adr/template.md if this PR introduces a decision that
  isn't recorded anywhere yet. "N/A" is fine for small changes.
-->

## Screenshots / demo

<!--
  For visible or measurable output changes: debug overlays, detection
  results, UI, dashboards, before/after comparisons, etc. Delete this
  section if there's nothing visual to show.
-->

## Testing done

<!-- What did you run, and what did it show? Paste relevant output. -->

- [ ] Added/updated unit tests in the affected service's `tests/`
- [ ] `uv run pytest` (or the service's equivalent) passes locally
- [ ] Verified manually — describe how:

## Checklist

- [ ] I've read [CONTRIBUTING](../docs/development/getting-started.md) (or
      the relevant service doc) before opening this PR
- [ ] Docs updated if this changes behavior, a service boundary, or a
      message schema
- [ ] CI is green
