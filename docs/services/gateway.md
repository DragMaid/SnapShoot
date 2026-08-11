# Gateway

**Status:** Proposed
**apps/ path:** `apps/gateway`

## Purpose

The single entry point for every phone. No client service talks to phones
directly, and phones talk to nothing else directly.

## Responsibilities

- Authentication
- Session management
- Routing requests to the correct backend service
- WebSocket connection lifecycle
- Frame forwarding (to [Vision](vision.md))
- Trigger forwarding (to Vision / [Attribution](attribution.md))
- Rate limiting

## Why it exists

Without a gateway, every backend service would need to authenticate
connections, manage per-phone session state, and defend itself against
malformed or hostile client input. Centralizing that here means Vision,
Routing, Attribution, and the Game Server can treat all inbound data as
already authenticated and already attributed to a known session.

## Interfaces

- **Inbound (phone → Gateway):** WebSocket for frames/triggers/IMU, REST for
  auth and session setup. See [Networking](../architecture/networking.md).
- **Outbound (Gateway → backend):** forwards frames to Vision, RF-adjacent
  session context to Routing, and relays Game Server state pushes back to
  phones.

## Open questions

- Exact session/auth mechanism (token-based, per-match, per-arena).
- How reconnection and mid-match rejoin is handled.
- Backpressure strategy when Vision/GPU capacity is saturated.
