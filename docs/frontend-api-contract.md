# CineTime Frontend API Contract

Version: 1.1
Base URL: `http://localhost:8081`

This file is the canonical contract for the first frontend integration. Swagger, Postman and frontend services should use these paths and response shapes.

## Response Envelope

Successful and failed business responses use the same top-level shape:

```json
{
  "object": {},
  "message": "Operation completed successfully",
  "httpStatus": "OK"
}
```

`object` can be a DTO, a list, a page object or `null`.

Validation errors return `400 BAD_REQUEST`:

```json
{
  "object": ["field: validation message"],
  "message": "Validation failed",
  "httpStatus": "BAD_REQUEST"
}
```

Unexpected internal errors do not expose exception details.

## Authentication

Protected requests use:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

| Flow | Method and path | Auth | Success |
| --- | --- | --- | --- |
| Register | `POST /auth/register` | Public | `201 CREATED` |
| Login | `POST /auth/login` | Public | `200 OK` |
| Refresh token | `POST /auth/refresh-token` | Public | `200 OK` |
| Logout | `POST /auth/logout` | Bearer | `200 OK` |
| Current user | `GET /user/me` | Bearer | `200 OK` |

## Movies

Movie status is always a string enum:

```text
NOW_SHOWING
COMING_SOON
ARCHIVED
```

| Flow | Method and path | Auth | Success |
| --- | --- | --- | --- |
| List/search | `GET /api/movies?q=&page=0&size=10&sort=title&type=asc` | Public | `200 OK` |
| Detail by id | `GET /api/movies/{id}` | Public | `200 OK` |
| Detail by slug | `GET /api/movies/slug/{slug}` | Public | `200 OK` |
| Now showing | `GET /api/movies/now-showing` | Public | `200 OK` |
| Coming soon | `GET /api/movies/coming-soon` | Public | `200 OK` |
| Create | `POST /api/movies` | ADMIN/MANAGER | `201 CREATED` |
| Update | `PUT /api/movies/{id}` | ADMIN/MANAGER | `200 OK` |
| Delete | `DELETE /api/movies/{id}` | ADMIN/MANAGER | `200 OK` |

Paged movie responses use a compact page object:

```json
{
  "object": {
    "content": [],
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "last": true
  },
  "message": "Movies fetched successfully",
  "httpStatus": "OK"
}
```

The legacy `GET /api/movies/in-theaters` and `GET /api/movies/{id}/show-times` aliases remain available but are hidden from Swagger. New frontend code must use `/now-showing` and `/{id}/showtimes`.

## Cinemas and Halls

| Flow | Method and path | Auth | Success |
| --- | --- | --- | --- |
| Cinema list | `GET /cinemas` | Public | `200 OK` |
| Cinema detail | `GET /cinemas/{id}` | Public | `200 OK` |
| Create cinema | `POST /admin/cinemas` | ADMIN | `201 CREATED` |
| Create hall and seats | `POST /admin/halls` | ADMIN | `201 CREATED` |

Hall creation returns the generated id and seat count:

```json
{
  "object": {
    "id": 1,
    "name": "Hall 1",
    "cinemaId": 1,
    "capacity": 40,
    "createdSeatCount": 40
  },
  "message": "Hall saved successfully",
  "httpStatus": "CREATED"
}
```

## Showtimes and Seats

| Flow | Method and path | Auth | Success |
| --- | --- | --- | --- |
| Filter | `GET /showtimes?movieId=&hallId=&date=2026-07-20` | Public | `200 OK` |
| Seat availability | `GET /showtimes/{id}/seats` | Public | `200 OK` |
| Create | `POST /admin/showtimes` | ADMIN | `201 CREATED` |
| Cancel | `PATCH /admin/showtimes/{id}/cancel` | ADMIN | `200 OK` |

The current filter uses `hallId`, not `cinemaId`.

## Booking, Payment and Tickets

| Flow | Method and path | Auth | Success |
| --- | --- | --- | --- |
| Create booking | `POST /customer/bookings` | CUSTOMER | `201 CREATED` |
| My bookings | `GET /customer/bookings` | CUSTOMER | `200 OK` |
| Booking detail | `GET /customer/bookings/{id}` | CUSTOMER | `200 OK` |
| Cancel pending booking | `PATCH /customer/bookings/{id}/cancel` | CUSTOMER | `200 OK` |
| Mock payment | `POST /customer/bookings/{id}/payment` | CUSTOMER | `200 OK` |
| Booking tickets | `GET /customer/bookings/{id}/tickets` | CUSTOMER | `200 OK` |
| Ticket detail | `GET /customer/tickets/{ticketNumber}` | CUSTOMER | `200 OK` |

Booking request:

```json
{
  "showtimeId": 1,
  "seatIds": [1, 2]
}
```

The backend accepts at most 10 seats in one booking. Duplicate or previously booked seats return `400 BAD_REQUEST`.

## Browser Integration

- The default allowed origin is `http://localhost:3000`.
- Additional origins are configured with the comma-separated `CORS_ALLOWED_ORIGINS` environment value.
- Public catalog endpoints work without a token.
- `/admin/**` requires `ADMIN`, except the documented movie operations that also allow `MANAGER`.
- `/customer/**` requires `CUSTOMER`.
- Unknown origins are rejected during the browser preflight request.
