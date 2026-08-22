---
description: Interpretable Context Methodology (ICM) Pipeline Workflow for Modern Machines
globs: ["**/*"]
alwaysApply: true
---

# Interpretable Context Methodology (ICM) Rule

When developing new features, machines, materials, tools, or mechanics for **Modern Machines**, you MUST follow the **Interpretable Context Methodology (ICM)** located in `pipeline/`.

## 1. Context Scoping & Layer Hierarchy
Do not dump or load monolithic context into the conversation window. Follow the 5-layer hierarchy:
- **Layer 0 (`pipeline/CLAUDE.md`)**: Workspace identity.
- **Layer 1 (`pipeline/CONTEXT.md`)**: Task routing and catalog.
- **Layer 2 (`pipeline/stages/<stage>/CONTEXT.md`)**: Stage-specific contract. Load only when executing that stage.
- **Layer 3 (`pipeline/_config/`, `pipeline/references/`)**: Stable domain rules and guides (Machine Architecture, Materials & Modular Tools, Flexbox GUI Engine, Asset Generators, Balance curves). Internalize as constraints.
- **Layer 4 (`pipeline/stages/<stage>/output/`)**: Working artifacts for the active feature run.

## 2. The 4 Sequential Stages & Review Gates
1. **`01_design_spec`**: Draft registry IDs, tier progression, balance math, and GUI wireframe to `pipeline/stages/01_design_spec/output/feature_spec.md`. Wait for human review/edits before Stage 02.
2. **`02_data_and_assets`**: Generate sprites via scripts/assets, define localization keys, and write recipe/tag JSON specs to `pipeline/stages/02_data_and_assets/output/asset_and_data_spec.md`.
3. **`03_implementation`**: Implement Java classes in `src/main/java/` (Block, BlockEntity, Menu, Screen, Capabilities) and document in `pipeline/stages/03_implementation/output/implementation_summary.md`.
4. **`04_verification`**: Run `python pipeline/pipeline.py build` (`./gradlew compileJava`), asset sanity checks, and provide an in-game QA testing protocol in `pipeline/stages/04_verification/output/verification_report.md`.

## 3. Automation Helper
Use `pipeline/pipeline.py` for mechanical operations:
- `python pipeline/pipeline.py status`
- `python pipeline/pipeline.py new <feature_name>`
- `python pipeline/pipeline.py check <stage_number>`
- `python pipeline/pipeline.py assets`
- `python pipeline/pipeline.py build`
- `python pipeline/pipeline.py archive <feature_name>`
