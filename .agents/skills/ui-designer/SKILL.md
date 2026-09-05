---
name: ui-designer
description: >-
  Guides the user through UI/UX design tasks by gathering user requirements, defining information hierarchy,
  and presenting multiple structural HTML wireframe options built with Tailwind CSS. Focuses on layout
  structure, component placement, visual flow, and density without excessive low-level aesthetic details.
---

# UI Designer Skill

The **UI Designer Skill** translates user requirements into clear, interactive, and structural UI wireframe prototypes. It helps designers and engineers explore multiple layout paradigms, compare trade-offs, and establish a solid architectural design specification before implementing production code.

---

## Core Philosophy

1. **Structure over Aesthetics**: Focus on information architecture, spatial relationships, visual hierarchy, component boundaries, and navigation flow rather than pixel-perfect branding or custom illustrations.
2. **Self-Contained & Browser-Viewable**: Generate portable HTML wireframes using Tailwind CSS CDN (`https://cdn.tailwindcss.com`) that can be opened and tested directly in any browser with zero build steps.
3. **Multiple Concrete Options**: Always present at least 2 distinct layout/structural directions (e.g., Option A vs Option B) to allow meaningful comparison and trade-off evaluation.
4. **Clean Handoff**: Document all decisions in `docs/designs/<task-slug>/design-spec.md` with component breakdowns and responsive guidelines ready for React/Tailwind implementation.

---

## Workflow Steps

### Phase 1: Requirements Gathering & Information Architecture

When activated for a UI design task:

1. **Establish Task Slug & Output Directory**:
   - Determine a kebab-case slug for the design task (e.g., `task-execution-dashboard`).
   - Create all design files under `docs/designs/<task-slug>/`.

2. **Explore Codebase & Data Models**:
   - Inspect backend models (`packages/backend/src/modules/`), DTOs, and frontend types/schemas (`packages/frontend/src/schemas/`) to understand the exact data fields, relationships, and API endpoints available.

3. **Interview the User on UX Requirements**:
   - Use structured questions or dialog to clarify:
     - **Target User & Persona**: Who is using this screen? What is their main mental model?
     - **Primary User Tasks**: What are the top 3 actions the user must accomplish effortlessly?
     - **Information Density**: Does the user need a compact data table, high-level summary cards, visual timelines, or canvas flows?
     - **Device & Viewport Priorities**: Desktop-first (e.g., admin consoles/dashboards) vs Mobile-first vs Responsive multi-pane.
     - **Navigation Paradigm**: Left sidebar, top navigation, drawer modals, split master-detail view, or multi-step wizard.

---

### Phase 2: Generate Structural HTML Wireframe Options

Construct 2 to 3 distinct structural HTML wireframes in `docs/designs/<task-slug>/`:
- `option-a.html` (e.g. Master-Detail Split View / Density-First Table)
- `option-b.html` (e.g. Card Grid + Modal Drawer / Kanban Flow)
- `option-c.html` (optional alternative direction)

#### HTML Wireframe Rules:
1. **Use Tailwind CDN**: Include `<script src="https://cdn.tailwindcss.com"></script>` in `<head>`.
2. **Neutral Wireframe Palette**: Use neutral slate/gray colors (`bg-slate-50`, `bg-white`, `border-slate-200`, `text-slate-900`, `text-slate-500`) with high-contrast primary accents (`bg-blue-600`) for primary calls to action.
3. **Component Boundaries**: Use clear borders (`border`, `border-slate-300`, `rounded-lg`) and dashed container outlines for placeholders.
4. **Realistic Mock Data**: Populate headers, lists, table rows, and badges with realistic domain data (matching backend enums and models) rather than generic `Lorem Ipsum`.
5. **Interactive Stubs**: Add simple hover states (`hover:bg-slate-50`, `hover:border-slate-400`, `cursor-pointer`) and collapsible sections where applicable.

---

### Phase 3: Author Design Specification (`design-spec.md`)

Load the template from [`resources/design-spec-template.md`](./resources/design-spec-template.md) and generate `docs/designs/<task-slug>/design-spec.md`:
1. **Context & User Tasks**: Summarize goals and constraints.
2. **Wireframe Options Summary**: Include table with markdown links to `option-a.html` and `option-b.html`.
3. **Trade-off Analysis**: Compare pros and cons of each option (cognitive load, scanability, scalability for large datasets, interaction cost).
4. **Component Hierarchy Tree**: Outline the component tree breakdown for React implementation.
5. **Responsive Behavior**: Specify layout transitions from desktop to tablet to mobile.

---

### Phase 4: Interactive Review & Selection

1. Present the design options to the user with clickable links to the HTML files and `design-spec.md`.
2. Provide a concise comparison summary highlighting key trade-offs between Option A and Option B.
3. Guide the user to select their preferred direction or combine specific components from each option.
4. Finalize the chosen direction in `design-spec.md` for seamless handoff to frontend engineers.
