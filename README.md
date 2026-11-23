# Project Run Instructions

## Configuration

Both ways of running the project read their settings from a `.env` file in the project root. Git ignores `.env`, so your real values never get committed. Start by copying the template:

```
cp .env.example .env
```

The template, `.env.example`, looks like this:

```dotenv
# Copy this file to .env and set your own values:
#   cp .env.example .env
# .env is ignored by Git. Never commit real passwords.

# Used by Spring Boot when running locally. Docker Compose sets its own
# host and port for the backend container.
SERVER_PORT=8080
DB_HOST=localhost
DB_PORT=5432

# Used by both Docker Compose and Spring Boot.
DB_NAME=pmsdb
DB_USER=pms_user
DB_PASSWORD=change_me
```

Change the passwords before you use it for anything beyond local testing.

| Variable      | Used by       | Default if unset | Purpose                                             |
|---------------|---------------|------------------|-----------------------------------------------------|
| `SERVER_PORT` | Local backend | `8080`           | Port for the REST API                               |
| `DB_HOST`     | Local backend | `localhost`      | PostgreSQL host (Docker sets `postgres`)            |
| `DB_PORT`     | Local backend | `5432`           | PostgreSQL port (Docker sets `5432`)                |
| `DB_NAME`     | Both          | `pmsdb`          | Database name                                       |
| `DB_USER`     | Both          | None, required   | Database user. In Docker, it also owns the database |
| `DB_PASSWORD` | Both          | None, required   | Database user's password                            |

---

## Option 1: Run with Docker (recommended)

The only prerequisite is Docker (with Compose). After creating `.env` as described in [Configuration](#configuration), start the stack from the project root:

```
docker compose up -d --build
```

This starts three containers:

* **postgres** — PostgreSQL 18 with a persistent volume (published on `127.0.0.1:5434`, so it doesn't clash with a local PostgreSQL and only your machine can reach it)
* **backend** — the Spring Boot REST API on `http://localhost:8080`
* **frontend** — the React app served by nginx on `http://localhost:3000` (proxies `/api` to the backend)

Open **http://localhost:3000** and log in.

To stop:

```
docker compose down
```

Add `-v` to also delete the database volume (fresh start).

PostgreSQL only applies the credentials from `.env` when the database volume is first created. To change them later, reset the volume with `docker compose down -v`, which deletes the data.

### Troubleshooting

**The backend stops with `password authentication failed for user "..."`**

The database volume was created with different credentials from the ones now in `.env`, usually because `.env` changed after the first start. Reset the volume, which deletes the data, and start again:

```
docker compose down -v
docker compose up -d --build
```

---

## Option 2: Run locally (development)

### Prerequisites

* PostgreSQL (running and accessible), with an empty database named as in `DB_NAME`
* Java Development Kit (JDK) 25
* Node.js 20+ (for the frontend)

### Configuration

Create `.env` as described in [Configuration](#configuration), and set `DB_USER` and `DB_PASSWORD` to an account on your PostgreSQL server.

No local PostgreSQL? Start only the database container with `docker compose up -d postgres`, and set `DB_PORT=5434` in `.env`.

Spring Boot doesn't read `.env` on its own. In Git Bash, or another POSIX shell, load it into the terminal you'll start the backend from:

```
set -a; source .env; set +a
```

### Backend

From the project root:

```
./gradlew bootRun
```

The REST API starts on `http://localhost:8080`.

### Frontend

In a second terminal:

```
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173** — the Vite dev server proxies `/api` requests to the backend on port 8080.

---

## Database Initialization

On startup, the application will automatically:

* Create the database **if it does not already exist**
* Apply the required schema using JPA/Hibernate
* Create a default **admin user**

### Default Admin Credentials

* **Username:** `admin`
* **Password:** `admin`

You can use these credentials to log in after the application starts.

> Note: passwords are stored with BCrypt. Users created before the BCrypt switch (other than `admin`, which is migrated automatically) need their password re-set.
