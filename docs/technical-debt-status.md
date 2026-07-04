# CineTime Backend Technical Debt Status

Date: 2026-07-04
Scope: Frontend handoff stabilization, including browser security boundaries.

## Completed In This Change Set

| Area | Result |
| --- | --- |
| API contract | Added `docs/frontend-api-contract.md` as the canonical frontend contract |
| Response model | Movie endpoints now use `ResponseMessage<T>` like the other domains |
| Pagination | Movie pages expose a compact `PageResponse<T>` instead of Spring internals |
| HTTP status | Register, cinema, hall and movie create responses now return real `201 CREATED` |
| Movie status | JSON is explicitly serialized as `NOW_SHOWING`, `COMING_SOON` or `ARCHIVED` |
| Hall handoff | Existing hall response was verified to include id, capacity and generated seat count |
| Validation | Movie, cinema, hall and booking request rules were strengthened |
| Error handling | Invalid JSON, constraint, conflict, email and unexpected errors have controlled responses |
| Information leakage | Unexpected exception messages are logged but no longer returned to clients |
| JPA boundary | `open-in-view` is disabled and service transaction boundaries were added |
| Redis logs | Redis repository scanning is disabled because repositories are JPA repositories |
| Environment | Database, Redis, mail and frontend settings support environment values and safe local defaults |
| Logging | SQL logging defaults to off and stray `System.out` statements were removed |
| Email | Password-change timestamp now uses the application formatter instead of a Hibernate formatter |
| Test isolation | H2 test configuration and focused controller, enum, validation and hall tests were added |
| Repository hygiene | `.env.example` was added; `.env`, logs and `.DS_Store` stay outside Git |
| Authorization matrix | Public catalog, admin and customer routes are explicitly separated |
| CORS | Wildcard origins were replaced with environment-driven frontend origins |
| Security regression | Public, protected and CORS preflight behavior is covered by integration tests |

## Frontend Handoff Status

The backend is ready for local frontend integration. The default frontend origin is
`http://localhost:3000`, the API contract is documented, and the secure `prod` profile
is active by default.

## Remaining Priority Debt

| Priority | Item | Reason |
| --- | --- | --- |
| P1 | Database migrations with Flyway/Liquibase | `ddl-auto=update` is still used for local development |
| P1 | Concurrent seat reservation protection | Sequential duplicate checks exist, but race conditions need DB locking/constraints |
| P1 | Cancel/refund lifecycle | Confirmed booking refund rules need product decisions |
| P1 | Full automated API regression | Existing Postman flow should be run again after the response contract update |
| P2 | Real signed QR payload | Ticket QR currently mirrors the ticket number |

## Push Gate

Before merging:

1. Run `mvn test` with Java 17.
2. Start the application and verify that Redis repository and open-in-view warnings are absent.
3. Re-import/update Postman requests for the movie response envelope.
4. Run the clean-database MVP regression.
5. Verify CORS from the configured frontend origin.
