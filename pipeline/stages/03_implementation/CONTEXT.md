# Stage 03 Contract: Java Implementation (Layer 2)

## 1. Role
You are the **Lead Java Engineer**. Your job is to implement clean, type-safe, performant Java 21 code integrating with NeoForge 26.2 and the Modern Machines framework.

## 2. Inputs
| Layer | Path | Description |
| :--- | :--- | :--- |
| **Layer 4 (working)** | `../01_design_spec/output/feature_spec.md` | Feature design specification and balance math |
| **Layer 4 (working)** | `../02_data_and_assets/output/asset_and_data_spec.md` | Asset paths, lang keys, and data structure mappings |
| **Layer 3 (reference)** | `../../_config/coding_standards.md` | Java 21 / NeoForge conventions, naming rules, registry patterns |
| **Layer 3 (reference)** | `../../references/machine_architecture.md` | Machine interface contracts, capabilities, and server tick loops |
| **Layer 3 (reference)** | `../../references/gui_system.md` | Flexbox UI element composition and modular container screens |

## 3. Process
1. **Registry Registration**:
   - Register new Blocks, Items, BlockEntities, MenuTypes, or RecipeTypes in `core.registry.*`.
2. **Block & BlockEntity Implementation**:
   - Implement block class with properties, placement states, and tool requirements.
   - Implement `BlockEntity` handling `IUpgradableMachine`, `ISideConfigurable`, `MenuProvider`, NBT serialization, and server-side ticking.
3. **Menu & Container Implementation**:
   - Implement `AbstractContainerMenu` handling slot synchronization, quick-move/shift-click transfers, and data slots.
4. **Client GUI Screen**:
   - Implement `ModularContainerScreen` subclass using the Flexbox container tree (`Column`, `Row`, `SlotElement`, `ProgressBarElement`, `SideTabElement`).
5. **Capabilities Binding**:
   - Ensure energy and item capabilities are exposed via `ModBlockEntities::registerCapabilities`.

## 4. Outputs
- `output/implementation_summary.md` -> Summary of all created/modified Java classes, registration entries, and architectural notes.
- Java source files written directly to `src/main/java/io/github/gtbauke/modernmachines/...`.

## 5. Review Gate
- The human inspects git diffs and `output/implementation_summary.md`.
- Code reviews, architectural adjustments, and refactors are verified before proceeding to Stage 04.
