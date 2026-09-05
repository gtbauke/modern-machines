---
name: feature-planner
description: >-
  Guides the user through end-to-end feature planning: eliciting functional and non-functional requirements,
  assessing integration with existing systems, drafting a Feature Specification, launching an expert Reviewer
  subagent for risk analysis and edge-case detection, and dispatching an LLD Architect subagent to generate
  a detailed Low-Level Design implementation plan.
---

# Feature Planner Skill

This skill guides you through a robust, 4-phase feature design pipeline:
1. **Requirements Gathering & Specification**: Collaboratively define functional/non-functional requirements, integration boundaries, and acceptance criteria.
2. **Feature Review & Risk Assessment (Subagent)**: Launch a dedicated reviewer subagent (`Model: pro`) to stress-test the specification for missing edge cases, architectural risks, and security gaps.
3. **Low-Level Design (LLD) & Implementation Plan (Subagent)**: Launch an LLD architect subagent (`Model: pro`) to formulate the technical architecture, data schemas, API contracts, frontend state structure, and a phased task breakdown with verification steps.
4. **Handoff & Artifact Delivery**: Save all documents in `docs/features/<feature-slug>/` and mirror them as session artifacts for immediate execution.

---

## Workflow Steps

### Phase 1: Requirements Gathering & System Context

When this skill is activated for a feature:

1. **Establish Feature Slug & Directory**:
   - Determine a kebab-case slug for the feature (e.g., `workflow-execution-engine`).
   - The artifacts will be created under `docs/features/<feature-slug>/`.

2. **Explore Existing Codebase**:
   - Before asking redundant questions, inspect relevant existing modules:
     - Backend: [`packages/backend/src/`](../../../packages/backend/src/)
     - Frontend: [`packages/frontend/src/`](../../../packages/frontend/src/)
     - Database schema, migrations, and docker configuration.

3. **Interview the User on Requirements**:
   - Gather critical details through structured questions or dialog:
     - **Goals & User Stories**: What problem does this solve? Who is the user?
     - **Functional Requirements (FRs)**: What are the specific inputs, operations, state transitions, and outputs?
     - **Non-Functional Requirements (NFRs)**: Latency, throughput, security/auth, error handling, reliability.
     - **Integration with Existing Services**: How does it interface with Axum backend, PostgreSQL, Redis, and React frontend?
     - **Scope Boundaries**: What is strictly In-Scope for this release vs Out-of-Scope (future work)?

4. **Draft the Feature Specification**:
   - Load the specification template from [`resources/spec-template.md`](./resources/spec-template.md).
   - Fill in all sections based on the user's requirements and codebase exploration.
   - Write the file to `docs/features/<feature-slug>/spec.md`.

---

### Phase 2: Feature Scope Review & Risk Analysis (Agent 1)

Once `spec.md` is generated, launch a dedicated reviewer subagent to evaluate the design.

1. **Invoke Subagent**:
   - Tool: `invoke_subagent`
   - `TypeName`: `"self"`
   - `Role`: `"Feature Reviewer & Risk Analyst"`
   - `Model`: `"pro"`
   - `Prompt`:
     ```text
     You are a Principal Software Architect and Security Specialist reviewing a newly drafted Feature Specification.

     Target Feature Specification: docs/features/<feature-slug>/spec.md
     Review Template: .agents/skills/feature-planner/resources/review-template.md
     Output File: docs/features/<feature-slug>/review.md

     Instructions:
     1. Read the feature specification at docs/features/<feature-slug>/spec.md.
     2. Review the codebase (packages/backend, packages/frontend, database setup) to understand existing patterns and constraints.
     3. Perform a rigorous, critical review:
        - Identify missing edge cases, ambiguous acceptance criteria, and failure modes.
        - Evaluate technical risks (race conditions, N+1 queries, state desync, breaking API changes).
        - Evaluate security, authentication, and data integrity concerns.
        - Assess backward compatibility and database migration risks.
     4. Write your comprehensive analysis to docs/features/<feature-slug>/review.md following the review template structure.
     5. Report back when the review artifact is written with a concise executive summary of the top findings.
     ```

2. **Wait for Agent Completion**:
   - Review the subagent's findings in `docs/features/<feature-slug>/review.md`.
   - If critical blocking gaps are identified, align with the user on any necessary adjustments.

---

### Phase 3: Low-Level Design & Implementation Plan (Agent 2)

After the review is complete, launch a dedicated LLD architect subagent to create the detailed technical plan.

1. **Invoke Subagent**:
   - Tool: `invoke_subagent`
   - `TypeName`: `"self"`
   - `Role`: `"LLD Architect"`
   - `Model`: `"pro"`
   - `Prompt`:
     ```text
     You are a Staff Software Engineer and Technical Lead creating a Low-Level Design (LLD) and Implementation Plan.

     Specification: docs/features/<feature-slug>/spec.md
     Risk Review: docs/features/<feature-slug>/review.md
     LLD Template: .agents/skills/feature-planner/resources/lld-template.md
     Output File: docs/features/<feature-slug>/plan.md

     Instructions:
     1. Read both docs/features/<feature-slug>/spec.md and docs/features/<feature-slug>/review.md.
     2. Inspect the existing codebase architecture (Rust Axum backend, SQLx schemas, React/Vite/TanStack Router frontend).
     3. Design the technical implementation addressing all risks and edge cases identified in the review:
        - Exact database schema migrations (SQL DDL).
        - Rust data structs, error types (thiserror), Axum route signatures, and service logic.
        - Frontend component hierarchy, TanStack Router routes, state/query hooks, and UX error handling.
        - Phased, ordered task list with actionable checkboxes and concrete verification commands for every single task.
     4. Write the final document to docs/features/<feature-slug>/plan.md following the LLD template.
     5. Report back when complete with a summary of the architectural strategy.
     ```

2. **Wait for Agent Completion**:
   - Verify `docs/features/<feature-slug>/plan.md` has been successfully created.

---

### Phase 4: Delivery & Handoff

Present the completed feature planning bundle to the user:
1. Provide clickable markdown links to all 3 artifacts:
   - Specification: `docs/features/<feature-slug>/spec.md`
   - Review & Risk Assessment: `docs/features/<feature-slug>/review.md`
   - Implementation Plan: `docs/features/<feature-slug>/plan.md`
2. Highlight key architectural trade-offs, mitigations, and the phased breakdown.
3. Recommend proceeding directly with execution using the phased checklist or via `/plan`.
