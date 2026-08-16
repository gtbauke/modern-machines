# Modern Machines - Asset & Data Generation Scripts

This directory contains development and asset generation utility scripts for **Modern Machines**.

| Script | Purpose |
| :--- | :--- |
| `generate_all_assets.py` | Master asset generator pipeline for all blocks, items, and models. |
| `generate_gui_atlas.py` | Generates the 256x256 UI sprite atlas for Modern Machines windows, tabs, slots, buttons, gauges, and graphs. |
| `generate_part_templates.py` | Generates the 16x16 grayscale template textures for modular tool parts (heads, handles, bindings, attachments). |
| `generate_gear_screw_templates.py` | Generates the clockwork precision gear and diagonal slotted screw grayscale template sprites. |
| `generate_modular_tool_textures.py` | Assembles layered composite tool textures and patterns. |
| `generate_resource_textures.py` | Generates layered resource sprites (ingots, plates, dusts, wires, rods, nuggets, raw ores). |
| `generate_station_assets.py` | Generates workstation textures and model definitions for Part Builder, Tinkering Table, and Alloy Smelter. |
| `generate_terminal_assets.py` | Generates Engineer's Terminal and Tablet workstation sprites and models. |
| `generate_textures.py` | Baseline procedural texture generation utility. |
| `generate_tool_textures.py` | Tool item model texture generation utility. |
| `generate_modular_data.py` | Modular tool part data generation helper. |

---

### Usage

Run any script from the project root:
```bash
python scripts/generate_gui_atlas.py
python scripts/generate_all_assets.py
```
