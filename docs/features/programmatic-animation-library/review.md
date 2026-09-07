# Feature Review & Risk Assessment: Programmatic Animation Library

- **Specification Reference**: [`spec.md`](./spec.md)
- **Reviewer**: Principal Software Architect
- **Date**: 2026-09-07
- **Overall Assessment**: Medium Risk

---

## 1. Executive Summary
The Programmatic Animation Library feature specification presents a solid foundation for adding high-fidelity, code-driven animations to Modern Machines. It correctly targets the pain points of manual `PoseStack` manipulation and external JSON dependencies. The declarative DSL and Layer 0/1 blending concepts are excellent. However, there are significant performance and technical risks around matrix object allocation during rendering, angle wrapping during interpolation, and network packet dispatch thread safety that must be addressed to ensure production stability and zero GC overhead. Overall, the feature is of Medium Risk and requires strict low-level implementation safeguards.

---

## 2. Scope Completeness & Missing Requirements
- **Omitted Edge Cases**:
  - **Angle Wrapping**: The spec mentions float `partialTick` interpolation but omits how angle wrapping (e.g., 359° to 0° degrees) will be handled to prevent reverse spinning (jitter).
  - **Model Caching**: The spec mentions dynamic `ModelResourceLocation` but does not specify how these baked models will be cached or resolved on the client to avoid querying the `ModelManager` every frame.
  - **Custom AABBs**: Frustum culling requires knowing the bounds of the block entity. Animated parts might extend beyond the standard 1x1x1 block bounds, requiring custom `AABB` definitions for accurate culling.
- **Ambiguous Requirements**:
  - **Missing Sub-Model Logging**: "log warning once" per missing model is not fully defined. It must be tracked per-model (e.g., in a `Set<ResourceLocation>`), not per-block-entity, to avoid log spam when multiple machines of the same type are placed.
- **Missing Failure Modes**:
  - **Matrix Stack Mismatch**: If a timeline layer or part graph push/pop operation throws an exception, the `PoseStack` might not be popped correctly, leading to a GL stack overflow in the renderer.
  - **Chunk Unload/Reload**: Behavior of the animation timeline state when a chunk is unloaded and reloaded on the client (does it restart, fast-forward, or persist?).

---

## 3. Technical & Architectural Risks
| Risk Area | Severity (Low/Med/High) | Description & Potential Impact | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| **Performance** | High | Allocating `Matrix4f`, `Quaternionf`, or `Transform3D` objects per frame per machine will cause severe Garbage Collection (GC) spikes and lag. | Use reusable, mutable `Matrix4f` and `Quaternionf` instances. `Transform3D` should be updated in-place. |
| **Concurrency** | High | `S2CAnimation...Packet` handlers executing on Netty threads modifying block entity state while the render thread reads it. | Network packet handlers must enqueue tasks to the main client thread (`Minecraft.getInstance().execute(...)`). |
| **Data Integrity** | Medium | Continuous kinematic drivers might drift between server and client if the server experiences tick lag. | Synchronize absolute angle alongside speed. Use `Mth.lerpDegrees` or Quaternion `slerp`. |
| **Rendering** | Medium | Uncaught exceptions during sub-part rendering leaving the `PoseStack` in an invalid state, crashing the renderer. | Enforce `try-finally` blocks around all `PoseStack.pushPose()` and `PoseStack.popPose()` calls. |

---

## 4. Security & Compliance Considerations
- **Network Packet Spamming**: Since packets are Server-to-Client (S2C), clients cannot spoof them. However, poorly written server machine logic could spam `S2CAnimationTriggerPacket` to clients, causing client-side lag. Ensure triggers are only sent on state transitions.
- **Payload Validation**: Ensure `triggerId` and `BlockPos` in packets are validated against the loaded client world to prevent `NullPointerException` or out-of-bounds access.

---

## 5. Backward Compatibility & Migration Impact
- **Block Entity Integration**: `BaseMachineBlockEntity` currently implements `ISideConfigurable`, `IUpgradableMachine`, etc. Adding `IAnimatedMachine` and `AnimationParameterContainer` must not break existing NBT serialization. The `AnimationParameterContainer` should use a dedicated sub-tag (e.g., `AnimationData`) to avoid key collisions.
- **API Clients**: Existing non-animated machines should not pay the performance penalty of empty animation updates. `BaseMachineBlockEntity` should only sync the animation container if the subclass registers parameters.

---

## 6. Recommendations & Action Items for Implementation Plan
1. **Critical Pre-requisites**: Design an object pool or mutable API for `Transform3D` and quaternion math to guarantee zero object allocations during the render cycle.
2. **Critical Pre-requisites**: Ensure all network packet handlers execute on the main client thread via NeoForge network context `enqueueWork`.
3. **Key Safeguards**: Define a system for overriding machine AABBs for proper frustum culling. Implement `try-finally` for all `PoseStack` operations.
4. **Suggested Scope Adjustments**: Limit the initial easing functions to the most commonly used ones (Linear, Quad, Sine, Cubic) to reduce testing surface area and ensure mathematical correctness, adding the rest later if needed.
