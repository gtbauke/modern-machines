# Modern Machines ICM Pipeline (Layer 0: Global Identity)

Welcome to the **Modern Machines Interpretable Context Methodology (ICM)** workspace.
This workspace governs the end-to-end design, asset creation, Java implementation, and verification of features for the **Modern Machines** Minecraft mod (NeoForge 26.2 / Java 21).

## 1. Where Am I?
You are operating in the `pipeline/` directory of the `Modern Machines` codebase.
- Mod ID: `modernmachines`
- Platform: Minecraft 26.2 on NeoForge `26.2.0.59` (Java 21)
- Architecture: Modular Machines, Flexbox GUI Engine, Procedural Asset Generation

## 2. The 5-Layer Context Hierarchy
ICM replaces framework orchestration with filesystem structure. When executing a stage:
- **Layer 0 (Here)**: Global identity, directory map, and execution principles.
- **Layer 1 (`pipeline/CONTEXT.md`)**: Task routing and stage catalog.
- **Layer 2 (`pipeline/stages/<stage>/CONTEXT.md`)**: Stage contract (Inputs, Process, Outputs).
- **Layer 3 (`pipeline/_config/`, `pipeline/references/`)**: Stable reference material (The Factory). Loaded as constraints.
- **Layer 4 (`pipeline/stages/<stage>/output/`)**: Working artifacts (The Product). Unique to the current feature run.

> **CRITICAL RULE**: Do not load the entire codebase or all stage files at once. Only load the files specified in the active stage's `CONTEXT.md` Inputs table.

## 3. The 4 Sequential Stages

| Stage | Name | Input (Layer 4) | Process | Output (Layer 4) | Review Gate |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **01** | `01_design_spec` | User Prompt / Feature Request | Concept, Balance, Energy Math, GUI wireframes, Registry IDs | `01_design_spec/output/feature_spec.md` | Human approves/edits spec |
| **02** | `02_data_and_assets` | `01_design_spec/output/feature_spec.md` | Texture configs, script calls, lang keys, recipes, tags, loot tables | `02_data_and_assets/output/asset_and_data_spec.md`, textures & JSONs | Human checks sprites & JSONs |
| **03** | `03_implementation` | `01_design_spec/output/feature_spec.md`, `02_data_and_assets/output/` | Java code: Blocks, Items, BlockEntities, Menus, Screens, Capabilities | `03_implementation/output/implementation_summary.md` + source code in `src/` | Human reviews code diffs |
| **04** | `04_verification` | `src/`, `scripts/`, `assets/` | Compile (`./gradlew compileJava`), asset sanity checks, test checklist | `04_verification/output/verification_report.md` | Human tests in-game & approves archive |

## 4. Pipeline CLI Tool (`pipeline/pipeline.py`)

A mechanical helper script handles mechanical operations:
- `python pipeline/pipeline.py status` - Show status of active stages.
- `python pipeline/pipeline.py new <feature_name>` - Start a new feature run.
- `python pipeline/pipeline.py check <stage_num>` - Validate stage inputs/outputs against contract.
- `python pipeline/pipeline.py assets` - Run procedural asset generation scripts.
- `python pipeline/pipeline.py build` - Run Gradle compilation check.
- `python pipeline/pipeline.py archive <feature_name>` - Archive completed feature to `pipeline/runs/<feature_name>/`.
