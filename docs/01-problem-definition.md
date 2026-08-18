# Problem Definition and Scope
## Property Listing Portal — CI/CD Pipeline Academic Project

### 1. Problem Statement
Property listings in small real-estate agencies and independent agent networks are
often managed through informal channels — spreadsheets, WhatsApp messages, or paper
records. This leads to duplicate listings, outdated status information, and no
central searchable record. There is a need for a lightweight, role-based web
application that lets agents create and manage listings, lets admins oversee status
workflow, and lets end users search and view available properties.

This project addresses that need while serving as a vehicle to demonstrate a
complete, real-world DevOps lifecycle: version control, continuous integration,
automated testing, containerization, and automated provisioning.

### 2. Project Motivation
- Demonstrate an end-to-end DevOps pipeline (Git → Jenkins → Test → Docker → Ansible)
  on a realistic, non-trivial application rather than a toy "Hello World" service.
- Build academic competency in CI/CD tooling used in industry (Jenkins, Docker, Selenium, Ansible).
- Produce a reproducible, evidence-backed project suitable for college evaluation and viva.

### 3. Target Users
| User Type | Description |
|---|---|
| Admin | Oversees the platform, manages users, approves/rejects listing status changes |
| Agent | Creates, updates, and manages their own property listings |
| User (Public/Buyer) | Searches and views property listings |

### 4. Stakeholders
- Project developer/student (owner, implementer)
- Academic evaluators / project guide (assessors)
- Hypothetical real-estate agency (end business owner, for realism)
- Hypothetical agents and property seekers (end users)

### 5. Existing Pain Points
- Listings scattered across spreadsheets/chat apps — no single source of truth
- No status tracking (available / under negotiation / sold / rented)
- No search or filter capability for buyers
- No audit trail of who created/updated a listing or when
- No automated deployment process — manual updates are error-prone

### 6. Business Requirements
- BR1: Provide a centralized system for managing property listings
- BR2: Support distinct roles with different permissions
- BR3: Provide search/filter capability for end users
- BR4: Provide a dashboard summarizing listing activity
- BR5: Be deployable via an automated, repeatable pipeline

### 7. Functional Requirements
- FR1: Create a property listing (Agent)
- FR2: View property listings (all roles)
- FR3: Update a property listing (Agent/Admin)
- FR4: Search/filter listings by location, type, price, bedrooms (all roles)
- FR5: Role-based status workflow (e.g., Draft → Active → Under Negotiation → Sold/Rented)
- FR6: Summary dashboard (counts by status, type, agent)

### 8. Non-Functional Requirements
- NFR1: Application must run reliably on a local/dev environment via Docker
- NFR2: Codebase must be version-controlled with a clear branching strategy
- NFR3: All builds must pass through CI before deployment
- NFR4: Automated tests must gate deployment (failing tests block release)
- NFR5: Deployment must be reproducible via Ansible provisioning
- NFR6: Response time for listing search should be under 2 seconds on local dev environment

### 9. Constraints
- Must be completed within 15 weeks, one week at a time
- Must use the fixed technology stack: Java/Spring Boot, MySQL/H2, Jenkins, Docker, Selenium, Ansible
- Single-developer project — no team-based Git workflows requiring multiple contributors (will be simulated where needed, e.g., PR review)
- Academic project — scoped for demonstrability, not production scale

### 10. Measurable Success Criteria
- SC1: All 6 MVP features functional and demonstrable
- SC2: Jenkins pipeline runs end-to-end (checkout → build → test → docker → deploy) with a passing status
- SC3: At least 5 Selenium tests integrated into the pipeline, with at least one demonstrated pipeline failure and recovery
- SC4: Docker image builds, runs, and is versioned/tagged
- SC5: Ansible playbook provisions environment idempotently (second run produces no/minimal changes)
- SC6: Full documentation set completed (SRS, architecture, API, test, DevOps docs)

### 11. MVP Scope (In Scope)
- Create / View / Update / Search property listings
- Role-based status workflow (Admin, Agent, User)
- Summary dashboard
- REST API backend (Spring Boot)
- Simple frontend (HTML/CSS/JS or minimal templating)
- MySQL/H2 persistence
- Full CI/CD pipeline: Git → Jenkins → Selenium → Docker → Ansible

### 12. Out of Scope
- Payment processing / transactions
- Real-time chat between agents and buyers
- Image/media upload and storage for listings
- Multi-tenant/multi-agency support
- Mobile application
- Production-grade security hardening (OAuth, rate limiting, etc.) — basic auth/roles only
- Kubernetes orchestration (Docker standalone/Compose is sufficient for this scope)