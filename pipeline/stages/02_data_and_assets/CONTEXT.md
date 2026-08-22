# Stage 02 Contract: Data & Asset Specification (Layer 2)

## 1. Role
You are the **Technical Artist & Data Integrator**. Your job is to define all visual palettes, invoke asset generation scripts, formulate localization keys, and write datagen JSON specifications.

## 2. Inputs
| Layer | Path | Description |
| :--- | :--- | :--- |
| **Layer 4 (working)** | `../01_design_spec/output/feature_spec.md` | Approved design specification |
| **Layer 3 (reference)** | `../../references/asset_generation.md` | Script catalog, color tinting rules, atlas definitions |
| **Layer 3 (reference)** | `../../references/gui_system.md` | GUI sprite atlas mapping and widget definitions |

## 3. Process
1. **Asset & Texture Generation**:
   - Determine whether existing scripts in `scripts/` (e.g. `generate_resource_textures.py` or `generate_all_assets.py`) need new entries.
   - Run required asset generation commands or create 16x16 pixel art definitions.
2. **Localization (Lang)**:
   - Formulate all `en_us.json` translation keys for blocks, items, tooltips, JEI/REI categories, and screen titles.
3. **Data Specifications (JSON / Datagen)**:
   - Define recipe JSONs (or Datagen calls).
   - Define block/item tag JSONs (e.g. `c:ores`, `c:ingots`, `c:tools/pickaxes`).
   - Define block loot tables (drops with Fortune, Silk Touch, or self-drop).

## 4. Outputs
- `output/asset_and_data_spec.md` -> Master summary of asset configs, lang mappings, and data recipes.
- Generated texture files in `src/main/resources/assets/modernmachines/textures/` or `textures/`.
- Asset / JSON files prepared for implementation.

## 5. Review Gate
- The human inspects `output/asset_and_data_spec.md` and visually reviews generated textures.
- Any color tweaks or translation wording adjustments are made directly before Stage 03.
