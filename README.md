# Project Run Instructions

## Option 1: Run with Docker (recommended)

The only prerequisite is Docker (with Compose). From the project root:

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

Credentials can be overridden with the `MYSQL_NAME`, `MYSQL_USER`, `MYSQL_PASSWORD` and `MYSQL_ROOT_PASSWORD` environment variables (see `docker-compose.yml` for the defaults).

---

## Option 2: Run locally (development)

### Prerequisites

* MySQL Server (running and accessible)
* Java Development Kit (JDK) 25
* Node.js 20+ (for the frontend)

### Configuration

The backend uses **external configuration** for environment-specific values. Set these environment variables (each has a default in `application-dev.properties`):

* `SERVER_PORT`
* `MYSQL_HOST`
* `MYSQL_PORT`
* `MYSQL_NAME`
* `MYSQL_USER`
* `MYSQL_PASSWORD`

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
