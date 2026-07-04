# CineTime Backend Technical Debt Status

Date: 2026-07-04
Scope: Backend hardening and production-oriented lifecycle completion.

## Closed Debt

| Area | Resolution |
| --- | --- |
| API contract | Canonical frontend contract documents response, auth and lifecycle behavior |
| Response model | Domain endpoints use the shared `ResponseMessage<T>` envelope |
| Pagination | Movie endpoints return compact `PageResponse<T>` objects |
| Validation and errors | Invalid input, conflicts and unexpected errors return controlled responses |
| Security and CORS | Role matrix and environment-driven browser origins are integration-tested |
| Database migrations | Flyway V1 baseline and V2 hardening migrations replace `ddl-auto=update` |
| Concurrent seats | Pessimistic showtime locking serializes competing seat reservations |
| Seat availability | Active booking seats are reported as booked by the public seat endpoint |
| Cancellation/refund | Pending cancellation and confirmed mock-refund lifecycle are implemented |
| Showtime cancellation | Active bookings are cancelled, completed payments refunded and tickets cancelled |
| Ticket lifecycle | Tickets support `ACTIVE`, `USED` and `CANCELLED` states |
| Signed QR | Ticket QR payloads are expiring HMAC-signed JWTs with admin verification/check-in |
| JPA boundary | `open-in-view` is disabled and service transactions own persistence access |
| Test isolation | Automated tests use H2 and do not modify the local PostgreSQL database |
| Repository hygiene | Secrets, logs, IDE metadata and build output remain outside Git |

## Remaining Registered Debt

None. The items in the previous debt register are implemented in this change set.

Future integrations such as a real payment provider, SMS delivery and production
observability are product roadmap items rather than unfinished MVP behavior.

## Merge Gate

Completed on 2026-07-04:

1. Java 17 Maven suite: **18 tests, 0 failures**.
2. Clean PostgreSQL migration: **Flyway V1 and V2 applied successfully**.
3. Schema validation: application started with **`ddl-auto=validate`**.
4. Newman regression: **32 requests and 29 assertions, 0 failures**.
5. CORS, role protection, refund, signed QR and duplicate-seat scenarios passed.

The isolated verification database was removed after the run. The developer's
local CineTime database was not modified by automated verification.
