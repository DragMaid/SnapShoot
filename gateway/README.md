# Spring Boot Gateway
The single entry point for every phone. No client service talks to phones directly, and phones talk to nothing else directly.

## Tech Stack
- Java 25
- Spring Boot 4.1
- Spring Data JPA (Hibernate)
- Spring Security
- Springdoc OpenAPI (Swagger UI)
- Liquibase (database migrations)
- PostgreSQL 17 (via Docker)

## Prerequisites
- JDK 25
- Docker + Docker Compose
- Maven 3.9+ (optional — the bundled `./mvnw` wrapper is used below)

## Docker Compose
All dependencies are already packaged into this *docker compose*:
- `database` — PostgreSQL 17 database
- `gateway_service` — The Spring Boot Gateway Service

## Quick Start

### 1. Configure the environment
```bash
cp .env.template .env
```
Edit `.env` and set the PostgreSQL credentials:
```dotenv
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
POSTGRES_DB=gateway
POSTGRES_PORT=5432
POSTGRES_HOST=localhost
POSTGRES_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
```

### 2. Start Gateway Service
```bash
docker compose up --build -d
```

### 4. Verify
- Health check: http://localhost:8080/api/health → `Gateway is running successfully!`
- Swagger UI: http://localhost:8080/api/swagger-ui.html

Alternatively, run both the database and the app together:
```bash
docker compose up --build
```

## Configuration
- `.env` — source of truth for DB credentials (gitignored). Drives `docker-compose.yaml` and the runtime environment.
- `src/main/resources/application.yaml` — runtime settings (port `8080`, context-path `/api`, datasource, JPA, Liquibase).
- `liquibase.properties` (auto-generated) — Liquibase database migration config, auto-generated from `.env` by `scripts/new-migration.sh` (gitignored).

## Database Migrations (Liquibase)
- Master changelog: `src/main/resources/db/changelog/db.changelog-master.xml` — includes everything in `db/changelog/migrations/`.
- **Create a migration** (number auto-generated, name supplied by you) through a wrapper of Liquibase shell script:
  ```bash
  ./scripts/new-migration.sh create_users
  ```
  → produces `src/main/resources/db/changelog/migrations/001_create_users.sql`
- **Apply pending migrations**:
  ```bash
  ./mvnw liquibase:update
  ```
- **Naming convention**: `NNN_description.sql` (3-digit zero-padded prefix, formatted SQL changelog).
- The script compiles first so Liquibase's Hibernate scan sees your entities. Run it (or `./mvnw compile`) before `liquibase:update` after a `mvn clean`, so the latest migrations are present in `target/classes`.
