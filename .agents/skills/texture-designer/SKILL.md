---
name: texture-designer
description: >-
  Designs, previews, and exports 16x16 pixel art textures for Minecraft blocks, items, machines, and modular tool parts.
  Harmonizes color palettes with vanilla Bedrock textures (C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures),
  generates rich interactive HTML design boards with 2D scaled and 3D isometric previews, and exports lossless PNGs directly to mod assets.
---

# Texture Designer Skill

The **Texture Designer Skill** guides the visual design, palette harmonization, interactive HTML previewing, and asset deployment of 16x16 pixel art textures for the *Modern Machines* mod.

---

## 1. Core Principles

1. **Bedrock Palette & Style Harmonization**:
   - Always reference vanilla reference textures located at:
     `C:\Users\gusta\dev\bedrock-samples-main\bedrock-samples-main\resource_pack\textures\`
   - Sample base stone (`blocks/stone.png`, `blocks/deepslate.png`, `blocks/tuff.png`), metal blocks (`blocks/iron_block.png`, `blocks/copper_block.png`), and items (`items/`) to match Minecraft's signature 16x16 contrast, beveling, dithering, and shading curves.

2. **Interactive HTML Design Board (`preview.html`)**:
   - Every texture task generates a dedicated interactive HTML preview at:
     `docs/designs/textures/<asset-slug>/preview.html`
   - Must present at least 2 distinct artistic directions (e.g. Option A vs Option B).
   - Features 1x (native), 4x (handheld), 8x/16x (detail canvas) zoom views.
   - Features an interactive 3D rotating isometric cube preview for blocks.
   - Displays color palette swatches with hex codes and interactive pixel breakdown grid.

3. **Dual Export Pipeline**:
   - **Automated CLI Export**: Uses `.agents/skills/texture-designer/scripts/export_png.py` to convert color matrix JSON files directly into lossless 32-bit RGBA PNG files in `src/main/resources/assets/modernmachines/textures/`.
   - **Browser-Side Export**: Includes a "Download 16x16 PNG" button in the HTML preview for manual inspection and downloads.

---

## 2. Directory Layout & Asset Conventions

| Asset Type | Source Target Directory | Model / Blockstate Config |
| :--- | :--- | :--- |
| **Standard Blocks** | `src/main/resources/assets/modernmachines/textures/block/<name>.png` | `ModelTemplates.CUBE_ALL` |
| **Machine Faces** | `.../textures/block/<name>_front.png`, `_top.png`, `_side.png` | `TexturedModel.ORIENTABLE_ONLY_TOP` |
| **Pipes & Tubing** | `.../textures/block/pipe/<name>_center.png`, `_arm.png` | Multipart generator |
| **Flat Items / Tools**| `src/main/resources/assets/modernmachines/textures/item/<name>.png` | `ModelTemplates.FLAT_ITEM` |
| **Tool Parts** | `.../textures/item/template/part/<part_type>.png` | Dynamic tint provider |

---

## 3. Step-by-Step Workflow

### Phase 1: Reference Inspection & Palette Extraction
1. Identify the texture requirements (e.g. geological barrier rock, casing metal, valve dial, fluid overlay).
2. Locate the corresponding vanilla reference textures in Bedrock samples:
   - Example Deepslate/Stone: `.../resource_pack/textures/blocks/deepslate.png`
   - Example Metals: `.../resource_pack/textures/blocks/netherite_block.png`
   - Example Gauges/Machinery: `.../resource_pack/textures/blocks/furnace_front_off.png`
3. Define a coherent 4 to 8 color palette representing shadow, midtone, highlight, and accent hues.

### Phase 2: HTML Design Board Generation
1. Create `docs/designs/textures/<asset-slug>/preview.html` using `.agents/skills/texture-designer/templates/preview_template.html`.
2. Construct 2 distinct 16x16 pixel matrix options (`Option A` and `Option B`):
   - Option A: Standard industrial/geological design matching standard mod aesthetics.
   - Option B: Stylized or heavy-duty alternative with distinct bevels, trims, or wear patterns.
3. Include the color palette map and full 16x16 grid arrays.

### Phase 3: User Review & Selection
1. Inform the user of the generated design board with a clickable file link to `docs/designs/textures/<asset-slug>/preview.html`.
2. Solicit feedback or selection between the proposed options.

### Phase 4: PNG Export & Build Verification
1. Create a JSON definition file `docs/designs/textures/<asset-slug>/texture_def.json` containing the approved pixel matrix and target output path:
   ```json
   {
     "textures": [
       {
         "output": "src/main/resources/assets/modernmachines/textures/block/<name>.png",
         "width": 16,
         "height": 16,
         "palette": {
           "0": "#23272A",
           "1": "#3B4048",
           "2": "#4A515B"
         },
         "pixels": [
           [0, 1, 2, ...],
           ...
         ]
       }
     ]
   }
   ```
2. Run the export script:
   ```powershell
   python .agents/skills/texture-designer/scripts/export_png.py --json docs/designs/textures/<asset-slug>/texture_def.json
   ```
3. Run `./gradlew runClientData` to ensure client asset caches and models update cleanly.
