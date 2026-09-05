# Feature Specification: Resource-Specific Large Ore Veins

- **Status**: Draft
- **Author**: Antigravity (Feature Planner)
- **Date**: 2026-09-05
- **Target Release**: Modern Machines v1.0.0 (NeoForge 1.21+)

---

## 1. Overview & Problem Statement

### 1.1 Summary
The **Resource-Specific Large Ore Vein System** allows materials in `Modern Machines` to generate as massive, rare, 3D serpentine ore veins (similar to Vanilla's large Copper and Iron veins, but fully customizable per material). Each large vein is configured directly inside the material's JSON definition and features custom filler matrix blocks (e.g., granite, tuff, diorite, blackstone), embedded primary ores, secondary/byproduct ores, rare raw metal storage blocks, surface indicator markers, and fine-grained noise/density tuning.

### 1.2 Problem Statement
Standard ore generation produces small, discrete clusters scattered uniformly throughout the world. While suitable for basic mining, it lacks the excitement and logistical depth of locating and excavating massive mineral deposits:
- Players cannot discover massive, high-yield mineral veins.
- Tech progression lacks centralized mining hubs or quarry targets.
- Ores cannot naturally yield byproducts or thematic host matrix rocks (such as granite-embedded aluminum or tuff-embedded titanium).
- There is no surface exploration gameplay (such as finding surface indicator samples that reveal deep veins below).

### 1.3 Goals
- **Declarative Material JSON Configuration**: Configure large veins directly inside `CustomMaterialConfig` (`large_veins` array under `ore_generation`).
- **3D Serpentine Noise Geometry**: Generate authentic continuous 3D serpentine ribbon veins across custom height ranges.
- **Thematic Matrix / Filler Block**: Replace host stone with configurable filler blocks (e.g., granite, diorite, andesite, tuff, calcite, blackstone, basalt).
- **Multi-Resource Composition**:
  - Primary material ore (with automatic stone/deepslate/netherrack form resolution).
  - Optional secondary / byproduct ore with configurable mix ratio (e.g., sporadic nickel in iron veins, or silver in lead veins).
  - Optional raw metal storage block nodes (high-value dense cores).
- **Surface Indicators**: Optionally spawn surface sample blocks or pebbles above deep veins to reward overland exploration.
- **Performance & Safety**: Bound noise calculations to avoid chunk cascading, enforce sensible density clamping, and run seamlessly on NeoForge 1.21+ worldgen threading.

### 1.4 Non-Goals
- Replacing vanilla world noise generator pipeline completely (large veins will be implemented as modular `Feature<LargeOreVeinConfig>` placement features).
- In-game live vein editing GUI.

---

## 2. User Personas & User Stories

### 2.1 Personas
- **Modpack Creator**: Wants to design dedicated mining progression (e.g., massive Bauxite veins in mountains with granite host rock, or Uranium veins in deep dark depths with sporadic raw blocks).
- **Survival / Tech Player**: Wants the thrill of finding surface indicators, excavating a massive vein, and setting up automated mining infrastructure.
- **Mod Developer**: Wants to easily attach thematic large vein profiles to any new custom material via simple JSON.

### 2.2 User Stories
- *As a player exploring the surface, I want to find small copper/iron indicator stones on the terrain, so that I know a massive vein exists deep beneath.*
- *As a modpack author, I want to configure a Titanium large vein in Tuff host rock with sporadic Raw Titanium Blocks and Secondary Nickel ore, so that mining is varied and rewarding.*
- *As a server administrator, I want large vein generation to be lag-free and strictly confined within chunk generation bounds.*

---

## 3. Functional Requirements (FRs)

### 3.1 Large Vein Configuration Schema (FR-1)
- The `ore_generation` block in material JSONs must support an optional `large_veins` list:
  ```json
  "ore_generation": {
    "enabled": true,
    "large_veins": [
      {
        "enabled": true,
        "dimensions": ["minecraft:overworld"],
        "biome_tags": ["#minecraft:is_overworld"],
        "min_y": -32,
        "max_y": 32,
        "rarity": 24,
        "filler_block": "minecraft:granite",
        "primary_ore_chance": 0.35,
        "raw_block_chance": 0.05,
        "secondary_material": "nickel",
        "secondary_ore_chance": 0.10,
        "surface_indicators": {
          "enabled": true,
          "block": "modernmachines:raw_titanium_block",
          "chance": 0.6
        },
        "noise": {
          "length": 48,
          "thickness": 6,
          "density": 0.65
        }
      }
    ]
  }
  ```

### 3.2 3D Serpentine Vein Generation Feature (FR-2)
- Register a custom `LargeOreVeinFeature` (`Feature<LargeOreVeinConfiguration>`) with NeoForge:
  - Takes seed-derived 3D continuous Simplex/Perlin noise or parameterized 3D spline ribbons.
  - Spans a configurable horizontal length (e.g. 24–64 blocks) and vertical thickness (e.g. 4–12 blocks).
  - Replaces valid replaceable blocks (stone, deepslate, netherrack, etc.) with the configured `filler_block`.

### 3.3 Resource Placement & Density Probability (FR-3)
- Within the filler matrix:
  - If a block is inside the core vein volume:
    - Roll `raw_block_chance` (e.g., 5%): place material `RAW_STORAGE_BLOCK`.
    - Else roll `secondary_ore_chance` (e.g., 10%): place secondary material ore.
    - Else roll `primary_ore_chance` (e.g., 35%): place primary material ore (adapting to stone or deepslate based on target block / Y level).
    - Else: place `filler_block`.

### 3.4 Surface Indicators (FR-4)
- When `surface_indicators.enabled` is true:
  - For the origin column of the vein, find the top motion-blocking solid block (`Heightmap.Types.WORLD_SURFACE_WG`).
  - Place 1–4 indicator blocks (e.g., surface sample ore or raw nuggets/stone) on or near the surface.

### 3.5 Virtual Datapack Integration (FR-5)
- `VirtualDataPack` must synthesize:
  - `worldgen/configured_feature/large_ore_vein_<material>_<index>.json` (type `modernmachines:large_ore_vein`)
  - `worldgen/placed_feature/large_ore_vein_<material>_<index>_placed.json` (with `RarityFilter`, `HeightRange`, `DimensionFilter`, and `BiomeFilter`)
  - `neoforge/biome_modifier/add_large_ore_vein_<material>_<index>.json`

---

## 4. Non-Functional Requirements (NFRs)

- **Performance & Safety**:
  - Noise evaluation must be chunk-bounded (evaluating positions within `WorldGenRegion` safe bounds) with zero cross-chunk loading.
  - Rarity and size parameters must be validated and clamped (e.g., max length 128, max thickness 16).
- **Resilience & Graceful Fallback**:
  - If `secondary_material` is missing or invalid, log a warning and fall back to 100% primary ore without crashing.
  - If `filler_block` is omitted, default intelligently (e.g., `minecraft:granite` above Y=0, `minecraft:tuff` below Y=0).
- **Code Standards**:
  - Adherence to `AGENTS.md` (mandatory braces, empty lines after blocks, `var` for locals, `@Override`/`@Nullable` annotations).

---

## 5. Integration & Existing System Context

- `io.github.gtbauke.modernmachines.config.material`:
  - `LargeOreVeinConfig`, `SurfaceIndicatorConfig`, `VeinNoiseConfig` records.
  - Integration with `OreGenConfig` and `CustomMaterialLoader`.
- `io.github.gtbauke.modernmachines.worldgen.feature`:
  - `LargeOreVeinFeature`, `LargeOreVeinConfiguration`.
- `io.github.gtbauke.modernmachines.core.registry`:
  - `ModFeatures`: Register `large_ore_vein` feature and codec with `Registries.FEATURE`.
- `io.github.gtbauke.modernmachines.data`:
  - `VirtualDataPack`: Synthesize large vein configured features, placed features, and biome modifiers.

---

## 6. Scope & Boundaries

| In Scope | Out of Scope (Future Work) |
| :--- | :--- |
| • Declarative `large_veins` JSON schema in material configs | • In-game seismic scanner tool / HUD radar |
| • 3D serpentine vein feature (`LargeOreVeinFeature`) | • Dynamic seismic earthquakes around veins |
| • Matrix filler block replacement (granite, tuff, etc.) | • Custom dimension-wide fluid aquifers |
| • Primary, secondary byproduct, and raw storage block tiers | |
| • Surface indicator generation above vein nodes | |
| • Virtual datapack synthesis & registration | |

---

## 7. Acceptance Criteria

- [ ] **Scenario 1: Large Vein Generation**:
  - *Given* a material with `large_veins` configured with granite filler and 35% ore density,
  - *When* a world chunk generates matching the rarity filter,
  - *Then* a 3D serpentine deposit of granite containing ore and raw storage blocks generates in the specified Y range.

- [ ] **Scenario 2: Secondary Byproduct Ore**:
  - *Given* an Iron large vein configured with 10% secondary Nickel ore,
  - *When* the vein generates,
  - *Then* approximately 10% of ore nodes within the matrix place Nickel ore blocks.

- [ ] **Scenario 3: Surface Indicators**:
  - *Given* a large vein configured with `surface_indicators: { "enabled": true }`,
  - *When* the vein generates deep underground,
  - *Then* surface indicator sample blocks are placed on the terrain surface directly above the vein origin.

- [ ] **Scenario 4: Fallback & Clamping**:
  - *Given* a JSON with an un-registered secondary material name or out-of-range length,
  - *When* the config is parsed,
  - *Then* bounds are clamped safely, a warning is logged, and primary ore generation proceeds smoothly.
