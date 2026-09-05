# Low-Level Design & Implementation Plan: Subsurface Fluid Reservoirs & Pressurized Pockets

- **Specification Reference**: [`spec.md`](./spec.md)
- **Review Reference**: [`review.md`](./review.md)
- **Author**: Staff Software Engineer / LLD Architect Agent
- **Date**: 2026-09-05
- **Status**: Ready for Implementation

---

## 1. Architecture & Design Decisions

- **Design Pattern**: Decoupled Logical State vs. Physical Representation
- **Key Architectural Choices**:
  - **Logical Reservoir State (`ReservoirSavedData`)**: To address the severe risk of cross-chunk state sync and cascading block updates during fluid extraction, the reservoir's physical blocks act purely as WorldGen/hazard structures. The actual fluid volume and pressure are tracked logically via a global `SavedData` instance attached to the `ServerLevel`. Wellheads will interface strictly with this logical data.
  - **Bounded WorldGen**: To prevent WorldGen chunk cascading, the `SubsurfaceReservoirFeature` will strictly clamp its ellipsoid generation within the boundaries of the generating chunk (or use careful multi-chunk bounds logic limited to safely loaded areas).
  - **Tick Throttling**: The `WellheadBlockEntity` will limit `IFluidHandler` capability interactions and fluid pushing/pulling to once every 20 ticks (1 second) to prevent BlockEntity tick lag on large factory setups.
  - **Hazard Event Handling**: Hazard mechanics (Blowout, Flashover, Toxic Gas) will be managed via NeoForge event subscribers (e.g., `BlockEvent.BreakEvent`) instead of physical fluid block updates to prevent recursive explosion/eruption infinite loops and server crashes.

---

## 2. Data Model & Configuration

### 2.1 Logical Reservoir State
```java
// Data class tracking the logical state of a reservoir
public class ReservoirInstance {
    private final UUID id;
    private final BoundingBox bounds;
    private final ResourceLocation fluid;
    private final long maxVolume;
    private long currentVolume;
    private final float initialPressure;
    private float currentPressure;
    private long injectedVolume;

    // Getters, setters, and recalculatePressure() logic
}

// Global Level data manager
public class ReservoirSavedData extends SavedData {
    private final Map<UUID, ReservoirInstance> reservoirs;
    // Spatial index mapping ChunkPos or BoundingBox to Reservoir UUID
    
    public ReservoirInstance getReservoirAt(BlockPos pos) { /* ... */ }
    // load(), save(), etc.
}
```

### 2.2 JSON Configuration Schema (`config/reservoir/`)
```java
public record ReservoirConfig(
    ResourceLocation fluid,
    ResourceLocation barrierBlock,
    Optional<ResourceLocation> capstoneBlock,
    int minY,
    int maxY,
    int rarity,
    IntProvider radiusXz,
    IntProvider radiusY,
    float initialPressure,
    HazardConfig hazards
) {
    public static final Codec<ReservoirConfig> CODEC = /* ... */;
}

public record HazardConfig(
    boolean eruption,
    float eruptionVelocity,
    boolean flammable,
    int ignitionRadius,
    float explosionStrength,
    boolean toxic,
    int toxicDuration
) {
    public static final Codec<HazardConfig> CODEC = /* ... */;
}
```

---

## 3. Block, Entity & Hazard Mechanics

### 3.1 Blocks and BlockEntities
- **`DrillCasingBlock`**: A simple structural block. Placing it (or replacing a barrier block with it) safely seals the reservoir.
- **`WellheadBlock`**: Standard block with a BlockEntity.
- **`WellheadBlockEntity`**:
  - Implements NeoForge `IFluidHandler`.
  - Determines if it's in extraction or injection mode based on operation.
  - Interacts with `ReservoirSavedData` to decrement volume or increase `injectedVolume`.
  - Pushes/pulls fluids using `IItemHandler` / `IFluidHandler` to adjacent pipes, throttled to 20 ticks.

### 3.2 Hazard Event Subscribers
- **`BlockEvent.BreakEvent` Subscriber**:
  - Intercepts mining of `barrier_block` or `capstone_block`.
  - Checks if an active `DrillCasingBlock` is present.
  - If breached without casing:
    - Triggers Geyser (particle packets and fluid blocks spawned upwards).
    - Checks for adjacent open flames if `HazardConfig.flammable` is true -> Triggers `Level.explode(...)`.
    - Spawns AreaEffectCloud with toxic potion effects if `HazardConfig.toxic` is true.
  - Enforces a hard limit/cooldown on blowout events per chunk using a WeakHashMap or capability to prevent infinite loops.

---

## 4. WorldGen & VirtualDataPack

### 4.1 SubsurfaceReservoirFeature
- **`SubsurfaceReservoirFeature extends Feature<SubsurfaceReservoirConfiguration>`**:
  - Generates the fluid cavity and encasing barrier block shell.
  - Registers the generated bounding box and fluid type into `ReservoirSavedData` during generation.
  
### 4.2 VirtualDataPack Integration
- **`VirtualDataPack` Synthesis**:
  - Parses loaded `ReservoirConfig` objects at runtime.
  - Dynamically synthesizes `ConfiguredFeature`, `PlacedFeature`, and `BiomeModifier` entries based on the JSON configuration for seamless modpack integration.

---

## 5. Step-by-Step Implementation Task List

### Phase 1: Core Data Model & Configuration
- [x] **Task 1.1**: Create JSON configuration records (`ReservoirConfig`, `HazardConfig`) and codecs.
  - *Verification*: Run `./gradlew compileJava` and ensure codecs compile cleanly.
- [x] **Task 1.2**: Implement `ReservoirLoader` to parse JSON from `data/modernmachines/reservoirs/`.
  - *Verification*: Run `./gradlew test` with dummy JSON data.
- [x] **Task 1.3**: Implement `ReservoirInstance` and `ReservoirSavedData` for the `ServerLevel`.
  - *Verification*: Run `./gradlew test` to verify spatial indexing and serialization logic.

### Phase 2: Blocks & Capabilities
- [ ] **Task 2.1**: Implement and register `DrillCasingBlock`, `WellheadBlock`, `OilShaleBlock`, and `CapstoneBlock` in `ModBlocks`.
  - *Verification*: Run `./gradlew runData` to generate blockstates and models.
- [ ] **Task 2.2**: Implement `WellheadBlockEntity` with pressure formulas and tick throttling.
  - *Verification*: Run `./gradlew compileJava`.
- [ ] **Task 2.3**: Register NeoForge `IFluidHandler` capability for `WellheadBlockEntity`.
  - *Verification*: Run `./gradlew build` to ensure capability registration is correct.

### Phase 3: WorldGen & VirtualDataPack
- [ ] **Task 3.1**: Implement `SubsurfaceReservoirConfiguration` and `SubsurfaceReservoirFeature`.
  - *Verification*: Run `./gradlew compileJava`.
- [ ] **Task 3.2**: Add generation logic with strict chunk clamping and insertion into `ReservoirSavedData`.
  - *Verification*: Launch a test world (`./gradlew runClient`) and verify safe generation without cascading console warnings.
- [ ] **Task 3.3**: Integrate `ReservoirConfig` into `VirtualDataPack` to synthesize placed features/biome modifiers.
  - *Verification*: Check `.minecraft/logs/latest.log` during `./gradlew runClient` for VirtualDataPack registration success.

### Phase 4: Hazards & Secondary Recovery
- [ ] **Task 4.1**: Implement NeoForge event subscriber for `BlockEvent.BreakEvent` to handle breach hazards (blowouts, toxic clouds, flashovers).
  - *Verification*: Run `./gradlew compileJava`.
- [ ] **Task 4.2**: Implement hazard cooldowns and particle limits per chunk.
  - *Verification*: Test in-game via `./gradlew runClient` by mining barriers with and without casings near lava.
- [ ] **Task 4.3**: Finalize Wellhead injection logic (secondary recovery waterflooding) and pressure restabilization.
  - *Verification*: Run full build: `./gradlew build`.

### Phase 5: Polish & Validation
- [ ] **Task 5.1**: Ensure all annotations (e.g. `@SubscribeEvent`, nullability contracts) and braces follow `AGENTS.md` guidelines.
  - *Verification*: Manual review or custom linter script.
- [ ] **Task 5.2**: Test server/client synchronization and client particle rendering packets.
  - *Verification*: Run `./gradlew runServer` and connect a client to verify multiplayer capability.
