# Low-Level Design & Implementation Plan: {Feature Name}

- **Specification Reference**: [`spec.md`](./spec.md)
- **Review Reference**: [`review.md`](./review.md)
- **Author**: LLD Architect Agent
- **Date**: {YYYY-MM-DD}
- **Status**: Ready for Implementation

---

## 1. Architecture & Design Decisions
- **Design Pattern**: {e.g. Repository pattern, service layer, command handler, state machine}
- **Key Architectural Choices**:
  - Choice 1: {Rationale & Trade-offs}
  - Choice 2: {Rationale & Trade-offs}

---

## 2. Data Model & Database Schema Changes
### 2.1 Schema Migrations (SQLx)
```sql
-- Migration: YYYYMMDDHHMMSS_create_feature_table.sql
CREATE TABLE IF NOT EXISTS feature_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 2.2 Rust Models & Enums
```rust
#[derive(Debug, Clone, Serialize, Deserialize, sqlx::FromRow)]
pub struct FeatureItem {
    pub id: Uuid,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}
```

---

## 3. Backend API & Service Design (`packages/backend`)
### 3.1 Endpoint Contracts
- `POST /api/v1/features` - Create feature item
  - Request Body: `{ ... }`
  - Response (201 Created): `{ ... }`
- `GET /api/v1/features/:id` - Fetch item
- `PATCH /api/v1/features/:id` - Update item
- `DELETE /api/v1/features/:id` - Delete item

### 3.2 Error Types & Status Mapping
```rust
#[derive(Debug, thiserror::Error)]
pub enum FeatureError {
    #[error("Item not found with id {0}")]
    NotFound(Uuid),
    #[error("Validation failed: {0}")]
    Validation(String),
}
```

---

## 4. Frontend Design & Component Structure (`packages/frontend`)
### 4.1 Routes & TanStack Router
- Route: `/features` or `/workflows/$id/...`
- Loaders & Search Params: `{ ... }`

### 4.2 Component Hierarchy
```text
<FeatureContainer>
 ├── <FeatureHeader />
 ├── <FeatureList>
 │    └── <FeatureListItem />
 └── <FeatureModal />
```

### 4.3 State Management & Hooks
- Query keys & mutation hooks (`useQuery`, `useMutation`).
- Optimistic updates and error toasts.

---

## 5. Step-by-Step Implementation Task List

### Phase 1: Database & Backend Core
- [ ] **Task 1.1**: Create database migration in `packages/backend/migrations/`
  - *Verification*: Run `cargo sqlx migrate run` (or test query).
- [ ] **Task 1.2**: Define domain models, errors, and repository functions
  - *Verification*: Unit tests in `packages/backend/src/...`
- [ ] **Task 1.3**: Implement Axum route handlers and register in router
  - *Verification*: Integration tests via `cargo test -p backend`

### Phase 2: Frontend Integration
- [ ] **Task 2.1**: Define TypeScript interfaces and API client functions
  - *Verification*: Type check with `npm run typecheck`
- [ ] **Task 2.2**: Build UI components and integrate with TanStack Router
  - *Verification*: Verify component rendering and Vite build
- [ ] **Task 2.3**: Connect mutation hooks, loading states, and error handling
  - *Verification*: End-to-end user journey test in browser / dev mode

### Phase 3: Validation, Polish & Documentation
- [ ] **Task 3.1**: Run full workspace test suite: `npm run test` and `cargo test --workspace`
- [ ] **Task 3.2**: Run linter and formatting: `npm run check` and `cargo clippy --workspace`
- [ ] **Task 3.3**: Update user-facing docs / README if applicable
