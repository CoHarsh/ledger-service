# ledger-service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Running the application in dev mode
### You can run the application in dev mode using:

```
bash./mvnw quarkus:dev
``` 

### or setup -Dquarkus.profile=dev in your IDE.

-- - -

## About this project

Offline-first payment ledger service built with Quarkus, implementing an append-only event-sourced architecture with escrow buckets to prevent double spending and guarantee merchant settlement. Designed for secure syncing, invariant enforcement, and auditable financial transactions.

-- - -

# TODO — Offline First Payment Ledger

## Phase 1 — Ledger Core
- [X] Set up Quarkus project structure
- [X] Configure PostgreSQL database connection
- [X] Create ledger_events schema (Flyway migration)
- [X] Implement LedgerEvent entity
- [X] Build append-only LedgerService
- [X] Add invariant validation (prevent negative escrow)
- [X] Implement idempotency via unique event IDs
- [X] Expose REST API to append events
- [ ] Write unit tests for ledger invariants

## Phase 2 — Balance Projection
- [X] Create balance projection tables
- [ ] Build event replay → balance projection worker
- [ ] Implement real-time balance updates
- [X] Add API to query user escrow balance
- [ ] Add projection rebuild command
- [ ] Performance/load test projections

## Phase 3 — Escrow Logic
- [X] Implement preload funds flow
- [ ] Implement offline escrow debit flow
- [X] Enforce escrow limits per user
- [ ] Add escrow expiry rules

## Phase 4 — Sync & Ordering
- [ ] Implement event ordering logic
- [X] Add deduplication on sync
- [ ] Handle offline event queues
- [ ] Add conflict resolution rules

## Phase 5 — Merchant Settlement
- [X] Build merchant pending bucket logic
- [X] Implement settlement worker
- [ ] Add payout retry mechanism
- [X] Track settlement status

## Phase 6 — Security
- [ ] Implement event signature verification
- [ ] Add device key management
- [ ] Validate tamper-proof event flow
- [ ] Add audit logging

## Phase 7 — Reliability
- [ ] Add background job monitoring
- [ ] Implement failure recovery flows
- [ ] Add observability (metrics + logs)
- [ ] Stress test ledger consistency

## Phase 8 — Production Readiness
- [ ] Dockerize services
- [ ] Add CI/CD pipeline
- [ ] Write integration tests
- [ ] Deployment documentation



API Endpoints:
- `POST /events` - Append a new ledger event (preload, debit, settlement)
- `GET wallet/balance/{userId}` - Get current escrow balance for a user
- `POST wallet/load` - Preload funds into escrow

