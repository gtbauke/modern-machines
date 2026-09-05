# Low-Level Design & Implementation Plan: Resource-Specific Large Ore Veins

- **Specification Reference**: [`spec.md`](./spec.md)
- **Review Reference**: [`review.md`](./review.md)
- **Author**: Staff Software Engineer & Technical Lead
- **Date**: 2026-09-05
- **Status**: Ready for Implementation

---

## 1. Architecture & Design Decisions

### 1.1 Chunk-Bounded Noise Intersection (Preventing Cascading WorldGen)
To address the severe risk of cascading chunk generation highlighted in the Risk Review, the generation logic will use a **global deterministic 3D noise evaluation** approach. 
Instead of selecting an origin and attempting to place blocks outward (which crosses chunk boundaries), the `LargeOreVeinFeature` will iterate exclusively over the `16x16` area of the currently generating chunk (`FeaturePlaceContext`). For each block within the configured Y-range, it will evaluate a globally seeded 3D noise function (e.g., `NormalNoise` or `SimplexNoise`). If the noise value falls within the defined density threshold, the block is considered part of the vein.

### 1.2 Safe Block Replacement
To avoid overwriting structures, bedrock, or fluids:
- A new block tag `#modernmachines:large_vein_replaceable` will be created (including stone, deepslate, netherrack, end_stone, tuff, granite, etc.).
- The `LargeOreVeinFeature` will strictly check if the current block state belongs to this tag or matches `BlockTags.BASE_STONE_OVERWORLD` before applying any replacement. Air and water are explicitly skipped.

### 1.3 Probability Resolution
Block placement within the vein volume will follow a sequential probability roll to ensure the total chance never exceeds 100%:
1. Roll `raw_block_chance` -> Place `RAW_STORAGE_BLOCK`.
2. Else, roll `secondary_ore_chance` -> Place secondary material ore.
3. Else, roll `primary_ore_chance` -> Place primary material ore (resolving to stone/deepslate variants based on height).
4. Else -> Place `filler_block`.

### 1.4 Surface Indicators
Surface indicator generation will also be strictly bounded to the generating chunk. We will randomly select 1-4 X/Z coordinates within the chunk, find the `WORLD_SURFACE_WG` height, and place the indicator block if the surface is valid (e.g., grass, dirt, stone, sand) and the block above is air.

---

## 2. Data Models & Configuration Changes

### 2.1 JSON Configuration Records
New record classes will be added to `io.github.gtbauke.modernmachines.config.material`:

```java
package io.github.gtbauke.modernmachines.config.material;

public record VeinNoiseConfig(
        int length,
        int thickness,
        float density
) {}

public record SurfaceIndicatorConfig(
        boolean enabled,
        String block,
        float chance
) {}

public record LargeOreVeinConfig(
        boolean enabled,
        java.util.List<String> dimensions,
        java.util.List<String> biome_tags,
        int min_y,
        int max_y,
        int rarity,
        String filler_block,
        float primary_ore_chance,
        float raw_block_chance,
        org.jspecify.annotations.Nullable String secondary_material,
        float secondary_ore_chance,
        SurfaceIndicatorConfig surface_indicators,
        VeinNoiseConfig noise
) {}
```

- Update `OreGenConfig` to include `List<LargeOreVeinConfig> large_veins`.
- Implement validation and clamping in `CustomMaterialLoader` (e.g., clamping `rarity` to > 0).

---

## 3. Core WorldGen Feature (`io.github.gtbauke.modernmachines.worldgen.feature`)

### 3.1 Feature & Configuration
- **`LargeOreVeinConfiguration`**: Implements `FeatureConfiguration`. Contains all properties needed during generation. Requires a `Codec` and `MapCodec` for NeoForge registration.
- **`LargeOreVeinFeature`**: Extends `Feature<LargeOreVeinConfiguration>`. 
  - Overrides `place(FeaturePlaceContext<LargeOreVeinConfiguration> context)`.
  - Instantiates a deterministic `NormalNoise` using `context.level().getSeed()`.
  - Iterates `x` and `z` from `0` to `15` within the chunk bounding box, and `y` between `min_y` and `max_y`.

### 3.2 Feature Registration
- Register in `io.github.gtbauke.modernmachines.core.registry.ModFeatures` using NeoForge's `DeferredRegister<Feature<?>>`.

---

## 4. Virtual DataPack Synthesis (`io.github.gtbauke.modernmachines.data.VirtualDataPack`)

`VirtualDataPack.java` will be updated to synthesize JSONs for large veins:
- Generate `worldgen/configured_feature/large_ore_vein_<material>_<index>.json` using `modernmachines:large_ore_vein`.
- Generate `worldgen/placed_feature/large_ore_vein_<material>_<index>_placed.json` applying `RarityFilter`, `HeightRange`, `DimensionFilter`, and `BiomeFilter`.
- Generate `neoforge/biome_modifier/add_large_ore_vein_<material>_<index>.json`.

---

## 5. Step-by-Step Implementation Task List

### Phase 1: Configuration Models & Loaders
- [x] **Task 1.1**: Create `VeinNoiseConfig`, `SurfaceIndicatorConfig`, and `LargeOreVeinConfig` records in `config/material/`.
  - *Verification*: `./gradlew compileJava`
- [x] **Task 1.2**: Update `OreGenConfig` to include the `large_veins` list and update `CustomMaterialLoader` to safely parse and clamp config values.
  - *Verification*: `./gradlew test` (or ensure it compiles with `./gradlew compileJava`)

### Phase 2: Core Feature & Registry
- [ ] **Task 2.1**: Implement `LargeOreVeinConfiguration` with a proper `Codec` and `MapCodec`.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 2.2**: Implement `LargeOreVeinFeature` adhering strictly to chunk-bounded noise intersection logic, probability cascading, and safe block replacement.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 2.3**: Register `LargeOreVeinFeature` in `ModFeatures`.
  - *Verification*: `./gradlew compileJava`

### Phase 3: Virtual DataPack Synthesis
- [ ] **Task 3.1**: Update `VirtualDataPack` to generate `configured_feature` JSONs for each large vein entry.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 3.2**: Update `VirtualDataPack` to generate `placed_feature` JSONs with appropriate rarity and height placement rules.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 3.3**: Update `VirtualDataPack` to generate `biome_modifier` JSONs for NeoForge.
  - *Verification*: `./gradlew test` (or `./gradlew build`)

### Phase 4: Polish, Testing & Validation
- [ ] **Task 4.1**: Create the `#modernmachines:large_vein_replaceable` block tag in the datagen or virtual datapack.
  - *Verification*: `./gradlew build`
- [ ] **Task 4.2**: Test generation in-game to verify chunk borders are seamless and no cascading lag occurs. Run the full build suite.
  - *Verification*: `./gradlew build`

---

## 6. Code Standard Checklist
- [x] All control flow statements use braces `{ ... }`.
- [x] Empty lines immediately following code blocks.
- [x] `var` used for clear local variable declarations.
- [x] Early returns and guard clauses prioritized.
- [x] Correct nullability annotations applied (`@Nullable`, `@NonNull`).
