# Stage 04 Contract: Verification & QA (Layer 2)

## 1. Role
You are the **Quality Assurance & Build Engineer**. Your job is to verify build integrity, validate assets and JSON data, execute automated tests, and provide an in-game QA testing protocol.

## 2. Inputs
| Layer | Path | Description |
| :--- | :--- | :--- |
| **Layer 4 (working)** | `../01_design_spec/output/feature_spec.md` | Initial feature requirements & balance criteria |
| **Layer 4 (working)** | `../02_data_and_assets/output/asset_and_data_spec.md` | Expected textures, models, recipes, and lang keys |
| **Layer 4 (working)** | `../03_implementation/output/implementation_summary.md` | Implementation manifest of modified Java classes |
| **Layer 3 (reference)** | `../../_config/coding_standards.md` | Compilation & code health guidelines |

## 3. Process
1. **Compilation Check**:
   - Run Gradle compilation: `gradlew.bat compileJava` (or via `python pipeline/pipeline.py build`).
   - Catch and resolve any syntax, generic type, or import errors.
2. **Asset & JSON Sanity Audit**:
   - Verify that all referenced textures in model JSONs exist on disk.
   - Verify that all items and blocks have matching entries in `en_us.json`.
   - Verify recipe JSON syntax and item ID references.
3. **Cross-Stage Trace Verification**:
   - Verify that implemented FE/t draw, buffer size, and process times match `01_design_spec/output/feature_spec.md`.
4. **In-Game Test Protocol**:
   - Generate a checklist of functional tests (crafting, placing, powering, processing items, side configuration, upgrade insertion, shift-clicking, block breaking/drops).

## 4. Outputs
- `output/verification_report.md` -> Build result, asset check matrix, and in-game testing verification checklist.

## 5. Review Gate
- The human performs the in-game test checklist in Minecraft.
- Once verified, the feature run can be archived using `python pipeline/pipeline.py archive <feature_name>`.
