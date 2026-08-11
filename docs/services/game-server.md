# Game Server

**Status:** Proposed
**apps/ path:** `apps/game`

## Purpose

The authoritative source of truth for gameplay. Every rule that determines
what actually happens in a match lives here — nowhere else in the system is
allowed to mutate game state.

## Responsibilities

- Health
- Damage
- Teams
- Respawn
- Weapons
- Cooldowns
- Match timers
- Scoring
- Rule validation

## Boundaries

[Vision](vision.md) detects *possible* hits. [Attribution](attribution.md)
resolves *who* was hit. The Game Server decides whether damage is actually
applied — checking cooldowns, team/friendly-fire rules, weapon state,
confidence thresholds, and match rules before mutating any player's health.

This is the enforcement point for [server-authoritative gameplay](../adr/0001-server-authoritative-gameplay.md):
nothing upstream of the Game Server has write authority over game state.

## Inputs / Outputs

- **Input:** attributed hit candidates from [Attribution](attribution.md),
  session/match control from [Gateway](gateway.md)
- **Output:** updated game state, pushed to [Gateway](gateway.md) → phones,
  and persisted via [Recorder](recorder.md)

## Open questions

- Match state persistence (in-memory + snapshot vs. backed by
  Postgres/Redis — see [Technology Stack](../architecture/overview.md)).
- How confidence thresholds from Attribution map to damage rules (hard
  cutoff vs. probabilistic damage).
