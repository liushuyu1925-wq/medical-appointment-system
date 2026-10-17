# Medical Appointment System

A full-stack course project for patient appointments and hospital administration, built with Vue 3, Spring Boot, and MySQL. The system includes patient-facing pages and administrative workflows, with appointment notifications connected to booking and schedule updates.

## Main capabilities

- Browse departments and doctors and manage appointments.
- Create, cancel, and reschedule appointments with schedule-capacity updates.
- Generate in-application notifications for appointment confirmation, cancellation, and rescheduling.
- Support visit reminders through the notification reminder task.
- Query notifications with pagination, unread counts, and read-status updates.
- Record notification operations for later inspection.
- Provide additional administration pages for users, schedules, medical records, prescriptions, medicines, reviews, and statistics.

These capabilities describe the project implementation. They do not imply deployment in a real healthcare organization or integration with external hospital systems.

## Appointment and notification flow

The appointment service checks the patient, doctor, schedule availability, capacity, and duplicate bookings before creating an appointment. A successful booking updates the schedule count and creates the corresponding notification.

Cancellation updates the appointment state, releases schedule capacity, and creates a cancellation notification. Rescheduling connects the appointment change to schedule handling and a rescheduling notification. The reminder task provides a separate entry point for visit reminders.

Notification operations include recipient-specific detail access, unread counts, read-state updates, and operation logs. The notification module provides application messages; external SMS or email delivery is not claimed here.

## Technology stack

| Layer | Technologies |
| --- | --- |
| Frontend | Vue 3, Vite, Vue Router, Element Plus, Axios, ECharts, and state-management packages |
| Backend | Java 17, Spring Boot 3.2.0, Maven Wrapper, and database-access services |
| Database | MySQL |
| Frontend checks | Vitest and Vite production build |
| Backend checks | Spring Boot test suite |

## Source layout

This repository contains one frontend/backend pair, an English setup guide, and a schema-only database initialization file.

```text
backend/springboot/
    pom.xml
    mvnw
    mvnw.cmd
    src/main/java/
    src/main/resources/
    src/test/java/
frontend/vue3/
    package.json
    package-lock.json
    vite.config.js
    src/
database/
    schema.sql
photo/
    doctor (1).png ... doctor (10).png
```

Runtime uploads, exported database records, private reports, build output, dependency directories, and duplicate source copies are excluded.

## Local setup

### Prerequisites

- Java 17.
- A Node.js release compatible with the project's Vite 6 dependency.
- MySQL and a local database configuration.
- The backend Maven Wrapper and the frontend package lock.

### Database

Clone the repository:

```powershell
git clone https://github.com/liushuyu1925-wq/medical-appointment-system.git
cd medical-appointment-system
```

Create a MySQL database named `hospital_management` with UTF-8 support. Import [database/schema.sql](database/schema.sql) using your MySQL client or database administration tool. This file contains 21 table definitions and no exported records. For example, inside the MySQL client:

```sql
CREATE DATABASE hospital_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_management;
SOURCE database/schema.sql;
```

Run the client from the repository root for the relative schema path above. The schema does not seed users, doctors, departments, or schedules; you must prepare your own demonstration data before testing those workflows. No shared account or login credentials are supplied.

### Backend configuration

The original machine-specific configuration is excluded. From `backend/springboot/`, copy the supplied template:

```powershell
Copy-Item src/main/resources/application.example.properties src/main/resources/application.properties
```

On Linux or macOS, use `cp` for the equivalent copy. The resulting `application.properties` is ignored by Git.

Set the following environment variables in the backend terminal, replacing every placeholder with your own local value:

```powershell
$env:DB_USERNAME = "YOUR_DATABASE_USER"
$env:DB_PASSWORD = "YOUR_DATABASE_PASSWORD"
$env:APP_DEFAULT_PASSWORD = "YOUR_LOCAL_DEFAULT_ACCOUNT_PASSWORD"
$env:DOCS_USERNAME = "YOUR_LOCAL_DOCS_USER"
$env:DOCS_PASSWORD = "YOUR_LOCAL_DOCS_PASSWORD"
$env:SECURITY_USERNAME = "YOUR_LOCAL_SECURITY_USER"
$env:SECURITY_PASSWORD = "YOUR_LOCAL_SECURITY_PASSWORD"
```

The default database URL points to `localhost:3306/hospital_management`. Set `DB_URL` to override it. On Linux or macOS, use the equivalent `export VARIABLE=value` syntax. Do not commit real configuration or credentials.

### Backend

From the project root:

```powershell
cd backend/springboot
.\mvnw.cmd spring-boot:run
```

On Linux or macOS, use `./mvnw spring-boot:run`.

The configured backend port is 1236. A working database connection is required for database-backed operations.

### Frontend

In another terminal, from the project root:

```powershell
cd frontend/vue3
npm ci
npm run dev
```

The Vite development server uses port 3000 and proxies `/api` requests to http://localhost:1236. Open http://localhost:3000.

The frontend uses Vite and `npm run dev`.

## Checks

Frontend, from `frontend/vue3/`:

```powershell
npm test
npm run build
```

Backend, from `backend/springboot/`:

```powershell
.\mvnw.cmd test
```

The backend context-load test requires the configuration, environment variables, and database described above. Additional local integration tests tied to private seeded accounts are excluded from this public copy. Frontend tests use their included fixtures. These commands document available checks; see the validation note below for what was checked for this release.

## Release validation

- Frontend: 3 test files and 9 tests passed.
- Frontend production build: passed with the included demonstration avatars.
- Backend: Maven compilation and packaging passed with tests skipped.
- Copied application sources were checked against the original files using SHA-256.
- The database export was reduced to table definitions; no exported records or original credentials are included.

Database initialization, login, and full appointment workflows were not rerun against a fresh database for this publication. Existing framework deprecation and bundle-size warnings are not a claim of a warning-free build.

## Scope and limitations

- This is a course-project prototype, not a production clinical system.
- Database setup, suitable demonstration accounts, and local configuration are required.
- Payment-related code does not establish a verified production payment integration.
- Personal contribution and team ownership are not assigned by this document.
- Personal records, real credentials, private reports, and machine-specific runtime files are excluded from this release.
