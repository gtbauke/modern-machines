# Modern Machines Pipeline Router (Layer 1: Task Routing)

This file routes incoming development requests to the appropriate ICM pipeline stage and catalogs shared resources.

## 1. Where Do I Go? (Task Routing Matrix)

| User Intent / Activity | Action / Next Step | Target Stage | Contract File |
| :--- | :--- | :--- | :--- |
| "I have an idea for a new machine / block / tool / material" | Initialize feature and draft balance, IDs, mechanics, and GUI wireframe. | Stage 1 | [`stages/01_design_spec/CONTEXT.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/stages/01_design_spec/CONTEXT.md) |
| "I want to create textures, JSON models, recipes, or lang files for the spec" | Generate sprites using Python scripts, define blockstates/models, and recipe data. | Stage 2 | [`stages/02_data_and_assets/CONTEXT.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/stages/02_data_and_assets/CONTEXT.md) |
| "I am ready to write the Java code / BlockEntity / Screen / Logic" | Implement Java classes adhering to machine architecture, capabilities, and flexbox UI. | Stage 3 | [`stages/03_implementation/CONTEXT.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/stages/03_implementation/CONTEXT.md) |
| "I want to compile, run QA checks, and verify in-game" | Run `./gradlew compileJava`, check missing assets/recipes, run manual test checklist. | Stage 4 | [`stages/04_verification/CONTEXT.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/stages/04_verification/CONTEXT.md) |
| "The feature is tested and complete. Let's archive it." | Run `python pipeline/pipeline.py archive <feature_name>` to store artifacts into `pipeline/runs/`. | Maintenance | [`pipeline/pipeline.py`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/pipeline.py) |

---

## 2. Shared Resources (Layer 3 Catalog)

### Factory Configurations (`pipeline/_config/`)
- [`coding_standards.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/_config/coding_standards.md): Java 21, NeoForge 26.2 patterns, registration rules, nullability, package structure.
- [`balance_progression.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/_config/balance_progression.md): FE/t energy tiers, processing times, machine upgrade formulas, material stat tiers.

### Technical References (`pipeline/references/`)
- [`machine_architecture.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/references/machine_architecture.md): `IUpgradableMachine`, `ISideConfigurable`, `MachineStats`, capabilities, tick loops.
- [`materials_and_modular_tools.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/references/materials_and_modular_tools.md): `Material`, `MaterialBuilder`, `ResourceForm`, `ToolPartType`, `PartSlot`.
- [`gui_system.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/references/gui_system.md): Flexbox containers, `ModularContainerScreen`, `SideConfigWindow`, 256x256 UI atlas.
- [`asset_generation.md`](file:///C:/Users/gusta/dev/Modern%20Machines/pipeline/references/asset_generation.md): Python texture scripts (`scripts/generate_...py`), ASCII templates, palette tinting.

---

## 3. Workflow Execution Rules
1. **Never skip review gates**: The human reviews and can edit any file in `stages/<stage>/output/` before the next stage runs.
2. **Context Isolation**: When working on Stage $N$, only load the inputs declared in `stages/<stage>/CONTEXT.md`.
3. **Traceability**: If an implementation error occurs in Stage 3 or 4, check if the issue is in the source contracts (`_config/` or `01_design_spec`) before patching symptoms.
