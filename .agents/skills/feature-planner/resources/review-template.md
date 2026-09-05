# Feature Review & Risk Assessment: {Feature Name}

- **Specification Reference**: [`spec.md`](./spec.md)
- **Reviewer**: Feature Reviewer Agent
- **Date**: {YYYY-MM-DD}
- **Overall Assessment**: [Low Risk | Medium Risk | High Risk / Needs Refinement]

---

## 1. Executive Summary
Brief high-level summary of the feature scope, viability, and overall architectural readiness.

---

## 2. Scope Completeness & Missing Requirements
- **Omitted Edge Cases**:
  - {Detail edge cases missing from the specification}
- **Ambiguous Requirements**:
  - {Items in the specification that lack clear acceptance criteria or definitions}
- **Missing Failure Modes**:
  - {Network errors, DB lockups, partial state updates, concurrent conflicts}

---

## 3. Technical & Architectural Risks
| Risk Area | Severity (Low/Med/High) | Description & Potential Impact | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **Data Integrity** | Medium | {e.g. Race conditions during simultaneous updates} | {Use DB transactions / row locking} |
| **Performance** | High | {e.g. N+1 queries on nested workflow nodes} | {Batch fetching with SQLx query} |
| **Breaking Changes** | Low | {e.g. Modifying existing API payload contract} | {Add backwards-compatible schema fields} |
| **State Sync** | Medium | {e.g. Frontend cache desynchronization} | {Cache invalidation / optimistic update rollback} |

---

## 4. Security & Compliance Considerations
- **Authentication / Authorization gaps**: {Check endpoint guards, user tenancy}
- **Input Validation / Injection vectors**: {Check serde deserialization, SQL injection, XSS}
- **Sensitive Data Exposure**: {Secrets or PII leakage in logs or responses}

---

## 5. Backward Compatibility & Migration Impact
- **Database Schema**: Impact on existing rows, zero-downtime migration feasibility.
- **API Clients**: Breaking changes vs additive changes.

---

## 6. Recommendations & Action Items for Implementation Plan
1. **Critical Pre-requisites**: Must be addressed before coding begins.
2. **Suggested Scope Adjustments**: Features to simplify or split into later phases.
3. **Key Safeguards**: Patterns or validations the implementation plan MUST incorporate.
