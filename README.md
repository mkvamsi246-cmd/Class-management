# Class Management (Smart Classroom)

A full-stack classroom and room-allocation management system with:
- **Backend:** Spring Boot + Spring Data JPA + PostgreSQL
- **Frontend:** Angular (standalone components)

This project supports login, faculty management, room management, timetable operations, room requests, notifications, dashboard metrics, audit viewing, and Excel-based bulk uploads.

---

## 1) Repository Layout (Pin-to-Pin)

```text
Class-management/
├── smartclassroom/                          # Spring Boot backend
│   ├── src/main/java/com/smartclassroom/
│   │   ├── config/                          # Security configuration
│   │   ├── controller/                      # REST APIs
│   │   ├── dto/                             # Request/response DTOs
│   │   ├── entity/                          # JPA entities
│   │   ├── repository/                      # Spring Data repositories
│   │   └── service/                         # Business logic
│   └── src/main/resources/application.properties
└── AngularProjects/smart-classroom-ui/      # Angular frontend
    ├── src/app/pages/                       # Page components
    ├── src/app/services/                    # API services
    ├── src/app/guards/                      # Route guards
    └── src/environments/environment.ts
```

---

## 2) Technology Stack

### Backend
- Java 17
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Security (currently configured with `permitAll`)
- PostgreSQL
- Apache POI (Excel uploads)

### Frontend
- Angular 21
- RxJS
- TypeScript 5.9
- Vitest (unit testing via `ng test`)

---

## 3) Prerequisites

Install the following before running:
- **Java 17**
- **Maven** (or use included `./mvnw`)
- **PostgreSQL**
- **Node.js + npm** (npm 10+ recommended)
- **Angular CLI** (optional globally; local CLI is already in project)

---

## 4) Backend Setup (Spring Boot)

### 4.1 Configure Database
File: `/home/runner/work/Class-management/Class-management/smartclassroom/src/main/resources/application.properties`

Key properties used:
- `spring.datasource.url=jdbc:postgresql://localhost:5432/smart_classroom_db`
- `spring.datasource.username=postgres`
- `spring.datasource.password` (set your local PostgreSQL password)
- `spring.jpa.hibernate.ddl-auto=update`
- `server.port=8080`

Create database:
```sql
CREATE DATABASE smart_classroom_db;
```

### 4.2 Run Backend
From:
`/home/runner/work/Class-management/Class-management/smartclassroom`

```bash
./mvnw spring-boot:run
```

Backend base URL:
`http://localhost:8080`

### 4.3 Backend Test Command
```bash
./mvnw test
```

---

## 5) Frontend Setup (Angular)

From:
`/home/runner/work/Class-management/Class-management/AngularProjects/smart-classroom-ui`

### 5.1 Install dependencies
```bash
npm install
```

### 5.2 Verify API Base URL
File: `/home/runner/work/Class-management/Class-management/AngularProjects/smart-classroom-ui/src/environments/environment.ts`

Configured as:
- `apiUrl: 'http://localhost:8080'`

### 5.3 Run Frontend
```bash
npm start
```

Frontend URL:
`http://localhost:4200`

### 5.4 Frontend Test/Build
```bash
npm test
npm run build
```

---

## 6) Frontend Route Map

Defined in `src/app/app.routes.ts`:
- `/` → Landing page
- `/login` → Login
- `/admin-dashboard` → Admin dashboard (guarded)
- `/faculty-dashboard` → Faculty dashboard (guarded)
- `/faculty-management` → Faculty management (guarded)
- `/room-management` → Room management (guarded)
- `/timetable` → Timetable (guarded)
- `/room-request` → Room request creation/approval page (guarded)
- `/my-requests` → Faculty request history (guarded)
- `/change-password` → Password update (guarded)

Auth guard checks localStorage key: `user`.

---

## 7) Backend API Map (Pin-to-Pin)

### Auth
Base: `/api/auth`
- `POST /login`
- `POST /change-password`

### Dashboard
Base: `/api/dashboard`
- `GET /`

### Faculty Management (Admin)
Base: `/api/admin`
- `GET /faculty`
- `POST /faculty`
- `PUT /faculty/{id}`
- `DELETE /faculty/{id}`

### Rooms
Base: `/api/rooms`
- `GET /`
- `GET /available`
- `POST /`
- `PUT /{id}`
- `DELETE /{id}`

### Timetable
Base: `/api/timetable`
- `GET /`
- `POST /`
- `PUT /{id}`
- `GET /faculty/{facultyId}`
- `DELETE /{id}`

### Room Requests
Base: `/api/requests`
- `GET /`
- `POST /`
- `PUT /{id}/approve`
- `PUT /{id}/reject`
- `GET /faculty/{facultyId}`

### Subjects
Base: `/api/subjects`
- `GET /`
- `POST /`

### Notifications
Base: `/api/notifications`
- `GET /{userId}`

### Audit
Base: `/api/audit`
- `GET /`

### Excel Uploads
Base: `/api/upload`
- `POST /faculty`
- `POST /rooms`
- `POST /timetable`

---

## 8) Main Domain Entities

Located under `smartclassroom/src/main/java/com/smartclassroom/entity`:
- `User`
- `Faculty`
- `Room`
- `Timetable`
- `RoomRequest`
- `Notification`
- `AuditLog`
- `Subject`
- `Material`

---

## 9) Typical Local Run Order

1. Start PostgreSQL.
2. Create `smart_classroom_db` if not present.
3. Set DB credentials in backend `application.properties`.
4. Start backend (`./mvnw spring-boot:run`).
5. Start frontend (`npm install && npm start`).
6. Open `http://localhost:4200` and use login.

---

## 10) Common Troubleshooting

### Backend fails to connect DB
- Check PostgreSQL is running.
- Check DB name/username/password in `application.properties`.
- Ensure port 5432 is available.

### Frontend cannot call API
- Confirm backend runs on `http://localhost:8080`.
- Confirm `environment.ts` points to same URL.
- Check browser console/network tab for endpoint errors.

### CORS issues
- Most controllers allow `*` or `http://localhost:4200`.
- If you change frontend port, update CORS config in controllers.

---

## 11) Notes

- Security config currently permits all requests (`auth.anyRequest().permitAll()`).
- JPA auto-update is enabled (`ddl-auto=update`) for local development.
- Keep secrets/passwords out of committed files; use secure local configuration in real deployments.
