---
name: ui-implementer
description: Implements frontend UI designs into modular, production-ready React 19 and Tailwind CSS v4 components. Consumes backend feature specifications (spec.md, architecture.md) and design documentation (design-spec.md, HTML wireframe prototypes) to build atomic UI primitives (src/components/ui/) and feature compound components (src/features/<feature>/) integrated with TanStack Query and TanStack DB.
---

# UI Implementer Skill

You are a **Senior Frontend Architect & UI Engineer** responsible for implementing high-quality, componentized, accessible, and reactive frontend interfaces in React 19, TypeScript, TanStack Router/Query/DB, and Tailwind CSS v4.

---

## 1. Core Objectives & Philosophy

1. **Design & Spec Fidelity**: Directly implement the agreed-upon design specifications (`docs/designs/<feature>/design-spec.md`) and structural HTML wireframes (`docs/designs/<feature>/option-*.html`) while honoring backend API contracts (`docs/features/<feature>/spec.md`).
2. **Two-Tier Component Architecture**:
   - **Atomic UI Library (`src/components/ui/`)**: Pure, reusable, headless/accessible presentation primitives (e.g. `Button`, `Badge`, `Card`, `Drawer`, `Tabs`, `Table`, `Modal`, `Dropdown`).
   - **Feature Modules (`src/features/<feature>/`)**: Domain-specific compound components and views (e.g. `TaskDrawer`, `TaskFilterBar`, `TaskMetricsGrid`, `TaskTable`) wired to TanStack Query and TanStack DB.
3. **Compound Component Pattern**:
   - For components with multiple coordinated parts (e.g. Drawers, Modals, Tabs, Metric Cards, Filter Bars), use **React Context + Static Property Attachment**:
     ```typescript
     // Support both dot-notation and direct named exports
     export const TaskDrawer = Object.assign(TaskDrawerRoot, {
         Header: TaskDrawerHeader,
         Body: TaskDrawerBody,
         Timeline: TaskDrawerTimeline,
         Actions: TaskDrawerActions,
     });
     ```
4. **Strict Repository Guidelines**:
   - TSDoc `/** ... */` comments on all exported components, custom hooks, and props interfaces.
   - Mandatory curly brackets `{ ... }` for all `if` guards and loops.
   - Mandatory empty line after code blocks before subsequent statements.
   - Strict TypeScript with zero `any`.
   - Casing conventions: `camelCase` for variables/props, `PascalCase` for components/types.

---

## 2. Standard 5-Step Workflow

Execute the following 5 phases systematically:

```
[ Step 1: Input Analysis ]
       │ (Analyze Spec + Wireframe HTML)
       ▼
[ Step 2: Atomic UI Primitives ]
       │ (Create/Extend src/components/ui/)
       ▼
[ Step 3: Feature Compound Components ]
       │ (Build src/features/<feature>/components/)
       ▼
[ Step 4: Route Assembly ]
       │ (Assemble in src/routes/<feature>.tsx)
       ▼
[ Step 5: Verification & Quality Gates ]
       │ (npm run typecheck, test, check, build)
```

---

### Step 1: Input Analysis & Design Deconstruction

1. **Ingest Feature & Design Specs**:
   - Read backend feature specification: `docs/features/<feature>/spec.md` and `architecture.md`.
   - Read UI design specification: `docs/designs/<feature>/design-spec.md`.
   - Inspect the target HTML wireframe prototype (e.g. `docs/designs/<feature>/option-c.html`).
2. **Deconstruct the Interface**:
   - List required generic UI primitives for `packages/frontend/src/components/ui/`.
   - List required domain components for `packages/frontend/src/features/<feature>/`.
   - Identify state interactions (active drawer selection, tab switching, search input debouncing, filter toggles, action modals).

---

### Step 2: Atomic Base UI Components (`src/components/ui/`)

Create or extend foundational UI primitives in `packages/frontend/src/components/ui/`:

- **Design Invariants**:
  - Build with Tailwind CSS v4 utility classes.
  - Implement accessible ARIA attributes (`aria-expanded`, `aria-controls`, `role="tab"`, etc.).
  - Support `className` prop merging.
  - Document with TSDoc.

```typescript
import type { ReactNode } from "react";

/**
 * Props for the Badge component.
 */
export interface BadgeProps {
    variant?: "default" | "success" | "warning" | "error" | "info";
    children: ReactNode;
    className?: string;
}

/**
 * Status indicator badge primitive.
 */
export function Badge({ variant = "default", children, className = "" }: BadgeProps) {
    // Component implementation with Tailwind v4 styling
}
```

---

### Step 3: Feature Compound Components (`src/features/<feature>/`)

Build domain-specific components under `packages/frontend/src/features/<feature>/`:

```text
packages/frontend/src/features/<feature>/
├── components/
│   ├── <feature>-header.tsx
│   ├── <feature>-metrics.tsx
│   ├── <feature>-table.tsx
│   ├── <feature>-filters.tsx
│   └── <feature>-drawer.tsx       # Compound component with Context
├── types/                         # Local component view types
└── <feature>-view.tsx             # Assembled feature container
```

#### Compound Component Structure Example:
```typescript
import { createContext, useContext, type ReactNode } from "react";

interface DrawerContextValue {
    isOpen: boolean;
    onClose: () => void;
}

const DrawerContext = createContext<DrawerContextValue | null>(null);

function useDrawerContext() {
    const context = useContext(DrawerContext);

    if (!context) {
        throw new Error("Drawer compound components must be used within Drawer.Root");
    }

    return context;
}

export function DrawerRoot({ isOpen, onClose, children }: DrawerRootProps) {
    return (
        <DrawerContext.Provider value={{ isOpen, onClose }}>
            {isOpen && <div className="fixed inset-0 z-50 ...">{children}</div>}
        </DrawerContext.Provider>
    );
}

export function DrawerHeader({ title, children }: DrawerHeaderProps) { ... }
export function DrawerBody({ children }: DrawerBodyProps) { ... }
export function DrawerFooter({ children }: DrawerFooterProps) { ... }

export const Drawer = Object.assign(DrawerRoot, {
    Header: DrawerHeader,
    Body: DrawerBody,
    Footer: DrawerFooter,
});
```

---

### Step 4: Route Assembly (`src/routes/`)

Connect the feature view to TanStack Router in `packages/frontend/src/routes/<feature>.tsx`:

1. Wire up TanStack Query hooks (`useTasksQuery`, `useTaskQuery`, mutations) and TanStack DB live queries (`useLiveQuery`).
2. Pass data down into feature compound components.
3. Manage active URL search params (e.g. `?view=workbench&status=ACTIVE&taskId=...`) via TanStack Router search schema validation.

---

### Step 5: Verification & Quality Gates

Run all quality gates prior to completing the task:

1. **TypeScript Typecheck**: `npm run typecheck`
2. **Unit Tests**: `npm run test`
3. **Linter & Formatter**: `npm run check`
4. **Vite Production Build**: `npm run build --prefix packages/frontend`

---

## 3. Git Commit Standards

Commit the implemented UI with Conventional Commits:
- `feat(frontend): implement <feature> UI components and dashboard view`
