# Feature Review & Risk Assessment: JSON-Configurable Ore Generation Condition System

- **Specification Reference**: [`spec.md`](./spec.md)
- **Reviewer**: Principal Software Architect & Security Specialist
- **Date**: 2026-09-05
- **Overall Assessment**: High Risk / Needs Refinement

---

## 1. Executive Summary
The proposed JSON-Configurable Ore Generation Condition System offers a highly flexible and powerful mechanism for defining ore generation rules. However, the specification relies heavily on synthesizing vanilla `PlacementModifier` and `BiomeModifier` configurations to fulfill these rules. There is a fundamental disconnect between the requested logical expressiveness (e.g., dimension filtering for shared biomes, `or`/`not` combinators for placement rules) and the native capabilities of Minecraft's data-driven worldgen system. Significant refinements in the low-level design and scope adjustments are necessary before implementation can safely begin.

---

## 2. Scope Completeness & Missing Requirements
- **Omitted Edge Cases**:
  - **Overlapping Rules**: What happens if multiple rules for the same material target the same biome/dimension? Do they stack (generating multiple veins), or does one take precedence?
  - **Dimension Filter vs Biome Scope**: If a biome (e.g., `minecraft:plains`) exists in both the Overworld and a custom dimension, filtering by dimension ID using only vanilla `BiomeModifier` is impossible because modifiers target biomes, not dimensions.
- **Ambiguous Requirements**:
  - **Logical Combinators (`and`, `or`, `not`)**: The spec dictates these combinators for constraints, but vanilla `PlacementModifier`s do not support `or`/`not` logical operators out-of-the-box. Sequential modifiers act as `and`.
  - **`near_lava` Check**: It is not specified if this is a strict 1-block adjacency check or a radius check. A radius check during generation can trigger chunk cascading if it looks outside the currently populating chunk.
- **Missing Failure Modes**:
  - Deeply nested logical combinators could result in exponential expansion if translated into multiple discrete `PlacedFeature` permutations.
  - Generating hundreds of new configured/placed features dynamically might exceed network payload limits if these are synced to clients, though worldgen registries are typically server-side only.

---

## 3. Technical & Architectural Risks
| Risk Area | Severity (Low/Med/High) | Description & Potential Impact | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **Dimension Filtering** | High | Vanilla features are biome-specific, not dimension-specific. A `BiomeModifier` cannot natively restrict a biome to a specific dimension. | Introduce a custom `DimensionFilterPlacementModifier` or clarify that dimension filters will just map to dimension-specific biome tags (e.g., `#minecraft:is_overworld`). |
| **Logical Combinator Mapping** | High | Translating `or`/`not` rules into vanilla `PlacedFeature` sequences is impossible without custom modifiers or exponential feature duplication. | Restrict combinators to biome selection only (using NeoForge's `neoforge:any`, `neoforge:not`), or implement a custom composable `PlacementModifier`. |
| **Chunk Cascading** | Medium | The `near_lava` rule might inspect blocks in adjacent, unloaded chunks, causing cascading generation lag. | Restrict spatial checks to the `WorldGenRegion` safe bounds (typically within the 16x16 chunk area being populated) and avoid radius lookups > 1 block. |
| **JSON Parsing/Error Recovery** | Medium | Validating deep tree structures without failing the entire file load requires custom deserialization logic instead of generic `Gson` mapping. | Implement a robust JSON validation layer with custom TypeAdapters that yield `Result<T>` instead of throwing `JsonParseException`. |

---

## 4. Security & Compliance Considerations
- **Input Validation / Injection vectors**: 
  - Malicious or overly complex JSON configurations (e.g., recursive `and`/`or` combinators) could cause stack overflow or memory exhaustion (Billion Laughs / ReDoS-style attacks on the parser). Limit maximum nesting depth for combinators to ~3-5.
- **Resource Exhaustion**: 
  - A server admin could accidentally configure `veins_per_chunk: 1000` and `vein_size: 64`, causing catastrophic server lag during chunk generation. Max bounds must be enforced (e.g., clamp veins to max 100, size to max 64).

---

## 5. Backward Compatibility & Migration Impact
- **API Clients**: 
  - 100% backward compatibility is mandated. The existing schema uses `overworld`, `nether`, and `end` keys.
  - The migration must reliably transform `overworld` to map to the `#minecraft:is_overworld` biome tag rather than a hardcoded dimension, as this mimics the exact behavior of legacy configurations.
- **Data Pack Synthesis**: 
  - The naming convention for synthesized IDs (e.g., `ore_<name>_<rule_id>`) must be completely deterministic so that worldgen data doesn't mismatch across server restarts, which could corrupt chunk feature parity. Use robust hashing of the rule contents or sequential index naming.

---

## 6. Recommendations & Action Items for Implementation Plan
1. **Critical Pre-requisites**:
   - **Architectural Decision**: Decide whether to implement a custom NeoForge `PlacementModifier` to support `dimension`, `not`, and `or` checks, OR restrict these advanced filtering options to biomes only. Custom modifiers are the only way to satisfy the spec fully, despite the NFR cautioning against reflective/sync lookups (a custom modifier can just check `level.getLevel().dimension()` efficiently).
2. **Suggested Scope Adjustments**:
   - Limit the `near_lava` check to strict adjacency (distance = 1) to prevent chunk cascading.
   - Restrict logical combinators to biome and dimension filtering, rather than allowing arbitrary combinations of placement heights/rarities.
3. **Key Safeguards**:
   - Add hard clamping validation for `vein_size` (max 64) and `veins_per_chunk` (max 256).
   - Enforce a max depth constraint on logical combinator trees.
   - Implement deterministic naming for all synthesized registry objects to prevent worldgen desyncs.
