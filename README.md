# Job Application Tracker

Track a job search in one workspace, with application forms, status tracking, and a Kanban view backed by a Java REST API.

**Stack:** Java 17, Spring Boot, AngularJS 1.x, PostgreSQL, Flyway, Docker Compose. The repository also includes an AWS ECR/ECS CI/CD workflow.

## Run the local demo

Install Docker and Docker Compose, clone this repository, then run:

```bash
docker compose up --build
```

- [Application UI](http://localhost:4200)
- [Swagger API documentation](http://localhost:8080/swagger-ui/index.html)

Use the seeded local account `admin` / `admin123`, or sign up. Create an application, edit its status, and inspect the Kanban view. These credentials are for local demonstration only. No hosted demo is currently linked.

## Architecture notes

- The AngularJS SPA calls the Spring Boot backend; PostgreSQL persists application data.
- Flyway applies versioned schema migrations when the backend starts.
- Docker Compose separates the database, API, and web server. Backend tests use an H2 database.

The demo password-reset flow returns its token directly instead of sending email, so it needs hardening before public deployment.

## Development and verification

```bash
cd backend
mvn test
```

In another terminal:

```bash
cd frontend
npm install
npm test
```

See [the development guide](README_DEV.md) for native setup, migration conventions, and troubleshooting.
