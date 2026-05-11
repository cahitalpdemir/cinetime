# Cinetime

Spring Boot REST API for an online movie ticketing platform.

**Stack:** Java 17 · Spring Boot 2.7 · PostgreSQL · JWT · Swagger

---

## PostgreSQL Setup

**Requirements:** Java 17, Maven, PostgreSQL running on port `5432`.

### 1. Create the user (pgAdmin)

```
Servers
 └── Login/Group Roles
      ├── postgres
      └── db_user   ← CREATE this user here
```

> In **Definition** tab → Password: `password`
> In **Privileges** tab → enable **Can login**, **Create databases**, **Superuser**

### 2. Create the database (pgAdmin)

```
Servers
 └── Databases
      └── cinetime_db   ← CREATE this database here (Owner: db_user)
```

### 3. Run the project

Run `CinetimeApplication.java` in your IDE, or:

```bash
mvn spring-boot:run
```

### 4. Open in browser

- App: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui.html

---

## Troubleshooting

- **`password authentication failed`** → user/password mismatch, re-check step 1.
- **`Connection refused` on 5432** → PostgreSQL not running, or another process (often a Docker container) is using the port.
