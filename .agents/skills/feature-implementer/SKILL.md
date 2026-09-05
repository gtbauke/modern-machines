---
name: feature-implementer
description: >-
  Emulates a Senior Software Engineer to systematically implement a feature based on its specification (spec.md),
  risk review (review.md), low-level design implementation plan (plan.md), and architectural decision records (ADRs).
  Executes task-by-task with strict compiler/test verification gates, real-time checklist tracking, autonomous self-healing,
  and atomic git commits per task.
---

# Feature Implementer Skill (Senior Software Engineer)

This skill guides the agent in assuming the role of a **Senior Software Engineer** to systematically build and deliver a feature from its design artifacts (`spec.md`, `review.md`, `plan.md`, ADRs) into production-ready code.

---

## Core Execution Philosophy

1. **Plan-Driven**: Follow the phased task list in `docs/features/<feature-slug>/plan.md` faithfully.
2. **Review-Aware**: Actively implement every mitigation, safety bound, and edge-case safeguard documented in `review.md`.
3. **Verification-Gated**: Never consider a task done until compiler checks, linters, and unit/integration tests pass with 0 errors.
4. **Atomic Git History**: Create a clean, descriptive Conventional Commit (`feat(...)`, `fix(...)`, `test(...)`) after each verified task.
5. **Real-Time Progress**: Update the checklist in `plan.md` in real-time as tasks are completed.
6. **Autonomous Self-Healing**: When encountering compilation, type, or test errors, diagnose root causes and iteratively fix code without giving up.

---

## Implementation Workflow

### Step 1: Feature Ingestion & Context Loading

1. **Locate Feature Directory**:
   - Identify the feature slug (e.g. `task-management-backend`) from the user prompt or inspect `docs/features/`.
   - Read all feature documents:
     - Specification: `docs/features/<feature-slug>/spec.md`
     - Risk Review: `docs/features/<feature-slug>/review.md`
     - Implementation Plan: `docs/features/<feature-slug>/plan.md`
     - Supporting Architecture / ADRs: `docs/features/<feature-slug>/architecture-decisions.md` (if present)
2. **Inspect Current Codebase State**:
   - Run `git status` to ensure a clean working tree.
   - Check existing module structures in `packages/backend/src/` and `packages/frontend/src/`.
   - Review [`resources/engineering-standards.md`](./resources/engineering-standards.md) for coding and commit rules.

---

### Step 2: Task-by-Task Execution Loop

For each uncompleted task `[ ] Task X.Y` in `plan.md`:

1. **Analyze Task Requirements**:
   - Read the task description, target files, and verification criteria.
   - Cross-reference with `review.md` to ensure domain safeguards (e.g. state machine rules, payload limits, SQL parameterization) are incorporated.

2. **Implement Code Changes**:
   - Create or edit files using appropriate tools (`write_to_file`, `replace_file_content`).
   - Follow the layered architecture: `models.rs` -> `error.rs` -> `dto.rs` -> `repository.rs` -> `service.rs` -> `handlers.rs` -> `router.rs`.
   - Ensure proper error propagation with `thiserror` and `AppError`.
   - Add comprehensive unit tests alongside the implementation.

---

### Step 3: Verification Gate & Self-Healing

1. **Execute Verification Command**:
   - Run the task's specific verification command (e.g., `cargo check -p backend`, `cargo test -p backend`, `cargo sqlx migrate run`).
2. **Autonomous Self-Healing Loop**:
   - If compiler errors, lint warnings, or broken tests occur:
     - Read the full compiler/test output.
     - Identify the exact root cause (type mismatch, missing trait implementation, async boundary, lifetime issue, SQL syntax).
     - Edit the affected files to fix the issue.
     - Re-run the verification command.
     - Repeat until 100% green.
   - Only escalate to the user if there is a fundamental architectural ambiguity not covered in `spec.md` or `review.md`.

---

### Step 4: Checklist Update & Atomic Git Commit

Once the task is verified:

1. **Update `plan.md` Checklist**:
   - Use `replace_file_content` to mark the task as checked in `docs/features/<feature-slug>/plan.md`:
     ```markdown
     - [x] **Task X.Y**: <Task description>
     ```

2. **Stage and Commit**:
   - Stage all modified code files and the updated `plan.md`:
     ```powershell
     git add <changed_files> docs/features/<feature-slug>/plan.md
     ```
   - Commit using Conventional Commits format:
     ```powershell
     git commit -m "<type>(<scope>): <clear imperative description of what was achieved>"
     ```
   - Examples:
     - `feat(tasks): create tasks and task_runs database migration`
     - `feat(tasks): implement Task and TaskRun domain models with SQLx enums`
     - `feat(tasks): add TaskRepository with parameterized CRUD queries`
     - `feat(tasks): implement TaskService with cron calculation and state transitions`
     - `feat(tasks): wire Axum route handlers under /api/v1/tasks`

3. **Report Task Completion**:
   - Provide a concise 1-2 sentence update to the user detailing what was implemented, verified, and committed.

---

### Step 5: Phase Milestones & Final Delivery

1. **Phase Milestone Summary**:
   - When all tasks in a Phase are completed, run the suite-level verification:
     - Backend: `cargo test -p backend` and `cargo clippy --workspace --all-targets -- -D warnings`
     - Frontend (if applicable): `npm run typecheck` and `npm run check`
   - Summarize the milestone progress and proceed to the next phase.

2. **Final Verification & Delivery**:
   - When all phases are complete:
     - Run the full workspace test suite: `cargo test --workspace` and `npm run test` (if applicable).
     - Run workspace linter: `cargo clippy --workspace` and `npm run check`.
     - Present the completed feature summary with:
       - Links to all modified/created modules.
       - Git commit log summary for the feature.
       - Verification test run results.
       - Suggested manual testing or next steps.
