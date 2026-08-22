# Asset & Texture Generation Guide (Layer 3 Reference)

This document catalogs the procedural asset generation scripts and pixel art conventions for **Modern Machines**.

---

## 1. Script Catalog (`scripts/`)

| Script | Purpose | When to Run |
| :--- | :--- | :--- |
| `scripts/generate_all_assets.py` | Master pipeline: generates models, blockstates, items, and textures for materials and machines. | When new materials or machine blocks are defined. |
| `scripts/generate_resource_textures.py` | Generates 16x16 shaded sprites for ingots, plates, dusts, rods, wires, nuggets, and ores. | When new metal/alloy colors are added. |
| `scripts/generate_part_templates.py` | Generates 16x16 grayscale template textures for modular tool heads, handles, and bindings. | When adding a new modular tool archetype. |
| `scripts/generate_gui_atlas.py` | Rebuilds the 256x256 UI sprite atlas for windows, slots, progress bars, and tabs. | When UI elements or widget themes change. |
| `scripts/generate_station_assets.py` | Workstation blocks (Part Builder, Tinkering Table, Alloy Smelter) models & textures. | When adding new crafting tables or multiblock controllers. |

---

## 2. Palette & Shading Rules

Procedural textures use an 8-level or 6-level luminance ramp calculated from a primary hex code:
- `.`: Transparent
- `1`: Highlight (`r * 1.35 + 25`)
- `2`: Light (`r * 1.15 + 10`)
- `3`: Base Midtone (`r, g, b`)
- `4`: Shadow (`r * 0.68`)
- `5`: Outline / Deep Shadow (`r * 0.40`)
- `6`: Contact / Crevice (`r * 0.22`)

### Color Assignment Format:
In Stage 02, specify material colors as 24-bit RGB hex integers (e.g. `0xC4D6E2` for Aluminum, `0xE8C15A` for Electrum, `0x3C4856` for Steel).

---

## 3. Data Generation (Datagen)
NeoForge datagen providers are located in `io.github.gtbauke.modernmachines.datagen`:
- `ModBlockStateProvider`: Blockstates and block models.
- `ModItemModelProvider`: Item models.
- `ModRecipeProvider`: Crafting, smelting, and machine recipes.
- `ModBlockTagsProvider` & `ModItemTagsProvider`: Tag definitions.
- `ModLootTableProvider`: Block drops and custom loot tables.
