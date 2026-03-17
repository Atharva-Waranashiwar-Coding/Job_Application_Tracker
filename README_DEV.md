# Development Guide

This project includes a Spring Boot backend (Java), an AngularJS (1.x) frontend, and a PostgreSQL database.

---

## ✅ Prerequisites

Before you start, make sure you have these installed:

- **Java 17** (JDK)
- **Maven**
- **Node.js 18+**
- **Docker** (for the local dev stack and CI)

> 💡 If you don’t already have Docker running, start it before running the compose stack.

---

## 🧰 Local Development (Docker)

This is the easiest way to run the full stack (DB + backend + frontend) locally.

### 1) Start the full stack

```bash
# Run in the project root
docker-compose up --build
```

- Backend API: `http://localhost:8080`
- Frontend UI: `http://localhost:4200`

> ✅ Flyway migrations run automatically when the backend starts.

### 2) Verify the backend

Open your browser and visit:

- Swagger UI (API docs): `http://localhost:8080/swagger-ui/index.html`

### 3) Login (seeded user)

The project seeds an admin user during migration:

- **Username:** `admin`
- **Password:** `admin123`

> 💡 You can also create a new account via the "Sign up" link on the login page.

### 4) Forgot password (demo)

If you need to reset a password, you can use the "Forgot password" link on the login page.

In this demo, the reset token is returned immediately (instead of being emailed).

### 5) Stop the stack

```bash
docker-compose down
```

---

## 🧩 Running the backend without Docker (optional)

If you prefer to run just the backend using your local PostgreSQL:

1. Start PostgreSQL locally
2. Create a database named `job_tracker`
3. Set these env vars (or use `application.yml` defaults):

```bash
export JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/job_tracker
export JDBC_DATABASE_USERNAME=job_tracker
export JDBC_DATABASE_PASSWORD=password
```

4. Run the backend:

```bash
cd backend
mvn spring-boot:run
```

---

## 🧪 Running tests

### ✅ Backend tests

```bash
cd backend
mvn test
```

> The tests run against an in-memory H2 database (no Docker dependency).

### ✅ Frontend tests

```bash
cd frontend
npm install
npm test
```

---

## 📦 Building Docker images locally

```bash
docker build -t job-tracker-backend:latest -f backend/Dockerfile backend
docker build -t job-tracker-frontend:latest -f frontend/Dockerfile frontend
```

---

## 🗃️ Adding a new database column (Flyway migration)

1. Add a new migration script:
   - `backend/src/main/resources/db/migration/V3__add_column_to_applications.sql`

2. Update related Java models:
   - Entity: `backend/src/main/java/com/example/jobtracker/entity/ApplicationEntity.java`
   - DTO: `backend/src/main/java/com/example/jobtracker/dto/ApplicationDto.java`

3. Update service/controller if needed.

---

## 🔧 Where to change things

### Add a new field to a job application
- Add schema change via Flyway: `backend/src/main/resources/db/migration/V3__…sql`
- Update entity: `backend/src/main/java/com/example/jobtracker/entity/ApplicationEntity.java`
- Update DTO(s): `backend/src/main/java/com/example/jobtracker/dto/ApplicationDto.java`
- Update service/controller: `backend/src/main/java/com/example/jobtracker/service/ApplicationService.java` and `backend/src/main/java/com/example/jobtracker/controller/ApplicationController.java`
- Update frontend form + views: `frontend/src/app/views/application-form.html` and `frontend/src/app/controllers/application-form.controller.js`

### Add a new API endpoint
- Add controller method in: `backend/src/main/java/com/example/jobtracker/controller/*Controller.java`
- Add service logic in: `backend/src/main/java/com/example/jobtracker/service/*Service.java`

### Change the status list (Kanban / UI)
- Update statuses list in:
  - `frontend/src/app/controllers/application-form.controller.js`
  - `frontend/src/app/controllers/kanban.controller.js`

---

## 📌 Troubleshooting

### Docker compose fails (port in use)
If `localhost:8080` or `4200` is in use, stop the conflicting process or change the ports in `docker-compose.yml`.

### Swagger endpoint not reachable
Make sure the backend is running and you used the correct port:
`http://localhost:8080/swagger-ui/index.html`

### Tests failing due to missing dependencies
Run `mvn -U test` in `backend` to force dependency refresh.

---

## 🚀 Next steps
If you'd like, I can also add:
- ✅ JWT login + refresh tokens
- ✅ Drag/drop Kanban
- ✅ E2E tests (Cypress)
- ✅ Full IaC for AWS ECS/RDS using Terraform or CloudFormation
