# UI/UX Design Specification: {{FEATURE_TITLE}}

- **Task Slug**: `{{TASK_SLUG}}`
- **Author**: UI Designer Skill
- **Date**: {{DATE}}
- **Status**: Review & Option Selection

---

## 1. Context & User Requirements

### Problem Statement
{{PROBLEM_STATEMENT}}

### User Personas & Primary Goals
- **Primary Persona**: {{PRIMARY_PERSONA}}
- **Key User Tasks**:
  1. {{USER_TASK_1}}
  2. {{USER_TASK_2}}
  3. {{USER_TASK_3}}

### Design & Layout Constraints
- **Device Support**: {{DEVICE_SUPPORT}} (e.g. Desktop-first with responsive tablet/mobile viewports)
- **Information Density**: {{INFORMATION_DENSITY}} (e.g. Compact / Balanced / Spacious)
- **Navigation Model**: {{NAVIGATION_MODEL}} (e.g. Sidebar + Breadcrumb / Multi-step Wizard / Canvas Flow)

---

## 2. Structural Wireframe Options

| Option | File Link | Layout Paradigm | Primary Focus |
| :--- | :--- | :--- | :--- |
| **Option A** | [`option-a.html`](./option-a.html) | {{OPTION_A_PARADIGM}} | {{OPTION_A_FOCUS}} |
| **Option B** | [`option-b.html`](./option-b.html) | {{OPTION_B_PARADIGM}} | {{OPTION_B_FOCUS}} |
| **Option C** (Optional) | [`option-c.html`](./option-c.html) | {{OPTION_C_PARADIGM}} | {{OPTION_C_FOCUS}} |

---

## 3. Side-by-Side Trade-off Comparison

### Option A: {{OPTION_A_TITLE}}
- **Layout Description**: {{OPTION_A_DESCRIPTION}}
- **Strengths (Pros)**:
  - {{OPTION_A_PRO_1}}
  - {{OPTION_A_PRO_2}}
- **Trade-offs (Cons)**:
  - {{OPTION_A_CON_1}}
  - {{OPTION_A_CON_2}}
- **Best Suited For**: {{OPTION_A_BEST_FOR}}

### Option B: {{OPTION_B_TITLE}}
- **Layout Description**: {{OPTION_B_DESCRIPTION}}
- **Strengths (Pros)**:
  - {{OPTION_B_PRO_1}}
  - {{OPTION_B_PRO_2}}
- **Trade-offs (Cons)**:
  - {{OPTION_B_CON_1}}
  - {{OPTION_B_CON_2}}
- **Best Suited For**: {{OPTION_B_BEST_FOR}}

---

## 4. Component Hierarchy & Information Architecture

```
{{COMPONENT_HIERARCHY_TREE}}
```

### Key Interactive Components
1. **{{COMPONENT_1_NAME}}**: {{COMPONENT_1_DESCRIPTION}}
2. **{{COMPONENT_2_NAME}}**: {{COMPONENT_2_DESCRIPTION}}
3. **{{COMPONENT_3_NAME}}**: {{COMPONENT_3_DESCRIPTION}}

---

## 5. Responsive & Mobile Strategy

- **Desktop (>= 1024px)**: {{DESKTOP_BEHAVIOR}}
- **Tablet (768px - 1023px)**: {{TABLET_BEHAVIOR}}
- **Mobile (< 768px)**: {{MOBILE_BEHAVIOR}}

---

## 6. Frontend Implementation Notes

- **Target Framework**: React 19 + Tailwind CSS + TanStack Router
- **Component Primitives**: {{COMPONENT_PRIMITIVES}} (e.g. Radix UI / Headless UI / Lucide icons)
- **State Management Considerations**: {{STATE_CONSIDERATIONS}} (e.g. TanStack Query caching, URL search params for filters)
