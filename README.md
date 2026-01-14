# Project Run Instructions

## Prerequisites

Before running the application, make sure you have the following installed:

* MySQL Server (running and accessible)
---

## Configuration

The application uses **external configuration** for environment-specific values such as server port and database credentials.

### Option 1: Using Environment Variables

Set the following environment variables according to your environment:

* `SERVER_PORT`
* `MYSQL_HOST`
* `MYSQL_PORT`
* `MYSQL_NAME`
* `MYSQL_USER`
* `MYSQL_PASSWORD`


### Option 2: Configure Directly in `application.properties`

You may also configure values **directly inside `application-dev.properties`** if preferred.

---

## Running the Application

To start the application using command prompt, navigate to the project root directory and run:

```
gradlew clean bootRun
```
To start the application using Git Bash or PowerShell , navigate to the project root directory and run:

```
./gradlew clean bootRun
```
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

---

## Stopping the Application

To stop the application, press:

```text
CTRL + C
```

in the terminal where the application is running.

---
