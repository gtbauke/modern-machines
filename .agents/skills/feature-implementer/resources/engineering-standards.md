# Engineering Standards & Implementation Guidelines

This guide defines the engineering standards, architecture patterns, and commit conventions that the **Senior Software Engineer** agent must adhere to during feature implementation.

---

## 1. Rust Backend Standards (`packages/backend`)

### 1.1 Language & Edition
- **Edition**: Rust 2024.
- **Async Runtime**: Tokio (`tokio = "1"`, full features).
- **Web Framework**: Axum 0.8.
- **Database Access**: SQLx 0.9 with PostgreSQL driver.

### 1.2 Module Organization
Modules live in `packages/backend/src/modules/<feature>/` and should follow a clean layered architecture:
```text
modules/<feature>/
├── mod.rs          # Re-exports and router entrypoint
├── models.rs       # Domain structs, database entities, SQLx FromRow / Type enums
├── dto.rs          # Request / Response schemas with serde & validation
├── error.rs        # Domain-specific thiserror enums mapping into AppError
├── repository.rs   # SQLx queries and transactional database interactions
├── service.rs      # Business logic, state machine validation, calculations
└── handlers.rs     # Axum route handler functions (HTTP extraction & responses)
```

### 1.3 Error Handling & Domain Errors
- Define domain errors using `thiserror`:
  ```rust
  #[derive(Debug, thiserror::Error)]
  pub enum TaskError {
      #[error("Task not found with id {0}")]
      NotFound(uuid::Uuid),
      #[error("Invalid state transition from {from:?} to {to:?}")]
      InvalidStateTransition { from: TaskStatus, to: TaskStatus },
      #[error("Payload exceeds maximum size limit")]
      PayloadTooLarge,
  }
  ```
- Implement `From<DomainError> for AppError` to ensure clean HTTP status mapping (`404 NOT_FOUND`, `400 BAD_REQUEST`, etc.).
- Never use `.unwrap()` or `.expect()` in production handler paths; always propagate errors with `?`.

### 1.4 Database & SQLx Guidelines
- **Parameterization**: Always use parameterized SQL (`$1`, `$2`) or `sqlx::query_as!` to prevent SQL injection.
- **Transactions**: For operations modifying multiple tables or transitioning state machines, wrap in `sqlx::Transaction`.
- **Sorting Safety**: NEVER interpolate dynamic user strings into `ORDER BY`. Use strict Rust match statements on enums:
  ```rust
  let order_clause = match query.sort_by {
      TaskSortField::CreatedAt => "created_at",
      TaskSortField::Priority => "priority",
      TaskSortField::NextRunAt => "next_run_at",
  };
  ```

### 1.5 Observability & Logging
- Use `tracing` for structured logs (`tracing::info!`, `tracing::warn!`, `tracing::error!`).
- Include structured fields: `tracing::info!(task_id = %id, status = ?status, "Task transitioned successfully")`.

---

## 2. Frontend Standards (`packages/frontend`)

- **Framework**: React 19 + TypeScript + Vite.
- **Routing**: TanStack Router (file-based or code-based routing with strict type safety).
- **Styling**: Tailwind CSS v4.
- **Linter & Formatter**: Biome (`npm run check`, `npm run format`).
- **Code Quality**: Strict TypeScript, no `any`, explicit interfaces/types for all API payloads.

---

## 3. Git Commit Conventions (Conventional Commits)

After each task is completed and verified, create an atomic git commit following the Conventional Commits specification:

### Commit Format
```text
<type>(<scope>): <short descriptive summary in imperative mood>

[optional body explaining context or rationale]
```

### Types
- `feat`: A new feature or capability (e.g. `feat(tasks): create tasks and task_runs database migration`)
- `fix`: A bug fix (e.g. `fix(tasks): correct next_run_at calculation for DST intervals`)
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `test`: Adding or updating tests
- `chore`: Build scripts, dependencies, or tool configurations

### Examples
- `feat(tasks): add tasks database migration and custom enums`
- `feat(tasks): implement Task and TaskRun domain models with SQLx types`
- `feat(tasks): implement TaskRepository with parameterized CRUD queries`
- `feat(tasks): add TaskService with cron evaluation and state transitions`
- `feat(tasks): add Axum route handlers and register under /api/v1/tasks`
- `test(tasks): add integration tests for task lifecycle transitions`

---

## 4. Verification Quality Gates

A task is NOT complete until all applicable verification commands succeed:

| Scope | Verification Command | Gate Condition |
| :--- | :--- | :--- |
| **Rust Compile** | `cargo check --workspace` | 0 errors |
| **Rust Linter** | `cargo clippy --workspace --all-targets -- -D warnings` | 0 warnings / 0 errors |
| **Rust Tests** | `cargo test -p <package>` or `cargo test --workspace` | All tests pass (green) |
| **SQLx Migrations** | `cargo sqlx migrate run` (or DB check) | Migration applies cleanly |
| **TypeScript Typecheck** | `npm run typecheck` (or `npx tsc --noEmit`) | 0 type errors |
| **Biome Linter** | `npm run check` | 0 formatting / lint errors |

---

## 5. Risk & Security Mitigation Checklist

Before finalizing any task, cross-check against `docs/features/<feature>/review.md`:
- [ ] **State Machine Separation**: Are definition states isolated from execution attempt states?
- [ ] **Payload Size Bounds**: Are input and output payloads validated against size limits?
- [ ] **SQL Injection Prevention**: Are sort fields and filter parameters strictly enum-whitelisted?
- [ ] **Timezone UTC Safety**: Are all scheduling timestamps parsed, calculated, and stored in UTC?
- [ ] **Soft Delete Safety**: Do standard list and lookup queries exclude `deleted_at IS NOT NULL`?
- [ ] **Concurrency & Locking**: Are race conditions prevented with row locks (`FOR UPDATE SKIP LOCKED`) or unique constraints?
