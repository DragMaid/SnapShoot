# Networking

## What phones send

Every phone maintains a connection to the Gateway (WebSocket for low-latency
streams, likely paired with REST for session/auth setup). Phones transmit:

- Camera frames (compressed)
- Trigger events
- IMU data
- Orientation
- Player ID
- Timestamps

## What the server returns

- Hit confirmation
- Damage
- Updated game state

The server owns every gameplay decision. The phone renders whatever it is
told; it does not locally compute hits, damage, or authoritative position.

## Transport shape (current thinking)

| Data | Direction | Likely transport | Notes |
|---|---|---|---|
| Camera frames | Phone → Gateway → Vision | WebSocket (binary) | Compressed, timestamped, player-tagged |
| Trigger events | Phone → Gateway → Vision/Attribution | WebSocket | Must carry precise trigger timestamp |
| IMU / orientation | Phone → Gateway → Vision/Routing | WebSocket | Streamed continuously, not just on trigger |
| RSSI/CSI samples | Router infra → Routing | Depends on hardware capture path | Not phone-originated |
| Game state | Game Server → Gateway → Phone | WebSocket | Pushed on every state change |
| Auth / session setup | Phone → Gateway | REST | One-time / infrequent |

This table reflects current direction, not a finalized protocol. Message
schemas are tracked in `libs/protocol` and documented in
[Protocol](../protocol/overview.md); this page describes the transport shape
around them, not the wire format itself.

## Why the Gateway is the only thing phones talk to

Centralizing all phone traffic through the Gateway keeps auth, rate limiting,
and session state in one place, and means Vision, Routing, Attribution, and
the Game Server never need to deal with untrusted client connections
directly — they only ever receive data the Gateway has already authenticated
and attributed to a session.
