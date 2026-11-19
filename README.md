# PathFinder: Centralized Internship Opportunity Dissemination Platform

University of Kelaniya, Career Guidance Unit (CGU). Group 31.

## Parts

| Part | Folder | Port | Database | Owner |
|---|---|---|---|---|
| user-service (accounts, JWT, profiles, saved posts, emails) | `services/user-service` | 8081 | MongoDB `user_db` | Rivindu |
| notice-service (notices, flyers, job categories) | `services/notice-service` | 8082 | PostgreSQL `notice_db` (Supabase when hosted) | Yoosuf (notices), Hijaz (flyers, student pages) |
| frontend (React + Vite) | `frontend` | 5173 | none | all |

Test emails are caught by MailHog: http://localhost:8025 (no real email is sent in development).

## Run it locally

You need: Docker Desktop, Node.js 20+, Java 21 and an IDE (IntelliJ).

1. Start Docker Desktop and wait for "Engine running".
2. If you have MongoDB installed as a Windows service, stop it (`Stop-Service MongoDB`, run as Administrator). Otherwise the app can connect to a different MongoDB than Docker's.
3. From the repo root:
   ```powershell
   docker compose up -d --build
   ```
   This starts MongoDB, PostgreSQL, MailHog and user-service (http://localhost:8081).
4. Load the test data (it drops and re-creates the data each time):
   ```powershell
   Set-ExecutionPolicy -Scope Process Bypass -Force
   .\seed\seed.ps1
   ```
   (Mac/Linux: `bash seed/seed.sh`). Details: `seed/README.md`.
5. Frontend:
   ```powershell
   cd frontend
   npm install
   npm run dev
   ```
   Open http://localhost:5173.
6. notice-service: run it from the IDE (port 8082).

If you change user-service code, rebuild its container: `docker compose up -d --build user-service` (or stop the container and run it from the IDE; both cannot use port 8081 at the same time).

## Test accounts

Every account uses the password `ChangeMe123!`.

| Role | Email |
|---|---|
| Admin | `admin@example.com` |
| Staff | `staff1@example.com`, `staff2@example.com` |
| Student | `student01@example.com` ... `student10@example.com` |

## Tests

```powershell
cd services\user-service
.\mvnw.cmd test
```

The tests use the Docker MongoDB and the seed data, so run steps 1 to 4 first.

## Team rules

- Branches: `<name>/<task>` (for example `rivindu/login-pages`), one pull request per task.
- Review circle: Rivindu's pull requests are reviewed by Yoosuf, Yoosuf's by Hijaz, Hijaz's by Rivindu.
- Do not commit secrets. The values in `application.yml` and `docker-compose.yml` are for development only; use environment variables / `.env` (ignored by git) when deploying.
- IDs shared between services are plain text: job category = slug (`software-it`), notice = UUID, user = hex string.