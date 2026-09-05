# Feature Review & Risk Assessment: Subsurface Fluid Reservoirs & Pressurized Pockets

- **Specification Reference**: [`spec.md`](./spec.md)
- **Reviewer**: Principal Software Architect & WorldGen Specialist
- **Date**: 2026-09-05
- **Overall Assessment**: High Risk / Needs Refinement

---

## 1. Executive Summary
The Subsurface Fluid Reservoirs feature introduces an immersive, high-stakes subterranean fluid extraction system with industrial mechanics (wellheads, drill casings) and dynamic environmental hazards (blowouts, toxic vapor, explosions). While the design goals align excellently with the theme of Modern Machines, the current specification presents significant architectural and technical risks. Chief among these are the lack of clarity on cross-chunk reservoir state synchronization, the potential for catastrophic server lag from fluid block cascades, and the risk of chunk cascading during WorldGen. The specification requires critical refinements to the data model for tracking reservoir state (pressure and volume) independently from physical block updates, especially across unloaded chunks.

---

## 2. Scope Completeness & Missing Requirements
- **Omitted Edge Cases**:
  - **Unloaded Chunk Interactions**: If a reservoir spans multiple chunks and a player extracts fluid from a wellhead in a loaded chunk, how does the system handle fluid depletion in the unloaded chunks? 
  - **Multiple Wellheads on One Reservoir**: The spec does not clarify how multiple wellheads tapping the *same* physical reservoir share pressure and volume metrics, or how an injection well identifies its connected extraction wellhead.
  - **Dimension/Height Limits**: Edge cases where the calculated ellipsoidal cavity breaks through the bedrock layer or world ceiling are not addressed.
  - **Waterlogging**: Interaction of fluid blocks with waterloggable blocks (e.g., caves with fences/slabs) when generating or erupting.
- **Ambiguous Requirements**:
  - **Reservoir Identity**: How is a "reservoir" uniquely identified by the Wellhead if block-by-block flood fills are forbidden? The spec mandates "spatial indexing / bounding box math" but does not define where this index is stored (e.g., Level SavedData, Chunk Capability, etc.).
  - **Fluid Replacement Mechanics**: Does extracting fluid from the reservoir physical volume replace the fluid blocks with air, stone, or leave them intact while logically decrementing a virtual counter?
- **Missing Failure Modes**:
  - **Hazard Infinite Loops**: A flammable gas flashover near lava could ignite, destroy blocks, and trigger subsequent eruptions in a continuous loop, crashing the server.
  - **Particle/Entity Spam**: Uncapped geyser eruptions and toxic vapor clouds could spawn too many entities/particles, crippling client and server tick rates.

---

## 3. Technical & Architectural Risks
| Risk Area | Severity | Description & Potential Impact | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **State Sync (Cross-Chunk)** | High | Wellheads tracking pressure/volume independently will drift. If physical blocks dictate volume, accessing unloaded chunks will cause chunk loading lag spikes. | Decouple logical state from physical blocks. Use a global `SavedData` registry (e.g., `ReservoirManager`) mapping bounding boxes to logical `Reservoir` objects. Wellheads query this global state instead of physical blocks. |
| **WorldGen Cascading** | High | Generating multi-chunk cavities during standard chunk generation often triggers runaway chunk loading (cascading) if bounding boxes extend beyond the safe `+8` offset. | Use Jigsaw/Structure generation instead of standard `Feature` for multi-chunk reservoirs, or strictly clamp generation bounds to the generating chunk using `ChunkPos` bounds logic. |
| **Fluid Tick Cascades** | High | Replacing large numbers of air blocks with fluid (or vice versa during extraction/eruption) triggers block updates that can freeze the server due to vanilla fluid mechanics. | Limit fluid block updates per tick using a queue, or use virtual reservoirs where the physical fluid is only a visual "crust" and extraction simply decrements a logical counter. |
| **BlockEntity Ticking** | Medium | `WellheadBlockEntity` querying `IFluidHandler` capabilities and updating fluid networks every tick can cause lag in large factory setups. | Implement tick throttling (e.g., update every 10 ticks). Cache connected `IFluidHandler` capabilities and invalidate on block updates. |
| **Data Pack Reloading** | Medium | `VirtualDataPack` features synthesized at startup cannot easily be reloaded dynamically via `/reload` without restarting the world/server. | Clear and rebuild `VirtualDataPack` entries during the `OnDatapackSyncEvent`, and ensure the `ReservoirConfig` registry supports hot-reloading. |

---

## 4. Security & Safety Considerations
- **Griefing / Base Destruction**: Eruptions and explosions from blowouts could be intentionally triggered by players under other players' bases.
- **Client Render Freezes**: The "high-velocity upward fluid spray particles" must have a hardcap per chunk to avoid intentional FPS dropping (lag bombs).
- **Infinite Eruption Loop**: If an explosion destroys the `DrillCasingBlock`, it could trigger another blowout. A cooldown or hard limit on blowout events per chunk must be enforced.

---

## 5. Backward Compatibility & Migration Impact
- **WorldGen Changes**: Existing chunks will not contain the new reservoirs. Players will need to generate new terrain to find them, which is standard for WorldGen additions. Retrogen (retroactive generation) might be considered if strictly necessary.
- **Config Schema**: Adding new properties to `ReservoirConfig` in the future must remain backward compatible with older JSON schemas.

---

## 6. Recommendations & Action Items for Implementation Plan
1. **Critical Pre-requisites**:
   - **Define the Data Model**: Design a `ReservoirSavedData` class attached to the `ServerLevel` to track reservoir ID, bounding box, fluid type, remaining volume, and pressure globally. 
   - **Architect WorldGen Safety**: Decide whether to use Jigsaw structures or strictly clamped single-chunk features to prevent cascading.
2. **Suggested Scope Adjustments**:
   - **Virtual vs Physical Extraction**: Instead of physically removing fluid blocks from the cavern during extraction, treat the physical cavern purely as WorldGen/hazards. The Wellhead extracts from the *logical* `ReservoirSavedData` counter to completely avoid fluid update lag.
3. **Key Safeguards**:
   - Implement an event-driven or queued system for block breaking during explosions/eruptions to prevent stack overflow from recursive updates.
   - Limit Wellhead fluid extraction operations and capability querying to at most once per 20 ticks (1 second).
   - Add a configuration option to globally disable reservoir hazard destruction (explosions/eruptions) for server admins.
