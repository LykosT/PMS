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
MYSQL_HOST=localhost
MYSQL_PORT=3306

# Used by both Docker Compose and Spring Boot.
MYSQL_NAME=pmsdb
MYSQL_USER=pms_user
MYSQL_PASSWORD=change_me
MYSQL_ROOT_PASSWORD=change_me_too
```

Change the passwords before you use it for anything beyond local testing.

| Variable | Used by | Default if unset | Purpose |
| --- | --- | --- | --- |
| `SERVER_PORT` | Local backend | `8080` | Port for the REST API |
| `MYSQL_HOST` | Local backend | `localhost` | MySQL host (Docker sets `mysql`) |
| `MYSQL_PORT` | Local backend | `3306` | MySQL port (Docker sets `3306`) |
| `MYSQL_NAME` | Both | `pmsdb` | Database name |
| `MYSQL_USER` | Both | None, required | Database user |
| `MYSQL_PASSWORD` | Both | None, required | Database user's password |
| `MYSQL_ROOT_PASSWORD` | Docker only | None, required | MySQL root password in the container |

---

## Option 1: Run with Docker (recommended)

The only prerequisite is Docker (with Compose). After creating `.env` as described in [Configuration](#configuration), start the stack from the project root:

```
docker compose up -d --build
```

This starts three containers:

* **mysql** — MySQL 8.4 with a persistent volume (published on host port `3307` to avoid clashing with a local MySQL)
* **backend** — the Spring Boot REST API on `http://localhost:8080`
* **frontend** — the React app served by nginx on `http://localhost:3000` (proxies `/api` to the backend)

Open **http://localhost:3000** and log in.

To stop:

```
docker compose down
```

Add `-v` to also delete the database volume (fresh start).

MySQL only applies the credentials from `.env` when the database volume is first created. To change them later, reset the volume with `docker compose down -v`, which deletes the data.

---

## Option 2: Run locally (development)

### Prerequisites

* MySQL Server (running and accessible)
* Java Development Kit (JDK) 25
* Node.js 20+ (for the frontend)

### Configuration

Create `.env` as described in [Configuration](#configuration), and set `MYSQL_USER` and `MYSQL_PASSWORD` to an account on your MySQL server.

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
