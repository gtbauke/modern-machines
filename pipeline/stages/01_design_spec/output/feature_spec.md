# Feature Specification: Steam System (Tier 1 Steam Era)

## 1. Executive Summary
- **Feature Name**: Steam System & Industrial Bronze Era
- **Category**: Power Generation, Thermodynamics, Fluid Logistics, and Mechanical Processing
- **Tier**: Tier 1 (Steam / Basic Bronze Era)
- **Primary Paradigm**: Dual Role Steam Ecosystem
  - **Direct Steam Processing**: Standalone Bronze-tier machines (Steam Crusher, Steam Alloy Smelter) run directly on steam fluid without electricity.
  - **Early Electricity Transition**: Steam Turbines convert steam into Forge Energy (FE) to bootstrap electric Tier 2 infrastructure.
  - **Thermodynamic Management**: Boilers require temperature management, fuel management, and careful water injection to avoid thermal shock hazards.

---

## 2. Registry Identifiers & Names

### 2.1 Blocks & BlockItems
| Registry Name | Class | Display Name | Tool / Mining Level | Hardness / Resistance |
| :--- | :--- | :--- | :--- | :--- |
| `modernmachines:solid_fuel_boiler` | `SolidFuelBoilerBlock` | Solid Fuel Boiler | Pickaxe / Stone | 3.5 / 6.0 |
| `modernmachines:steam_turbine` | `SteamTurbineBlock` | Steam Turbine | Pickaxe / Stone | 3.5 / 6.0 |
| `modernmachines:steam_crusher` | `SteamCrusherBlock` | Steam Crusher | Pickaxe / Stone | 3.5 / 6.0 |
| `modernmachines:steam_alloy_smelter` | `SteamAlloySmelterBlock` | Steam Alloy Smelter | Pickaxe / Stone | 3.5 / 6.0 |
| `modernmachines:bronze_casing` | `Block` | Bronze Machine Casing | Pickaxe / Stone | 3.0 / 5.0 |
| `modernmachines:bronze_fluid_tank` | `BronzeFluidTankBlock` | Bronze Fluid Tank | Pickaxe / Stone | 2.5 / 4.0 |
| `modernmachines:bronze_fluid_pipe` | `BronzeFluidPipeBlock` | Bronze Fluid Pipe | Pickaxe / Stone | 1.5 / 2.0 |

### 2.2 Items & Components
| Registry Name | Class | Display Name | Stack Size | Description |
| :--- | :--- | :--- | :--- | :--- |
| `modernmachines:steam_piston` | `Item` | Steam Piston | 64 | Bronze piston assembly for kinetic steam tools |
| `modernmachines:pressure_gauge` | `Item` | Pressure Gauge | 64 | Brass/Bronze dial measuring fluid pressure |
| `modernmachines:bronze_valve` | `Item` | Bronze Valve | 64 | Fluid flow regulator component |
| `modernmachines:steam_bucket` | `BucketItem` | Steam Bucket | 1 | Encapsulated steam container |

### 2.3 Fluids
| Fluid Name | Flowing / Source ID | Density | Viscosity | Temperature |
| :--- | :--- | :--- | :--- | :--- |
| `modernmachines:steam` | `modernmachines:steam_source`, `modernmachines:steam_flowing` | -100 (gaseous, flows upward) | 200 | 373 K (100°C) |

### 2.4 BlockEntities, Menus & Recipe Types
- **BlockEntities**:
  - `modernmachines:solid_fuel_boiler_be` (`SolidFuelBoilerBlockEntity`)
  - `modernmachines:steam_turbine_be` (`SteamTurbineBlockEntity`)
  - `modernmachines:steam_crusher_be` (`SteamCrusherBlockEntity`)
  - `modernmachines:steam_alloy_smelter_be` (`SteamAlloySmelterBlockEntity`)
  - `modernmachines:bronze_fluid_tank_be` (`BronzeFluidTankBlockEntity`)
  - `modernmachines:bronze_fluid_pipe_be` (`BronzeFluidPipeBlockEntity`)
- **Container Menus**:
  - `modernmachines:solid_fuel_boiler_menu`
  - `modernmachines:steam_turbine_menu`
  - `modernmachines:steam_crusher_menu`
  - `modernmachines:steam_alloy_smelter_menu`
- **Recipe Types**:
  - `modernmachines:crushing` (`CrushingRecipe`)
  - `modernmachines:alloy_smelting` (`AlloySmeltingRecipe`)

---

## 3. Thermodynamics, Ratios & Energy Balance

### 3.1 Fluid & Energy Conversion Ratios
- **Fluid Conversion Ratio**: $1\text{ mB Water} = 1\text{ mB Steam}$ (Direct 1:1 volumetric balance).
- **Turbine Power Generation**: $1\text{ mB Steam} = 1\text{ FE}$.
  - Peak Turbine Intake: $40\text{ mB/t Steam} \rightarrow 40\text{ FE/t Generation}$.
  - Internal Energy Buffer: $20,000\text{ FE}$.
- **Direct Steam Machine Consumption**:
  - Steam Crusher: $20\text{ mB/t Steam}$ during operation ($200\text{ ticks} \rightarrow 4,000\text{ mB Steam per recipe}$).
  - Steam Alloy Smelter: $25\text{ mB/t Steam}$ during operation ($200\text{ ticks} \rightarrow 5,000\text{ mB Steam per recipe}$).

### 3.2 Boiler Thermodynamic Curve
- **Temperature Range**: $20^\circ\text{C}$ (ambient) to $500^\circ\text{C}$ (maximum operating temperature).
- **Boiling Point**: $100^\circ\text{C}$.
- **Heating Behavior**:
  - While fuel burns, temperature rises by $+0.5^\circ\text{C}$ per tick until reaching $500^\circ\text{C}$.
  - When idle (no fuel burning), temperature cools down by $-0.1^\circ\text{C}$ per tick toward ambient ($20^\circ\text{C}$).
- **Steam Production Scale**:
  - $T < 100^\circ\text{C}$: $0\text{ mB/t}$ Steam (heating water only).
  - $100^\circ\text{C} \le T < 250^\circ\text{C}$: $10\text{ mB/t}$ Steam (consumes $10\text{ mB/t}$ Water).
  - $250^\circ\text{C} \le T < 400^\circ\text{C}$: $25\text{ mB/t}$ Steam (consumes $25\text{ mB/t}$ Water).
  - $400^\circ\text{C} \le T \le 500^\circ\text{C}$: $40\text{ mB/t}$ Steam (consumes $40\text{ mB/t}$ Water).

---

## 4. Safety, Hazards & Atmospheric Exhaust

### 4.1 Thermal Shock Hazard (Explosion / Destructive Failure)
- **Condition**: Boiler temperature $T \ge 100^\circ\text{C}$, internal water tank is **completely dry ($0\text{ mB}$)**, and water is suddenly injected (via bucket, pipe, or automated input).
- **Consequence**:
  - Instant flash evaporation causes catastrophic thermal shock.
  - A small localized explosion occurs (power: $2.5$, breaks the boiler block, damages nearby entities and players, but does not crater deep terrain).
  - Emits steam cloud particles and loud burst sound.

### 4.2 Overpressure Venting
- **Condition**: Boiler internal Steam Tank is completely full ($4,000\text{ mB}$) and temperature is above $100^\circ\text{C}$.
- **Consequence**:
  - The boiler automatically vents excess steam safely into the atmosphere.
  - Produces top-face steam puff particles and an audible high-pressure kettle whistle sound.
  - No structural damage or explosion occurs.

### 4.3 Atmospheric Steam Machine Exhaust
- All direct steam machines (Steam Crusher, Steam Alloy Smelter) have an active **Exhaust Vent** on their top face.
- **Clearance Requirement**: The block directly above the exhaust face must either be Air, a Fluid Pipe, or a non-solid block.
- **Obstructed Penalty**: If the exhaust vent is blocked by a solid opaque block:
  - Machine processing stalls / pauses.
  - The GUI displays an `[EXHAUST OBSTRUCTED]` warning badge.
  - Once unblocked, operation resumes automatically with steam particle discharge.

---

## 5. Flexbox GUI Specifications (`ModularContainerScreen`)

All GUIs inherit from `ModularContainerScreen<T>` and construct declarative component trees using the 256x256 UI sprite atlas.

### 5.1 Solid Fuel Boiler GUI (`solid_fuel_boiler_screen`)
- **Dimensions**: $176 \times 166\text{ px}$
- **Layout Architecture**:
```
+-------------------------------------------------------------+
|                      Solid Fuel Boiler                      |
|                                                             |
|  [Water Tank]       [Thermodynamics]        [Steam Tank]   |
|   4,000 mB           Temp: 340°C             4,000 mB       |
|  +---------+         +------------+         +---------+     |
|  |  WATER  |         | Thermometer|         |  STEAM  |     |
|  |  GAUGE  |         |   [||||]   |         |  GAUGE  |     |
|  +---------+         +------------+         +---------+     |
|  [In]  [Out]         [ BurningFlame ]       [In]  [Out]     |
|  (Bucket)            [  Fuel Slot   ]       (Bucket)        |
|                                                             |
| ----------------------------------------------------------- |
| [ 3x9 Player Inventory ]                                    |
| [ 1x9 Hotbar           ]                                    |
+-------------------------------------------------------------+
```
- **Flexbox Tree**:
  - `Column`:
    - `Row` (justify: `SPACE_EVENLY`):
      - `Column` [Water Tank Gauge (16x52), `Row`(`SlotElement`(BucketIn), `SlotElement`(BucketOut))]
      - `Column` [Thermometer Gauge (12x40, showing 20-500°C), `BurningElement` (14x14 flame), `SlotElement` (Fuel)]
      - `Column` [Steam Tank Gauge (16x52), `Row`(`SlotElement`(BucketIn), `SlotElement`(BucketOut))]
    - `Spacer(8)`
    - `PlayerInventoryElement`

### 5.2 Steam Turbine GUI (`steam_turbine_screen`)
- **Dimensions**: $176 \times 166\text{ px}$
- **Flexbox Tree**:
  - `Column`:
    - `Row` (justify: `SPACE_BETWEEN`):
      - `Column` [Steam Tank Gauge (16x52), `SlotElement`(BucketIn)]
      - `Column` [Turbine Rotor Animation Widget (32x32 rotating blades), Text: "40 FE/t"]
      - `Column` [Energy Bar Gauge (16x52, 20k FE), `SlotElement`(ChargeSlot)]
    - `Spacer(8)`
    - `PlayerInventoryElement`

### 5.3 Steam Crusher & Steam Alloy Smelter GUIs
- **Dimensions**: $176 \times 166\text{ px}$
- **Flexbox Tree**:
  - `Row`:
    - `Column` [Steam Gauge (16x52), Exhaust Status Indicator Icon]
    - `Column` (flexGrow: 1):
      - `Row` (justify: `CENTER`, align: `CENTER`):
        - Inputs: `SlotElement(Input 1)` (+ `SlotElement(Input 2)` for Alloy Smelter)
        - `ProgressBarElement` (Progress Arrow, atlas uv: 36, 0)
        - Outputs: `SlotElement(Primary Output)` + `SlotElement(Byproduct Output)` (Crusher)
      - `Spacer(10)`
      - `PlayerInventoryElement`
  - `SideTabElement` [Side Config Window Toggle]

---

## 6. Logistics & Storage

### 6.1 Bronze Fluid Tank (`bronze_fluid_tank`)
- **Capacity**: $16,000\text{ mB}$ (16 Buckets) of any single fluid (Water, Steam, Lava, etc.).
- **Auto-Output**: Configurable bottom face auto-drain.
- **Stacking**: Placing tanks vertically auto-connects them into a single multiblock fluid column.

### 6.2 Bronze Fluid Pipe (`bronze_fluid_pipe`)
- **Transfer Rate**: $100\text{ mB/t}$ per connection.
- **Heat Rating**: Rated for high-temperature fluids and gaseous steam without melting.

---

## 7. Recipe Matrix & Progression

### 7.1 Component Crafting Recipes
| Result Item | Crafting Grid Shape | Ingredients |
| :--- | :--- | :--- |
| `bronze_casing` | 3x3 Border | 8x Bronze Plate, 1x Stone Bricks (center) |
| `steam_piston` | 3x3 Vertical | Top: Bronze Plate; Mid: Bronze Ingot, Tin Rod; Bottom: Bronze Gear |
| `pressure_gauge` | 2x2 | 1x Bronze Plate, 1x Redstone Dust, 1x Glass Pane, 1x Bronze Nugget |
| `bronze_valve` | Cross | Top/Bottom: Bronze Nugget; Left/Right: Bronze Ingot; Center: Iron Lever |
| `bronze_fluid_pipe` (x6) | 3x3 Horizontal | Top/Bottom: 3x Bronze Plate; Center: Empty |
| `bronze_fluid_tank` | 3x3 Hollow | 8x Bronze Plate, Center: Glass |

### 7.2 Machine Crafting Recipes
| Result Machine | Ingredients |
| :--- | :--- |
| `solid_fuel_boiler` | Top: 3x Copper Plate; Mid: Bronze Casing, Furnace; Bottom: 3x Stone Bricks |
| `steam_turbine` | Top: 3x Bronze Plate; Mid: Bronze Gear, Steam Piston, Bronze Casing; Bottom: 3x Redstone |
| `steam_crusher` | Top: 2x Flint/Piston; Mid: Bronze Casing, Steam Piston; Bottom: 3x Bronze Plate |
| `steam_alloy_smelter` | Top: 2x Copper Ingot; Mid: Bronze Casing, 2x Furnace; Bottom: 3x Bronze Plate |

### 7.3 Processing Recipes (Examples)
- **Steam Crushing** ($200\text{ ticks}$, $20\text{ mB/t Steam}$):
  - 1x `raw_copper` $\rightarrow$ 2x `copper_dust` ($100\%$) + 1x `gold_nugget` ($25\%$).
  - 1x `raw_iron` $\rightarrow$ 2x `iron_dust` ($100\%$) + 1x `nickel_dust` ($25\%$).
  - 1x `raw_tin` $\rightarrow$ 2x `tin_dust` ($100\%$) + 1x `aluminum_dust` ($20\%$).
- **Steam Alloy Smelting** ($200\text{ ticks}$, $25\text{ mB/t Steam}$):
  - 3x `copper_ingot` + 1x `tin_ingot` $\rightarrow$ 4x `bronze_ingot`.
  - 2x `iron_ingot` + 1x `nickel_ingot` $\rightarrow$ 3x `invar_ingot`.
  - 1x `copper_ingot` + 1x `nickel_ingot` $\rightarrow$ 2x `constantan_ingot`.

---

## 8. Stage 01 Exit Criteria & Review Gate Checklist
- [x] Unambiguous snake_case registry identifiers defined for all blocks, items, block entities, menus, fluids, and recipe types.
- [x] Mathematical energy, thermal, and fluid rates documented and balanced against `pipeline/_config/balance_progression.md`.
- [x] Hazard and exhaust mechanics clearly specified (Thermal shock explosion & atmospheric venting).
- [x] Flexbox GUI layouts specified for all 4 machines with telemetry gauges and player inventory integration.
- [x] Full recipe progression tree mapped from raw resources to Bronze Era machines.

> **Review Gate**: Stage 01 is now complete and approved for human inspection. Proceed to Stage 02 (`02_data_and_assets`) to begin texture generation, localization, and recipe datagen specs.
