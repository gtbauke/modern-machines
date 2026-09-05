# Feature Specification: Subsurface Fluid Reservoirs & Pressurized Pockets

- **Status**: Under Review
- **Author**: Technical Architect & WorldGen Specialist
- **Date**: 2026-09-05
- **Target Release**: Modern Machines WorldGen & Industrial Fluids Milestone

---

## 1. Overview & Problem Statement

### 1.1 Summary
Subsurface Fluid Reservoirs introduces physical 3D subterranean fluid pockets (such as Crude Oil, Natural Gas / Methane, Geothermal Brine, and Mineral Leachates) encased in specialized geological barrier rock (e.g., *Oil Shale*, *Salt Domes*, *Dolomitic Capstone*). Reservoirs feature realistic geomechanical pressure, hazardous eruptions upon uncontained breach, flammability hazards, toxic vapor dissipation, and an industrial extraction system featuring **Drill Casings**, **Wellheads**, and **Secondary Pressure Recovery** (waterflooding).

### 1.2 Problem
Currently, resource generation is strictly oriented around solid ore blocks and veins. Modern Machines lacks immersive subterranean fluid extraction mechanics, geological barrier layers, and environmental mining hazards that reward careful engineering and automated drilling over simple pickaxe mining.

### 1.3 Goals
- Provide fully JSON-configurable fluid reservoir generation (`data/modernmachines/reservoirs/<name>.json`).
- Implement chunk-safe, non-cascading 3D cavern generation with configurable fluid cavities and encasing rock shells.
- Introduce dynamic pressure and breach hazards:
  - High-pressure geyser eruptions.
  - Flammable gas ignition / explosion risks near torches, flames, and lava.
  - Toxic vapor clouds inflicting suffocation, nausea, and blindness.
- Implement industrial extraction apparatus:
  - **Drill Casing Block**: Seals the borehole to prevent blowout eruptions.
  - **Wellhead Block & BlockEntity**: Regulates pressure and interfaces with NeoForge `IFluidHandler` fluid pipes.
  - **Secondary Recovery (Waterflooding)**: Injection of pressurized water/gas to maintain extraction rates as native reservoir pressure depletes.

### 1.4 Non-Goals
- Full hydrodynamic CFD fluid simulation across open caverns (fluids use standard NeoForge/Minecraft fluid physics once settled).
- Destructive infinite block-corrosion mechanics that permanently ruin user base infrastructure.

---

## 2. User Personas & User Stories

### 2.1 Personas
- **Industrial Automation Engineer**: Wants reliable, high-yield subterranean fluid supply chains for power generation, petrochemical synthesis, and chemical refining.
- **Subterranean Explorer / Miner**: Wants realistic, tension-filled mining exploration where breaching unknown geological strata carries environmental risk.
- **Modpack Creator / Server Admin**: Wants to define custom fluid pockets (e.g. End Void Plasma, Nether Sulfur Magma, Custom Fluids) with custom dimensions, biomes, and pressure ratings via JSON.

### 2.2 User Stories
- *As a player exploring deep caverns*, I want to identify oil shale and capstones so I know a pressurized reservoir is nearby before accidentally triggering a dangerous blowout.
- *As a factory builder*, I want to construct a sealed Wellhead over drill casings so I can safely pump crude oil and natural gas directly into my refinery pipeline.
- *As a modpack author*, I want to configure reservoir rarity, fluid types, and hazardous behaviors in JSON without writing Java code.

---

## 3. Functional Requirements (FRs)

### FR-1: JSON Reservoir Data Configuration (`ReservoirConfig`)
- Defined in `data/modernmachines/reservoirs/<name>.json` (or virtual data packs):
  - `fluid`: Resource location string (e.g., `modernmachines:crude_oil`, `modernmachines:natural_gas`, `minecraft:water`, `minecraft:lava`).
  - `barrier_block`: Encasing rock block ID (e.g., `modernmachines:oil_shale`, `modernmachines:salt_dome_rock`, `minecraft:tuff`).
  - `capstone_block`: Optional top barrier block with high hardness (e.g., `modernmachines:dense_capstone`).
  - `dimensions`, `dimension_blacklist`, `biomes`, `biome_tags`, `biome_blacklist`.
  - `min_y`, `max_y`, `rarity` (1 in N chunks).
  - `radius_xz` (min/max horizontal radius, e.g. 6 to 14 blocks), `radius_y` (vertical height, e.g. 3 to 7 blocks).
  - `initial_pressure`: Float value between `1.0` (mild) and `10.0` (extreme).
  - `hazards`:
    - `eruption`: Boolean and eruption velocity multiplier.
    - `flammable`: Boolean, ignition radius (blocks), and explosion strength.
    - `toxic`: Boolean, cloud duration (seconds), and applied status effects.

### FR-2: WorldGen Feature (`SubsurfaceReservoirFeature`)
- Generates subterranean ellipsoidal fluid cavities strictly within the generating chunk bounds or deterministic multi-chunk continuous envelopes without causing chunk cascading.
- Carves out the interior volume, replaces replaceable stone/deepslate with the target fluid, and surrounds the perimeter with a 1-2 block thick `barrier_block` shell.
- Integrates into `VirtualDataPack` synthesizing `configured_feature`, `placed_feature`, and `neoforge:biome_modifier`.

### FR-3: Geomechanical Pressure & Breach Hazards
- **Blowout / Eruption Event**:
  - If a player mines a `barrier_block` adjacent to a pressurized fluid without an active `DrillCasingBlock` sealing the position, a blowout event triggers:
    - High-velocity upward fluid spray particles and sound effects.
    - Spawns pressurized fluid geysers that project fluid blocks upward through the mined hole.
- **Flammable Gas Flashover**:
  - If the reservoir fluid is flammable (e.g., Natural Gas, Crude Oil) and the breach occurs within 6 blocks of an open flame (torch, campfire, flint and steel, lava), an instant deflagration explosion (strength 2.0 to 4.0) is triggered with widespread fire.
- **Toxic Vapor Leak**:
  - Toxic reservoirs release an area-of-effect gas cloud that persists for 20-40 seconds, inflicting Nausea II, Slowness II, and Suffocation (damage over time) to entities without respirators/diving gear.

### FR-4: Safe Extraction via Drill Casing & Wellhead
- **Drill Casing (`DrillCasingBlock`)**:
  - Reinforced borehole pipe block. Mining barrier rock with a casing in hand or using an automatic casing hammer installs the casing safely without triggering a blowout.
- **Wellhead (`WellheadBlock` & `WellheadBlockEntity`)**:
  - Mounted atop the casing column.
  - Connects to the underlying fluid reservoir and exposes a standard NeoForge `IFluidHandler` capability on its lateral faces.
  - Tracks extraction metrics: remaining fluid volume, current reservoir pressure, and extraction flow rate.
  - Can be turned on/off with redstone signals.

### FR-5: Pressure Depletion & Secondary Recovery (Waterflooding)
- Natural reservoir pressure decreases logarithmically as fluid is pumped out:
  $$\text{FlowRate}(P) = \text{BaseRate} \times \left(\frac{P}{P_{\text{initial}}}\right)^{0.5}$$
- When pressure drops below critical threshold ($< 20\%$), natural extraction slows down significantly.
- **Water/Gas Injection**:
  - Players can pump pressurized water or gas into an auxiliary injection wellhead on the same reservoir to restore reservoir pressure and achieve near 100% fluid recovery.

---

## 4. Non-Functional Requirements (NFRs)

### 4.1 WorldGen & Performance
- Zero chunk cascading: all cavity voxel evaluations and barrier placements respect chunk boundaries.
- Fluid volume lookups must use spatial indexing / bounding box math rather than recursive block-by-block flood fills on every tick.

### 4.2 Network & Client Experience
- Blowout particle effects and geysers use lightweight client-side particle packets and renderers to prevent FPS drops.
- Wellhead state synchronization with clients throttled to delta updates.

### 4.3 Extensibility & Compatibility
- Full compatibility with vanilla fluids and third-party mod fluids registered in `BuiltInRegistries.FLUID`.
- Fully interoperable with standard NeoForge `IFluidHandler` pipes, pumps, and fluid tanks.

---

## 5. Integration & Existing System Context

- **Configuration Layer**:
  - New `ReservoirConfig` records in `io.github.gtbauke.modernmachines.config.reservoir`.
  - Loader integrated into `CustomMaterialLoader` or dedicated `ReservoirConfigLoader`.
- **WorldGen Layer**:
  - `SubsurfaceReservoirFeature` and `SubsurfaceReservoirConfiguration` registered in `ModFeatures`.
  - Placement modifiers and virtual data pack entries synthesized dynamically in `VirtualDataPack`.
- **Block & Entity Layer**:
  - `ModBlocks`: `WELLHEAD`, `DRILL_CASING`, `OIL_SHALE`, `SALT_DOME_ROCK`, `CAPSTONE`.
  - `ModBlockEntities`: `WellheadBlockEntity` with `IFluidHandler` capability registration in `ModBlockEntities.registerCapabilities`.

---

## 6. Scope & Boundaries

| In Scope | Out of Scope (Future Work) |
| :--- | :--- |
| • JSON data-driven reservoir configuration | • Seismic graphical core-sample scanner UI |
| • Chunk-safe 3D cavern & barrier generation | • Multi-chunk continuous geological fault line plates |
| • Eruption, flammability, and toxic breach hazards | • Destructive acid-corrosion terrain dissolution |
| • Wellhead, Drill Casing, and `IFluidHandler` extraction | • Offshore floating oil platform multiblock |
| • Pressure depletion & secondary water injection | • Infinite bedrock virtual fluid reservoirs |

---

## 7. Acceptance Criteria

- [ ] **AC-1 (JSON Parsing)**: Custom reservoir JSON files in `data/modernmachines/reservoirs/` are loaded, validated, and clamped without runtime crashes.
- [ ] **AC-2 (World Generation)**: Reservoirs generate below the configured $Y$-level with enclosed barrier shells and target fluid contents without chunk cascading.
- [ ] **AC-3 (Breach Hazards)**: Mining the barrier rock directly triggers geyser eruptions, gas explosions near torches, and toxic clouds.
- [ ] **AC-4 (Safe Extraction)**: Placing a Drill Casing and Wellhead allows continuous, leak-free pumping into fluid pipes via NeoForge `IFluidHandler`.
- [ ] **AC-5 (Pressure & Recovery)**: Extraction causes pressure decay, and pumping water into an injection well restores pressure.
