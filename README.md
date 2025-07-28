# Minigames Hub Documentation

> _(Work still in progress, project is not finished yet. Tests are missing because this project is being developed quickly to implement more functionality in less time. I do not recommend skipping tests in real development. Documentation is shorter and limited to text-only without sequence or other diagrams for the same reason.)_

## Description

**Minigames Hub** is a pet project with the main idea of creating a "hub" of online games that can be played by users, making a common website where many games are hosted in one place.

It is implemented in a **microservices-oriented architecture** to support horizontal scaling of individual system components.

For example, one game can be implemented as a stateless service that can be scaled horizontally, using other distributed systems like:
- NoSQL databases (e.g., MongoDB, Aerospike, Cassandra)
- Messaging brokers (e.g., Kafka, RabbitMQ)

Minigames Hub already has an **SSO implementation** using JWT token authentication, OAuth2, and more, allowing users to register/login/authenticate/authorize. Currently, it supports only browsers, but it can be extended to mobile/desktop/other clients with minimal changes to the Auth service.

## REST API Design & Security

> **Entry point for all REST API requests is the Gateway service.**

The **Gateway service** handles and secures all requests. It:
- Validates the access token provided by the client.
- Extracts the user ID and session ID from the token.
- Adds these as HTTP headers: `X-User-Id` and `X-Session-Id` to downstream service requests.
- Strips these headers from client requests to prevent spoofing.

This means:
- Downstream services **trust the Gateway**.
- Presence of `X-User-Id` and `X-Session-Id` is proof that the request is authenticated.

### REST API Types

Services typically expose three types of APIs:

1. **Public APIs**
    - Used by authenticated users.
    - May require additional authorities.
    - Prefix: `/public/api/`

2. **Admin APIs**
    - Used only by admin users.
    - Currently accessible through Swagger/Postman in development.
    - Admin users must be created manually.
    - Prefix: `/admin/api`

3. **Internal APIs**
    - Not exposed externally.
    - Used for cross-service communication.
    - No Gateway routes exist for them.
    - Prefix: `/internal/api`

JWT tokens include an **authorities list**. Downstream services may additionally:
1. Parse JWT to check roles/authorities.
2. Verify session status with the Auth service.
3. Perform custom validation via Auth service.

## Services Design

_Valid for 23.07.2025_

---

### 🛰️ Service Registry (Eureka)

**Description:**
- Maintains registry of all system services.
- Services register themselves at startup.
- Performs service health checks.

**Responsibilities:**
1. Health checks
2. Service instance storage
3. Supports load balancing via Discovery Client (e.g., in Gateway)

---

### 🌐 Gateway (Spring Gateway)

**Description:**
- Core service and entry point.
- Described in detail above.

**Responsibilities:**
1. Security (authentication, authorization, etc.)
2. Adds `X-User-Id` and `X-Session-Id` headers.
3. Routes configuration for downstream services.

---

### 🔐 Auth Service

**Description:**
- Manages all user authentication and session-related actions.

**Responsibilities:**
1. Session management
2. User management
3. Access/refresh token generation
4. Login
5. Registration
6. Token refresh
7. OAuth2 support

---

### 🎮 Game Clicker Service

**Description:**
- Implements logic for the Clicker game.

**Responsibilities:**
1. Game logic
2. User game data persistence

## How to start up project

### Dev-only limitations
There is limitation for now that not all configuration can be avoided with dev-only values, a.e. SMTP connection (email) and OAuth2 client registration for Google.

These limitations, unfortunately, do not make possible to use some parts of functionality such as registration/email verification/OAuth2 flow.

For dev-only, test user will be created to avoid registration flow. You can log in as test user without need to register.

### Start project

1. Make sure you have Docker installed (a.e. Docker Desktop for Windows) and enabled.
2. In root project dir, run:

```shell
docker-compose up --build
```

### What can be touched
_Valid for 28.07.2025_

1. Login/logout
2. Clicker game fully playable.
3. Sessions revoking in dashboard.

### How to login

Use test user's credentials:

```json
{
   "email": "test@test.com",
   "username": "test",
   "password": "test"
}
```

