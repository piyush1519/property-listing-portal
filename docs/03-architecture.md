# Requirements, Architecture and Technology Setup
## Property Listing Portal

### 1. SRS Summary
The Property Listing Portal is a Spring Boot REST API backend with a lightweight
HTML/CSS/JS frontend, backed by a relational database (H2 for dev, MySQL for
deployment). It exposes CRUD operations on Property entities, enforces role-based
access (Admin/Agent/User), and supports search/filter and dashboard summary views.
The system is designed for demonstration of a full CI/CD lifecycle rather than
production-scale traffic.

### 2. Use Cases

| Use Case | Actor | Description |
|---|---|---|
| UC1 | Agent | Create property listing |
| UC2 | Agent, Admin, User | View property listings |
| UC3 | Agent, Admin | Update property listing |
| UC4 | User, Agent, Admin | Search/filter listings |
| UC5 | Agent, Admin | Change listing status |
| UC6 | Admin | View dashboard summary |

### 3. System Architecture (layered, textual)
Client (Browser)
| HTTP/JSON
v
Frontend (HTML/CSS/JS) -- served statically or separate --
| REST calls
v
Spring Boot Application
|-- Controller Layer (REST endpoints)
|-- Service Layer (business logic, role checks)
|-- Repository Layer (Spring Data JPA)
v
Database (H2 dev / MySQL production)

### 4. Component Architecture
- `PropertyController` — REST endpoints for CRUD + search
- `PropertyService` — business logic, status transition rules
- `PropertyRepository` — JPA repository interface
- `UserController` / `UserService` — role-based auth (simple, e.g. Spring Security with in-memory or DB-backed users for MVP)
- `DashboardController` — aggregate summary endpoint
- `Property` entity, `User` entity, `Role` enum

### 5. Database Design — ER Model (textual)
User

id (PK)
username
password (hashed)
role (ADMIN | AGENT | USER)

Property

id (PK)
title
description
location
property_type
price
bedrooms
area
owner_id (FK -> User.id)
status (DRAFT | ACTIVE | UNDER_NEGOTIATION | SOLD | RENTED)
created_date
updated_date

Relationship: One `User` (Agent) → Many `Property` listings (one-to-many).

### 6. REST API Specification (initial)

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | /api/properties | Create listing | AGENT |
| GET | /api/properties | List/search listings (query params: location, type, minPrice, maxPrice, bedrooms) | ALL |
| GET | /api/properties/{id} | View single listing | ALL |
| PUT | /api/properties/{id} | Update listing | AGENT, ADMIN |
| PATCH | /api/properties/{id}/status | Change status | AGENT, ADMIN |
| GET | /api/dashboard/summary | Aggregate counts | ADMIN |
| POST | /api/auth/login | Authenticate | ALL |

### 7. Technology Justification

| Choice | Reason |
|---|---|
| Spring Boot | Industry-standard Java framework, fast REST API scaffolding, strong Jenkins/Maven integration |
| H2 (dev) → MySQL (prod) | Zero-config fast dev loop; MySQL for realistic production-like deployment |
| Maven | Standard build tool, integrates directly with Jenkins pipeline stages |
| Selenium | Required by project brief; industry-standard browser automation for E2E testing |
| Docker | Enables consistent, portable deployment; required by project brief |
| Ansible | Agentless, YAML-based, simpler learning curve than Puppet for a single-environment academic setup |

### 8. Project Folder Structure
property-listing-portal/
├── backend/
│ ├── src/main/java/com/portal/
│ │ ├── controller/
│ │ ├── service/
│ │ ├── repository/
│ │ ├── entity/
│ │ └── PortalApplication.java
│ ├── src/main/resources/application.properties
│ ├── src/test/java/com/portal/
│ └── pom.xml
├── frontend/
│ ├── index.html
│ ├── css/
│ └── js/
├── database/
│ └── schema.sql
├── tests/
│ └── selenium/
├── docker/
│ └── Dockerfile
├── ansible/
│ ├── inventory.ini
│ └── playbook.yml
├── docs/
├── Jenkinsfile
├── README.md
└── .gitignore

