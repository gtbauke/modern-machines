# Low-Level Design & Implementation Plan: JSON-Configurable Ore Generation Condition System

- **Specification Reference**: [`spec.md`](./spec.md)
- **Review Reference**: [`review.md`](./review.md)
- **Author**: Staff Software Engineer & Technical Lead
- **Date**: 2026-09-05
- **Status**: Ready for Implementation

---

## 1. Architecture & Design Decisions
- **Design Pattern**: Data-Driven Worldgen via JSON Virtual Datapacks and Custom NeoForge Placement Modifiers.
- **Key Architectural Choices**:
  - **Custom Placement Modifiers over Pure Tags**: Vanilla biome tags cannot effectively restrict placement by dimension for shared biomes (e.g., plains in multiple dimensions). To resolve this High-Risk issue, we will implement a custom `DimensionFilterPlacementModifier`. This directly checks the level's dimension at placement time.
  - **Restricted Logical Combinators**: As identified in the review, translating arbitrary `or`/`not` rules into Vanilla `PlacedFeature`s is exponentially complex. Logical combinators will be restricted strictly to biome filters. NeoForge's `BiomeModifier` natively supports combinations of `HolderSet`s, allowing safe evaluation without feature duplication.
  - **Safe Adjacency Checks**: The `adjacent_to_block` condition will strictly enforce a 1-block distance check (`distance = 1`) and only inspect coordinates within the active `WorldGenRegion`. This mitigates the Medium Risk of chunk cascading lag.
  - **Graceful Parsing & Limits**: To address security and resource exhaustion risks, the custom Gson deserializer will clamp numeric values (e.g., `vein_size` to 64, `veins_per_chunk` to 256). Any deeply invalid rules will log a precise warning rather than aborting the mod load.
  - **Deterministic Naming**: `VirtualDataPack` will synthesize JSON files with strict index-based naming (`ore_<material>_rule_<idx>`) to prevent worldgen registry desync across server restarts.

---

## 2. Data Model & Configuration Classes (`io.github.gtbauke.modernmachines.config.material`)
### 2.1 Configuration Records (Java 21)
```java
public record OreTargetConfig(
    String targetType,
    String target,
    String oreForm
) {}

public record OreGenRule(
    boolean enabled,
    List<String> dimensions,
    List<String> dimensionBlacklist,
    List<String> biomes,
    List<String> biomeTags,
    List<String> biomeBlacklist,
    List<OreTargetConfig> targets,
    int veinSize,
    int veinsPerChunk,
    int rarity,
    String distribution,
    int minY,
    int maxY,
    float discardChanceOnAirExposure,
    String requiredMod,
    String adjacentToBlock
) {
    public OreGenRule {
        // Clamping logic here
        veinSize = Math.clamp(veinSize, 1, 64);
        veinsPerChunk = Math.clamp(veinsPerChunk, 1, 256);
        discardChanceOnAirExposure = Math.clamp(discardChanceOnAirExposure, 0.0f, 1.0f);
    }
}
```

### 2.2 Gson Parsing Layer
- A custom `TypeAdapter<OreGenRule>` will be implemented in `CustomMaterialLoader`.
- Legacy parsing logic will seamlessly map `overworld`, `nether`, and `end` entries into `OreGenRule` instances with `#minecraft:is_overworld`, `#minecraft:is_nether`, and `#minecraft:is_end` dimension/biome filters.

---

## 3. Registry & NeoForge Implementations (`io.github.gtbauke.modernmachines.worldgen`)
### 3.1 Custom Placement Modifiers
- `DimensionFilterPlacementModifier`
  - Codec fields: `List<ResourceLocation> allowed`, `List<ResourceLocation> denied`
  - Logic: Returns `Stream.of(pos)` if `level.getLevel().dimension().location()` matches the criteria, otherwise `Stream.empty()`.
- `AdjacentBlockPlacementModifier`
  - Codec fields: `ResourceLocation targetBlock`
  - Logic: Checks `level.getBlockState(pos.above())`, `.below()`, etc., yielding `pos` only if the target block is exactly adjacent.

### 3.2 Registry Integration
```java
// In ModPlacementModifiers.java
public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = ...;
public static final Supplier<PlacementModifierType<DimensionFilterPlacementModifier>> DIMENSION_FILTER = 
    PLACEMENT_MODIFIERS.register("dimension_filter", () -> typeConvert(DimensionFilterPlacementModifier.CODEC));
```

---

## 4. Datapack Synthesis (`io.github.gtbauke.modernmachines.data.VirtualDataPack`)
- **Configured Features**: Iterate `CustomMaterialLoader.getMaterials()`. Map `OreTargetConfig` to `OreConfiguration.targetStates`. Write to `data/modernmachines/worldgen/configured_feature/ore_<material>_rule_<idx>.json`.
- **Placed Features**: Assemble placement modifiers:
  1. `CountPlacement` or `RarityFilter`
  2. `HeightRangePlacement`
  3. `DimensionFilterPlacementModifier` (Custom)
  4. `AdjacentBlockPlacementModifier` (Custom)
  5. `BiomeFilter`
  Write to `data/modernmachines/worldgen/placed_feature/ore_<material>_rule_<idx>.json`.
- **Biome Modifiers**: Construct JSON using `neoforge:add_features`. Resolve `biome`, `biome_tag`, and `biome_blacklist` fields into standard NeoForge `HolderSet` conditions (e.g. `{"type": "neoforge:and", ...}`). Write to `data/modernmachines/neoforge/biome_modifier/add_ore_<material>_rule_<idx>.json`.

---

## 5. Step-by-Step Implementation Task List

### Phase 1: Configuration Models & Parsing
- [x] **Task 1.1**: Create `OreTargetConfig` and `OreGenRule` records with validation clamping.
  - *Verification*: `./gradlew compileJava`
- [x] **Task 1.2**: Update `CustomMaterialConfig` and `CustomMaterialLoader` to parse both legacy config blocks and the new `rules` list.
  - *Verification*: `./gradlew test` (Verify parsing of legacy and new JSON structures).
- [x] **Task 1.3**: Add custom Gson `TypeAdapter` logic to safely discard invalid rules and emit warnings without throwing.
  - *Verification*: `./gradlew compileJava`

### Phase 2: Placement Modifiers
- [ ] **Task 2.1**: Implement `DimensionFilterPlacementModifier` and its MapCodec in `io.github.gtbauke.modernmachines.worldgen.placement`.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 2.2**: Implement `AdjacentBlockPlacementModifier` and its MapCodec (strict 1-block adjacency).
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 2.3**: Register custom modifiers in `io.github.gtbauke.modernmachines.core.registry.ModPlacementModifiers`.
  - *Verification*: `./gradlew build`

### Phase 3: Datapack Synthesis
- [ ] **Task 3.1**: Extend `VirtualDataPack.java` to synthesize `ConfiguredFeature` JSON strings for each rule.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 3.2**: Extend `VirtualDataPack.java` to synthesize `PlacedFeature` JSON strings utilizing the new custom placement modifiers.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 3.3**: Extend `VirtualDataPack.java` to synthesize `BiomeModifier` JSON strings handling combinations of biome tags/IDs and `required_mod` checks (via `neoforge:mod_loaded` conditions).
  - *Verification*: `./gradlew compileJava`

### Phase 4: Validation & Polish
- [ ] **Task 4.1**: Run full compilation and checks: `./gradlew build`
- [ ] **Task 4.2**: Launch the client in dev environment (`./gradlew runClient`) and verify console logs show successful parsing and zero datapack syntax errors.
- [ ] **Task 4.3**: Update `AGENTS.md` or feature documentation with final configuration examples if necessary.
