# Asset & Data Specification: Steam System (Stage 02 Output)

## 1. Asset Inventory & Status

All textures have been procedurally generated via `scripts/generate_steam_assets.py` into `src/main/resources/assets/modernmachines/textures/`:

### 1.1 Block Textures (`textures/block/`)
- `bronze_casing.png` (Riveted industrial bronze frame)
- `solid_fuel_boiler_front.png`, `solid_fuel_boiler_front_on.png` (Cast-iron firebox door, flame grate)
- `solid_fuel_boiler_side.png`, `solid_fuel_boiler_top.png`, `solid_fuel_boiler_bottom.png`
- `steam_turbine_front.png`, `steam_turbine_front_on.png` (Radial rotor housing & glowing active stator)
- `steam_turbine_side.png`, `steam_turbine_top.png`, `steam_turbine_bottom.png`
- `steam_crusher_front.png`, `steam_crusher_front_on.png` (Reinforced crusher jaws & kinetic sparks)
- `steam_crusher_side.png`, `steam_crusher_top.png` (Exhaust vent grating), `steam_crusher_bottom.png`
- `steam_alloy_smelter_front.png`, `steam_alloy_smelter_front_on.png` (Dual crucible hatch & molten glow)
- `steam_alloy_smelter_side.png`, `steam_alloy_smelter_top.png` (Exhaust vent grating), `steam_alloy_smelter_bottom.png`
- `bronze_fluid_tank.png`, `bronze_fluid_tank_top.png` (Sight-glass level gauge)
- `bronze_fluid_pipe.png` (Flanged bronze distribution pipe)

### 1.2 Item Textures (`textures/item/`)
- `steam_piston.png` (Pneumatic bronze piston with tin/copper actuator)
- `pressure_gauge.png` (Dial gauge with brass bezel and red needle)
- `bronze_valve.png` (Cross valve wheel with bronze spindle)
- `steam_bucket.png` (Pressurized encapsulated steam container)

### 1.3 Fluid Textures (`textures/fluid/`)
- `steam_still.png`, `steam_flow.png` (Translucent billowing vapor)

---

## 2. Localization Specification (`en_us.json`)

The following localization keys must be present in `src/generated/client/assets/modernmachines/lang/en_us.json` and registered via `ModLanguageProvider`:

```json
{
  "block.modernmachines.solid_fuel_boiler": "Solid Fuel Boiler",
  "block.modernmachines.steam_turbine": "Steam Turbine",
  "block.modernmachines.steam_crusher": "Steam Crusher",
  "block.modernmachines.steam_alloy_smelter": "Steam Alloy Smelter",
  "block.modernmachines.bronze_casing": "Bronze Machine Casing",
  "block.modernmachines.bronze_fluid_tank": "Bronze Fluid Tank",
  "block.modernmachines.bronze_fluid_pipe": "Bronze Fluid Pipe",

  "item.modernmachines.steam_piston": "Steam Piston",
  "item.modernmachines.pressure_gauge": "Pressure Gauge",
  "item.modernmachines.bronze_valve": "Bronze Valve",
  "item.modernmachines.steam_bucket": "Steam Bucket",

  "fluid_type.modernmachines.steam": "Steam",

  "container.modernmachines.solid_fuel_boiler": "Solid Fuel Boiler",
  "container.modernmachines.steam_turbine": "Steam Turbine",
  "container.modernmachines.steam_crusher": "Steam Crusher",
  "container.modernmachines.steam_alloy_smelter": "Steam Alloy Smelter",
  "container.modernmachines.bronze_fluid_tank": "Bronze Fluid Tank",

  "gui.modernmachines.temperature": "Temperature: %s °C",
  "gui.modernmachines.water_stored": "Water: %s / %s mB",
  "gui.modernmachines.steam_stored": "Steam: %s / %s mB",
  "gui.modernmachines.energy_generation": "Generating: %s FE/t",
  "gui.modernmachines.exhaust_clear": "Exhaust: Clear",
  "gui.modernmachines.exhaust_obstructed": "Exhaust Obstructed!",
  "gui.modernmachines.thermal_shock_warning": "Warning: Thermal Shock Hazard!"
}
```

---

## 3. Recipe Data & Datagen Specifications (`ModRecipeProvider`)

### 3.1 Shaped Crafting Recipes
1. **Bronze Machine Casing** (`modernmachines:crafting/bronze_casing`):
   - Pattern:
     ```
     PPP
     PSP
     PPP
     ```
   - Keys: `P` = `c:plates/bronze`, `S` = `minecraft:stone_bricks`
   - Result: 1x `modernmachines:bronze_casing`

2. **Steam Piston** (`modernmachines:crafting/steam_piston`):
   - Pattern:
     ```
      P 
     IRI
      G 
     ```
   - Keys: `P` = `c:plates/bronze`, `I` = `c:ingots/bronze`, `R` = `c:rods/tin`, `G` = `c:gears/bronze`
   - Result: 1x `modernmachines:steam_piston`

3. **Pressure Gauge** (`modernmachines:crafting/pressure_gauge`):
   - Pattern:
     ```
     PN
     RG
     ```
   - Keys: `P` = `c:plates/bronze`, `N` = `c:nuggets/bronze`, `R` = `minecraft:redstone`, `G` = `minecraft:glass_pane`
   - Result: 1x `modernmachines:pressure_gauge`

4. **Bronze Valve** (`modernmachines:crafting/bronze_valve`):
   - Pattern:
     ```
      N 
     ILI
      N 
     ```
   - Keys: `N` = `c:nuggets/bronze`, `I` = `c:ingots/bronze`, `L` = `minecraft:lever`
   - Result: 1x `modernmachines:bronze_valve`

5. **Bronze Fluid Pipe** (`modernmachines:crafting/bronze_fluid_pipe`):
   - Pattern:
     ```
     PPP
        
     PPP
     ```
   - Keys: `P` = `c:plates/bronze`
   - Result: 6x `modernmachines:bronze_fluid_pipe`

6. **Bronze Fluid Tank** (`modernmachines:crafting/bronze_fluid_tank`):
   - Pattern:
     ```
     PPP
     PGP
     PPP
     ```
   - Keys: `P` = `c:plates/bronze`, `G` = `minecraft:glass`
   - Result: 1x `modernmachines:bronze_fluid_tank`

7. **Solid Fuel Boiler** (`modernmachines:crafting/solid_fuel_boiler`):
   - Pattern:
     ```
     CCC
     MBM
     SSS
     ```
   - Keys: `C` = `c:plates/copper`, `M` = `c:plates/bronze`, `B` = `modernmachines:bronze_casing`, `S` = `minecraft:furnace`
   - Result: 1x `modernmachines:solid_fuel_boiler`

8. **Steam Turbine** (`modernmachines:crafting/steam_turbine`):
   - Pattern:
     ```
     PPP
     GBS
     RRR
     ```
   - Keys: `P` = `c:plates/bronze`, `G` = `c:gears/bronze`, `B` = `modernmachines:bronze_casing`, `S` = `modernmachines:steam_piston`, `R` = `minecraft:redstone`
   - Result: 1x `modernmachines:steam_turbine`

9. **Steam Crusher** (`modernmachines:crafting/steam_crusher`):
   - Pattern:
     ```
     F F
     SBS
     PPP
     ```
   - Keys: `F` = `minecraft:flint`, `S` = `modernmachines:steam_piston`, `B` = `modernmachines:bronze_casing`, `P` = `c:plates/bronze`
   - Result: 1x `modernmachines:steam_crusher`

10. **Steam Alloy Smelter** (`modernmachines:crafting/steam_alloy_smelter`):
    - Pattern:
      ```
      I I
      FBF
      PPP
      ```
    - Keys: `I` = `c:ingots/copper`, `F` = `minecraft:furnace`, `B` = `modernmachines:bronze_casing`, `P` = `c:plates/bronze`
    - Result: 1x `modernmachines:steam_alloy_smelter`

---

## 4. Machine Recipe Type Definitions

### 4.1 Crushing Recipe Schema (`modernmachines:crushing`)
- **JSON Structure**:
```json
{
  "type": "modernmachines:crushing",
  "ingredient": {
    "tag": "c:raw_materials/copper"
  },
  "result": {
    "count": 2,
    "id": "modernmachines:copper_dust"
  },
  "byproduct": {
    "chance": 0.25,
    "count": 1,
    "id": "minecraft:gold_nugget"
  },
  "steam": 4000,
  "duration": 200
}
```

### 4.2 Alloy Smelting Recipe Schema (`modernmachines:alloy_smelting`)
- **JSON Structure**:
```json
{
  "type": "modernmachines:alloy_smelting",
  "input_a": {
    "count": 3,
    "tag": "c:ingots/copper"
  },
  "input_b": {
    "count": 1,
    "tag": "c:ingots/tin"
  },
  "result": {
    "count": 4,
    "id": "modernmachines:bronze_ingot"
  },
  "steam": 5000,
  "duration": 200
}
```

---

## 5. Tag & Loot Table Specifications

### 5.1 Tags
- `BlockTags.MINEABLE_WITH_PICKAXE`: All steam blocks, casings, pipes, and tanks.
- `BlockTags.NEEDS_STONE_TOOL`: All steam blocks.
- `c:plates/bronze`, `c:ingots/bronze`, `c:gears/bronze`, `c:rods/tin`, `c:nuggets/bronze`.
- `c:fluids/steam` containing `modernmachines:steam_source` and `modernmachines:steam_flowing`.

### 5.2 Loot Tables
- All blocks use standard `dropSelf(Block)` via `ModBlockLootSubProvider`.

---

## 6. Review Gate & Next Phase Hand-Off
- [x] All 16x16 pixel-art PNG textures generated into `assets/modernmachines/textures/`.
- [x] Complete localization key mapping declared for `en_us.json`.
- [x] All 10 shaped crafting recipes mapped with precise grid matrices and ingredients.
- [x] Machine recipe JSON schemas defined for Crushing and Alloy Smelting.
- [x] BlockTags, ItemTags, and LootTable criteria documented.

> **Status**: Stage 02 is complete. Proceed to Stage 03 (`03_implementation`) to write the Java classes and block entity logic.
