# Feature Review & Risk Assessment: Resource-Specific Large Ore Veins

- **Specification Reference**: [`spec.md`](./spec.md)
- **Reviewer**: Principal Software Architect & WorldGen Specialist
- **Date**: 2026-09-05
- **Overall Assessment**: High Risk / Needs Refinement

---

## 1. Executive Summary
The Resource-Specific Large Ore Veins feature proposes a robust, configurable system for injecting massive, continuous 3D ore veins into the world using NeoForge's generation system. It addresses core gameplay loops effectively by rewarding exploration and scaling resource acquisition. However, the architectural approach to 3D noise generation and cross-chunk boundary placement introduces substantial technical risks around chunk cascading, determinism, and performance that must be addressed in the Low-Level Design before implementation.

---

## 2. Scope Completeness & Missing Requirements
- **Omitted Edge Cases**:
  - **World Boundaries**: The spec doesn't explicitly mention what happens if a vein generates below `y = -64` or above `y = 320`. The noise bounds need strict world-height clamping.
  - **Void / Sky Veins**: What happens if the vein generates in the End (void) or Sky islands where the host block is air? Are veins allowed to float? Missing rule for `isAir` or `isWater` replacements.
  - **Water / Lava Handling**: If a vein intersects an underground aquifer, ravine, or lava lake, should it overwrite the fluid with filler block, wrap around it, or fall apart? Overwriting can cause ugly square cut-outs in water bodies.
  - **Structure Intersections**: How do large veins interact with Vanilla structures (e.g., Mineshafts, Strongholds, Ancient Cities)? We need to prevent veins from overwriting spawner rooms or end portal frames.

- **Ambiguous Requirements**:
  - **Noise Determinism**: How is the seed derived for the 3D spline/noise? Is it dependent on the chunk origin coordinates? This needs to be deterministic so that generating chunk A and then chunk B yields the same vein continuity as chunk B then chunk A.
  - **Probability Resolution**: The spec mentions probabilities for primary ore, secondary ore, and raw blocks. It needs to be clear if these are independent rolls or a unified drop table to prevent probability overflow (sum > 1.0).

- **Missing Failure Modes**:
  - **Chunk Boundary Clipping (Cascading WorldGen)**: The spec proposes veins spanning 24-64 blocks. If placed from a single chunk origin, placing blocks up to 64 blocks away *will* cause cascading chunk generation, crashing or lagging the server, since safe bounds for a `Feature` placement are strictly within `[chunkX * 16, chunkZ * 16]` to `[(chunkX * 16) + 15, (chunkZ * 16) + 15]` in `WorldGenRegion`.

---

## 3. Technical & Architectural Risks
| Risk Area | Severity (Low/Med/High) | Description & Potential Impact | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **Cascading WorldGen** | High | Veins larger than 16 blocks (up to 64) cannot be placed from a single chunk's `Feature` placement without violating `WorldGenRegion` safe bounds. | Implement the generation using chunk-coordinate noise evaluation (like Vanilla caves) rather than iterative point-placement from an origin chunk. Every chunk evaluates if it intersects a vein. |
| **Performance (Noise)** | High | 3D Simplex/Perlin noise evaluated per-block in the chunk (16x16x384) is extremely CPU intensive. | Downsample the noise evaluation (evaluate every 4th block and interpolate) or tightly bound the Y-range of evaluation. |
| **Surface Indicator Cascading** | Medium | Searching for `WORLD_SURFACE_WG` at the origin might be safe, but placing blocks 1-4 blocks around it might cross chunk boundaries. | Restrict surface indicator placement strictly to the current generating chunk bounds (`pos.getX() & 15`, `pos.getZ() & 15`). |
| **Determinism (Seed)** | Medium | If random number generators (`RandomSource`) are used for spline walking instead of strict noise fields, vein geometry will tear at chunk borders. | Use strict world-seeded 3D noise functions (e.g. `NormalNoise`) configured via `NoiseRouter`, not stateful random splines. |

---

## 4. Security & Compliance Considerations
- **Authentication / Authorization gaps**: N/A for world generation.
- **Input Validation / Injection vectors**: Configuration parsing (e.g., large vein sizes, rarity) could be exploited with negative values, `0` rarity (divide by zero), or massive numbers leading to OOM. 
- **Sensitive Data Exposure**: N/A.

---

## 5. Backward Compatibility & Migration Impact
- **Database Schema**: N/A.
- **WorldGen Upgrades**: Adding new large veins to an existing world will cause sharp chunk borders where new chunks have large veins and old ones don't. While normal for Minecraft, the `VirtualDataPack` must ensure existing world seeds do not change biome/feature placement order (which would shift all other ores).

---

## 6. Recommendations & Action Items for Implementation Plan
1. **Critical Pre-requisites**:
   - Redesign the generation logic to be an *intersection* check. Instead of "Chunk A generates a 64-block vein", it should be "Chunk A checks the noise field to see what part of a global vein falls inside its 16x16 boundaries". This completely eliminates the Cascading WorldGen risk.
2. **Suggested Scope Adjustments**:
   - Limit surface indicators to the strict `16x16` bounding box of the current generating chunk, even if the vein center is slightly outside.
   - Add explicit block tags for `#modernmachines:large_vein_replaceable` to safely avoid replacing bedrock, structure blocks, fluids, and air.
3. **Key Safeguards**:
   - Validate JSON configuration on load: Rarity > 0, lengths clamped to valid noise scales, probabilities summing correctly.
   - Respect `AGENTS.md` guidelines for all new Java classes.
