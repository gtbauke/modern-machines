# Feature Specification: JSON-Configurable Ore Generation Condition System

- **Status**: Approved (Reviewed)
- **Author**: Antigravity (Feature Planner)
- **Date**: 2026-09-05
- **Target Release**: Modern Machines v1.0.0 (NeoForge 1.21+)

---

## 1. Overview & Problem Statement

### 1.1 Summary
The **Ore Generation Condition System** enables mod authors, modpack creators, and server administrators to declaratively configure rich, multi-layered conditions for ore generation in `Modern Machines`. Ore rules are configured within material JSON definitions (and config overrides) and translated at runtime by the `VirtualDataPack` into native NeoForge/Minecraft worldgen registries (Configured Features, Placed Features, Placement Modifiers, and Biome Modifiers).

### 1.2 Problem Statement
Currently, Modern Machines uses a rigid, hardcoded ore generation schema partitioned strictly into three dimensions (`overworld`, `nether`, `end`) with static biome modifiers (`#minecraft:is_overworld`, etc.) and default stone/deepslate target replacements. This limits customization:
- Custom modded dimensions (e.g., The Aether, Twilight Forest) cannot be targeted.
- Ores cannot be restricted to specific biomes (e.g., mountains, oceans, deserts) or biome tags.
- Custom target replaceables (e.g., terracotta in Badlands, blackstone in Nether, end stone, custom mod stones) cannot be specified per rule.
- Advanced placement modifiers (air exposure discard chance, count distributions, environment/structure constraints) are inaccessible.

### 1.3 Goals
- **Declarative JSON Schema**: Support rich, extensible ore generation rules directly in material JSON configuration files.
- **Multi-Dimension & Biome Filtering**: Target any vanilla or modded dimension and filter biomes using ID lists, tag keys (`#minecraft:is_mountain`), and climate filters.
- **Flexible Replaceable Targets**: Allow arbitrary target blocks or block tags per ore rule.
- **Advanced Placement & Environmental Constraints**: Support height ranges (uniform/trapezoid/surface relative), vein size, count/rarity, air exposure discard, and safe 1-block proximity conditions.
- **Custom Modifiers**: Register lightweight `DimensionFilterPlacementModifier` and `AdjacentBlockPlacementModifier` for native placement filtering.
- **Logical Composition**: Allow `and`, `or`, and `not` condition combinators for biome and dimension filtering.
- **Seamless Datapack Synthesis**: Synthesize valid NeoForge Biome Modifiers and Vanilla Placed/Configured Features dynamically in `VirtualDataPack`.
- **100% Backward Compatibility**: Preserve full support for existing legacy material JSONs using the simple `overworld`/`nether`/`end` format.

### 1.4 Non-Goals
- In-game graphical UI for editing ore generation rules live (deferred to future config GUI).
- Live world regeneration / retro-gen of previously generated chunks.

---

## 2. User Personas & User Stories

### 2.1 Personas
- **Modpack Creator / Tweaker**: Wants to fine-tune resource progression (e.g., Titanium only spawns in mountain biomes above Y=90, or Uranium only in the Nether/deep depths with low air exposure).
- **Mod Developer**: Wants to add new materials with complex generation rules (e.g., dimensional exclusivity, specific target blocks) without writing custom Java worldgen feature code.
- **Server Administrator**: Wants to adjust ore vein sizes and frequencies to balance multiplayer economy.

### 2.2 User Stories
- *As a modpack creator, I want to restrict Platinum ore generation to Mountain and Peak biomes in the Overworld, so that players must explore high altitudes to progress.*
- *As a modpack author, I want to generate Tungsten ore inside a custom mod dimension replacing specific stone types, so that it integrates naturally with dimension mods.*
- *As a designer, I want ores to discard when exposed to air in open caves, so that players are encouraged to strip mine or cave dive strategically.*
- *As an existing user, I want my existing material JSON configurations to continue working without breaking changes.*

---

## 3. Functional Requirements (FRs)

### 3.1 Schema & Configuration Model (FR-1)
- The `ore_generation` object in `CustomMaterialConfig` must support two interchangeable formats:
  1. **Legacy Format**: `{ "enabled": true, "overworld": { ... }, "nether": { ... }, "end": { ... } }`
  2. **Rule-Based Format**: `{ "enabled": true, "rules": [ { ... rule definition ... } ] }`
- If both or either are provided, the loader must cleanly parse rules into a unified `OreGenRule` list.
- Parameter validation: `vein_size` clamped to `[1, 64]`, `veins_per_chunk` clamped to `[1, 256]`, air discard chance clamped to `[0.0, 1.0]`.

### 3.2 Dimension Conditions (FR-2)
- Each rule must support dimension matching:
  - `dimension`: Single dimension ID (`minecraft:overworld`, `aether:the_aether`)
  - `dimensions`: List of dimension IDs (`["minecraft:overworld", "minecraft:the_nether"]`)
  - `dimension_tag`: Dimension type tag (e.g., `#minecraft:is_overworld`)
  - `dimension_blacklist`: Exclusion list of dimension IDs
- Implemented via a custom `DimensionFilterPlacementModifier` codec (`modernmachines:dimension_filter`) to check `level.getLevel().dimension()`.

### 3.3 Biome Conditions (FR-3)
- Each rule must support biome matching:
  - `biome`: Specific biome ID (`minecraft:badlands`)
  - `biomes`: List of biome IDs (`["minecraft:plains", "minecraft:meadow"]`)
  - `biome_tag`: Biome tag (`#minecraft:is_mountain`, `#c:is_sandy`)
  - `biome_tags`: List of biome tags
  - `biome_blacklist`: Excluded biome IDs or tags
- Biome selectors are combined into NeoForge Biome Modifier `HolderSet<Biome>` definitions.

### 3.4 Target Replaceables & State Rules (FR-4)
- Each rule must define one or more target replacement definitions:
  - `target_type`: `tag_match` (default), `block_match`, `blockstate_match`, or `random_block_match`
  - `target`: Tag or block identifier (e.g., `minecraft:stone_ore_replaceables`, `minecraft:terracotta`, `minecraft:netherrack`, `minecraft:end_stone`)
  - `ore_form`: Which material form to place (e.g. `ore`, `deepslate_ore`, `netherrack_ore`, `end_stone_ore`) or explicit block ID override
- Multi-target support per rule (e.g., replace stone with standard ore AND deepslate with deepslate ore in the same height transition).

### 3.5 Height, Distribution & Placement Modifiers (FR-5)
- Each rule must specify:
  - `vein_size`: Number of ore blocks per vein (integer 1-64).
  - `veins_per_chunk`: Number of attempts per chunk (integer 1-256) or `rarity` (1 in N chunks).
  - `distribution`: `"uniform"`, `"triangle"`, or `"trapezoid"`.
  - `min_y` & `max_y`: Height boundaries, supporting absolute coordinates (`-64` to `320`).
  - `discard_chance_on_air_exposure`: Float from `0.0` (never discard) to `1.0` (always discard if air-adjacent).

### 3.6 Advanced Environmental & Logical Conditions (FR-6)
- Conditional constraints:
  - `required_mod`: Ore rule only active if specified mod ID is loaded (`FMLLoader.getLoadingModList().getModFileById(modId) != null`).
  - `adjacent_to_block`: Optional 1-block adjacency check (e.g., `"adjacent_to_block": "minecraft:lava"`), handled by `AdjacentBlockPlacementModifier` within safe chunk bounds.
  - Logical combinators: `and`, `or`, `not` for biome and dimension filters (max nesting depth 4).

### 3.7 Virtual Datapack & NeoForge Biome Modifier Synthesis (FR-7)
- `VirtualDataPack` must generate:
  - `data/<mod_id>/worldgen/configured_feature/ore_<name>_<rule_idx>.json`
  - `data/<mod_id>/worldgen/placed_feature/ore_<name>_<rule_idx>_placed.json`
  - `data/<mod_id>/neoforge/biome_modifier/add_ore_<name>_<rule_idx>.json`
- Feature IDs must use deterministic sequential naming (`ore_<name>_<index>`).

---

## 4. Non-Functional Requirements (NFRs)

- **Worldgen Performance**:
  - Evaluation of placement rules runs natively inside Minecraft's multi-threaded chunk generation pipeline using registered `PlacementModifier` codecs.
  - Adjacency checks are limited strictly to distance = 1 to prevent chunk cascading.
- **Fail-Safe Parsing & Validation**:
  - Malformed condition JSON logs descriptive warnings specifying the material name, rule index, and invalid field, without aborting game launch.
  - Defaults are automatically populated when optional fields are omitted.
- **Memory & Resource Efficiency**:
  - Dynamic JSON resources are synthesized once during datapack loading (`AddPackFindersEvent`).
- **Code Standards**:
  - Strict compliance with `AGENTS.md` (mandatory braces, empty lines after code blocks, `var` usage for locals, `@Override`/`@Nullable` annotations).

---

## 5. Integration & Existing System Context

### 5.1 Mod Architecture Context
```
┌─────────────────────────────────────────────────────────────┐
│ Material JSONs (src/main/resources & config/modernmachines) │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ CustomMaterialLoader & OreGenRule Parser                    │
│ - Parses legacy OreGenConfig or new OreGenRule array        │
│ - Clamps and validates numerical ranges and mod IDs         │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ VirtualDataPack / VirtualPackResources                      │
│ - Synthesizes ConfiguredFeatures (minecraft:ore)            │
│ - Synthesizes PlacedFeatures (DimensionFilter, Height, etc) │
│ - Synthesizes NeoForge BiomeModifiers (neoforge:add_features)│
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│ NeoForge Worldgen Registries (Server Data Pack Lifecycle)    │
│ - ModPlacementModifiers (DimensionFilter, AdjacentBlock)    │
└──────────────────────────────┬──────────────────────────────┘
```

### 5.2 Affected Subsystems
- `io.github.gtbauke.modernmachines.config.material`:
  - New classes: `OreGenRule`, `OreCondition`, `OreTargetConfig`, `BiomeFilterConfig`, `HeightRangeConfig`.
  - Updated classes: `CustomMaterialConfig`, `OreGenConfig`, `DimensionOreConfig`, `CustomMaterialLoader`.
- `io.github.gtbauke.modernmachines.core.registry`:
  - `ModPlacementModifiers`: Registers custom placement modifier types (`dimension_filter`, `adjacent_block`).
- `io.github.gtbauke.modernmachines.data`:
  - Updated `VirtualDataPack` worldgen generation methods.

---

## 6. Scope & Boundaries

| In Scope | Out of Scope (Future Work) |
| :--- | :--- |
| • JSON schema supporting lists of `OreGenRule`s per material | • In-game GUI config editor |
| • Biome filtering (IDs, tags, blacklists) | • Retroactive generation in existing chunks |
| • Dimension targeting via `DimensionFilterPlacementModifier` | • Custom noise-based vein spline generation |
| • Target replaceable block/tag specifications | • Dynamic weather-based generation triggers |
| • Height range (uniform/trapezoid/triangle) & air discard | |
| • Safe 1-block adjacency check (`adjacent_to_block`) | |
| • Full backward compatibility with legacy material JSONs | |
| • Virtual datapack synthesis & validation logging | |

---

## 7. Acceptance Criteria

- [ ] **Scenario 1: Legacy Compatibility**:
  - *Given* an existing material JSON using legacy `overworld`/`nether`/`end` format,
  - *When* the game loads,
  - *Then* it generates valid configured/placed features and biome modifiers identically to previous versions.

- [ ] **Scenario 2: Dimension & Biome Tag Filtering**:
  - *Given* a material rule targeting dimension `minecraft:overworld` and biome tag `#minecraft:is_mountain`,
  - *When* chunks generate,
  - *Then* ore veins generate exclusively within mountain biomes and nowhere else in the Overworld.

- [ ] **Scenario 3: Custom Dimension & Target Replaceable**:
  - *Given* a material rule targeting dimension `aether:the_aether` with replaceable block `aether:holystone`,
  - *When* the chunk generator runs in the Aether,
  - *Then* holystone is properly replaced by the configured material ore block.

- [ ] **Scenario 4: Air Exposure Discard**:
  - *Given* a rule with `discard_chance_on_air_exposure: 0.8`,
  - *When* veins generate adjacent to open air/caves,
  - *Then* exposed ore blocks have an 80% discard rate while unexposed blocks generate at 100%.

- [ ] **Scenario 5: Graceful Error Handling**:
  - *Given* an invalid or misspelled biome ID in a material JSON,
  - *When* materials are loaded at startup,
  - *Then* a warning is logged to the console identifying the offending material and rule, and the remaining valid rules load successfully.
