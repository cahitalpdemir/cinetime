# CineTime Backend

Spring Boot REST API for the CineTime cinema ticket booking application.

## Stack

- Java 17
- Spring Boot 2.7.14
- PostgreSQL
- Spring Security and JWT
- Redis for refresh tokens
- Swagger/OpenAPI
- Maven

## Local Setup

1. Create a PostgreSQL database named `cinetime_db`.
2. Create a PostgreSQL login role named `db_user` with password `password`, or use your own credentials in `.env`.
3. Copy `.env.example` to `.env` and update local values.
4. Start Redis locally, or enter the shared Redis connection values in `.env`.
5. Run the application:

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8081`.

Swagger UI: `http://localhost:8081/swagger-ui.html`

## Tests

Tests use an in-memory H2 database and do not modify the local PostgreSQL database.

```bash
mvn test
```

## Frontend Handoff

The canonical endpoint and response contract is documented in:

- `docs/frontend-api-contract.md`

All successful responses use this envelope:

```json
{
  "object": {},
  "message": "Operation completed successfully",
  "httpStatus": "OK"
}
```

## Configuration

Real secrets must not be committed. Local defaults are development-only values.

| Variable | Purpose |
| --- | --- |
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | PostgreSQL user |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | JWT signing secret |
| `ADMIN_EMAIL` | Initial admin email |
| `ADMIN_PASSWORD` | Initial admin password |
| `REDIS_HOST` | Redis host |
| `REDIS_PORT` | Redis port |
| `REDIS_PASSWORD` | Redis password |
| `REDIS_SSL` | Enable Redis TLS |
| `MAIL_FROM` | Sender email address |
| `FRONTEND_URL` | Frontend base URL for password reset links |

## Git Hygiene

The following local files must stay outside Git:

- `.env`
- `.idea/`
- `target/`
- `log/`
- `.DS_Store`
