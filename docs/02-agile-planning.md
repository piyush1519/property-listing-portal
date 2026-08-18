# Agile Planning and DevOps Workflow
## Property Listing Portal

### 1. Epics
| Epic ID | Epic |
|---|---|
| E1 | Property Listing Management (Create/View/Update) |
| E2 | Search and Filter |
| E3 | Role-Based Status Workflow |
| E4 | Dashboard |
| E5 | CI/CD Pipeline |
| E6 | Testing (Selenium/Unit) |
| E7 | Containerization (Docker) |
| E8 | Configuration Management (Ansible) |

### 2. Product Backlog — User Stories

| Story ID | Epic | User Story | Priority |
|---|---|---|---|
| US-01 | E1 | As an Agent, I want to create a property listing so buyers can see it | High |
| US-02 | E1 | As a User, I want to view all property listings so I can browse available properties | High |
| US-03 | E1 | As an Agent, I want to update my property listing so information stays accurate | High |
| US-04 | E2 | As a User, I want to search/filter listings by location, type, price, bedrooms so I can find relevant properties | High |
| US-05 | E3 | As an Agent, I want to change a listing's status (Draft→Active) so buyers know it's available | High |
| US-06 | E3 | As an Admin, I want to approve status changes (e.g. mark Sold/Rented) so the workflow is controlled | Medium |
| US-07 | E4 | As an Admin, I want a dashboard showing listing counts by status/type so I can monitor activity | Medium |
| US-08 | E5 | As a Developer, I want a Jenkins pipeline that builds on every push so integration errors are caught early | High |
| US-09 | E6 | As a Developer, I want Selenium tests to gate deployment so broken features never reach users | High |
| US-10 | E7 | As a Developer, I want the app containerized so it runs identically across environments | High |
| US-11 | E8 | As a Developer, I want Ansible to provision the environment so deployment is repeatable and idempotent | Medium |

### 3. Acceptance Criteria (sample — full set grows per feature week)

**US-01 — Create Property Listing**
- Given valid property data (title, location, type, price, bedrooms, area), when Agent submits the form, then a new listing is persisted with status = Draft
- Given a required field is missing, when Agent submits, then a validation error is shown and no record is created
- Created listing has Created Date auto-populated

**US-04 — Search/Filter**
- Given listings exist, when User filters by location, then only matching listings are returned
- Given no listings match filter criteria, then an empty state is shown, not an error

**US-09 — Selenium Gating**
- Given a Selenium test fails in the pipeline, when the pipeline runs, then the Docker build/deploy stage does not execute
- Given all Selenium tests pass, then the pipeline proceeds to Docker build

### 4. Definition of Done
A story is "Done" when:
- [ ] Code implemented and merged to `develop` via reviewed PR
- [ ] Acceptance criteria verified manually or via automated test
- [ ] No linter/build errors
- [ ] Relevant unit/Selenium test added (where applicable)
- [ ] Documentation updated (API doc / README as relevant)
- [ ] Evidence captured for weekly submission

### 5. 15-Week Plan (Week → Focus → Key Stories)

| Week | Focus | Stories/Deliverables |
|---|---|---|
| 1 | Problem Definition | — |
| 2 | Agile Planning | Backlog, DoD, workflow (this week) |
| 3 | Architecture & Setup | SRS, ERD, API spec, local env |
| 4 | Git/GitHub Init | Repo, branches, skeleton app |
| 5 | Feature Dev — Create | US-01 |
| 6 | MVP Completion | US-02, US-03, US-04, US-05, US-07 + merge conflict demo |
| 7 | Jenkins CI | US-08 |
| 8 | Pipeline as Code + Deploy | Jenkinsfile, Tomcat/Nginx deploy |
| 9 | Selenium Test Design | US-09 (local) |
| 10 | Continuous Testing in Jenkins | US-09 (Jenkins-integrated), fail/fix demo |
| 11 | Docker Image | US-10 |
| 12 | Jenkins-Docker CD | US-10 (pipeline-integrated) |
| 13 | Ansible Config Mgmt | US-11 |
| 14 | Provisioning & Reliability | US-11 (idempotency, rollback) |
| 15 | Final Release & Viva | Full docs, demo, report |

### 6. Task Board Structure (Kanban columns)
`Backlog → In Progress → In Review (PR) → Testing → Done`

Each story card tracks: Story ID, assignee (self), branch name, PR link, status.

### 7. Git Workflow

**Branches:**
- `main` — always deployable/stable
- `develop` — integration branch
- `feature/<story-id>-<short-name>` — e.g. `feature/US-01-create-listing`
- `bugfix/<short-name>`
- `release/<version>`

**Flow:**
1. Branch off `develop` → `feature/US-XX-name`
2. Implement + commit with conventional messages (`feat:`, `fix:`, `docs:`, `chore:`, `test:`)
3. Push feature branch → open PR into `develop`
4. Self-review (simulate reviewer role) — check DoD checklist
5. Merge PR → delete feature branch
6. `develop` merges to `main` at release points (tagged, e.g. `v1.0.0`)

**PR Process:**
- PR description must reference Story ID
- PR must show what changed and how it was tested
- At least one review comment thread simulated per PR (self-review acceptable for solo project, but must be documented)

**CI/CD Process (from Week 7 onward):**
- Every push to `feature/*` or `develop` triggers Jenkins build
- `develop` → `main` merge triggers full pipeline: build → test → Selenium → Docker → deploy

### 8. DevOps Lifecycle Diagram (textual)

```
Backlog (GitHub Issues)
    ↓
Development (feature branch, local)
    ↓
Git (commit, push)
    ↓
GitHub PR → Review → Merge (develop)
    ↓
Jenkins CI (checkout → build → unit test)
    ↓
Selenium Testing (quality gate)
    ↓
Docker Build & Tag
    ↓
Docker Registry (push image)
    ↓
Ansible Provisioning (configure target env)
    ↓
Deployment (container running)
    ↓
Health Check / Monitoring
    ↓
Feedback → new Backlog items
```