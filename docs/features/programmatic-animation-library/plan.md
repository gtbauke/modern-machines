# Low-Level Design & Implementation Plan: Programmatic Animation Library

- **Specification Reference**: [`spec.md`](./spec.md)
- **Review Reference**: [`review.md`](./review.md)
- **Author**: Staff Software Engineer & Tech Lead
- **Date**: 2026-09-07
- **Status**: Ready for Implementation

---

## 1. Architecture & Design Decisions

- **Design Pattern**: Component-based Architecture, Fluent Builder DSL, and Model-View Separation (Server state vs. Client rendering).
- **Zero-Allocation Rendering**: To mitigate GC overhead, `Transform3D` will be highly mutable and `Matrix4f` / `Quaternionf` operations will utilize pre-allocated instances (or `joml` mutables) that are updated in place during the rendering loop. 
- **Concurrency & Thread Safety**: All incoming network packets (`S2CAnimationParameterSyncPacket`, `S2CAnimationTriggerPacket`) will defer state changes to the main client thread using NeoForge's network `context.enqueueWork(...)` to prevent `ConcurrentModificationException` during rendering.
- **Matrix Integrity**: To prevent `PoseStack` overflow/underflow on exceptions during rendering, every transformation scope (`pushPose`) will be strictly paired with `popPose` within a `try-finally` block.
- **Math & Interpolation Resilience**: Kinematic driver angle calculations will interpolate using `Mth.rotLerp` (or JOML Quaternion Slerp) to solve the 359° to 0° wrapping issue.

---

## 2. Mathematical & Easing Engine (`api.client.animation.math`)

### 2.1 Easing & Math APIs
```java
public interface Easing {
    float ease(float t);
    
    // Core implementations
    Easing LINEAR = t -> t;
    Easing EASE_IN_QUAD = t -> t * t;
    Easing EASE_OUT_QUAD = t -> t * (2 - t);
    Easing EASE_IN_OUT_QUAD = t -> t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    // ... Sine, Cubic, etc.
}

public class Transform3D {
    public final Vector3f translation = new Vector3f();
    public final Vector3f rotation = new Vector3f(); // Euler angles
    public final Vector3f scale = new Vector3f(1, 1, 1);
    public float alpha = 1.0f;
    public int emissiveLight = -1; // -1 for disabled

    public void clear() {
        translation.zero();
        rotation.zero();
        scale.set(1, 1, 1);
        alpha = 1.0f;
        emissiveLight = -1;
    }
    
    // Mutators, blended add/multiply, etc.
}

public record Pivot(float x, float y, float z) {
    public static final Pivot CENTER = new Pivot(0.5f, 0.5f, 0.5f);
    public static Pivot of(float x, float y, float z) { return new Pivot(x, y, z); }
}
```

---

## 3. Hierarchical Part Graph (`api.client.animation.model`)

### 3.1 Graph Nodes
```java
public class ModelPartNode {
    private final String name;
    private final ResourceLocation modelId;
    private final Pivot pivot;
    private final List<ModelPartNode> children = new ArrayList<>();
    
    // Transient render state
    public final Transform3D currentTransform = new Transform3D();
    public boolean visible = true;
    
    // Parent-child matrix evaluation happens in the renderer.
}
```

---

## 4. Animation Timeline & Controller DSL (`api.client.animation.timeline`)

### 4.1 Layered Playback System
```java
public class AnimationController {
    // Layer 0: Kinematics (e.g., continuous spin)
    private final List<KinematicDriver> baseDrivers = new ArrayList<>();
    
    // Layer 1: Discrete Clips
    private final Map<String, TimelineAction> timelines = new HashMap<>();
    
    public void evaluate(float partialTick, ModelPartNode root) {
        // Evaluate Kinematics, output to part transforms
        // Evaluate active timelines, blend over base kinematics
    }
}
```
**Blending Logic**: Discrete track targets operate additively or multiplicatively on the base `Transform3D` state.

---

## 5. Network Sync & Block Entity Integration (`api.animation`)

### 5.1 Parameter Container
```java
public class AnimationParameterContainer {
    // Dirty tracking
    private boolean dirty = false;
    private final Map<String, Float> floatParams = new HashMap<>();
    
    public void setFloat(String key, float value) { ... dirty = true; }
    public CompoundTag save(HolderLookup.Provider provider) { ... }
    public void load(CompoundTag tag, HolderLookup.Provider provider) { ... }
}
```
*Backward Compatibility*: Saved in `BaseMachineBlockEntity` under a `"AnimationData"` NBT tag.

### 5.2 Network Packets (NeoForge 1.21.4 PayloadRegistrar)
```java
// Synchronizes tracked states from the server
public record S2CAnimationParameterSyncPacket(BlockPos pos, CompoundTag data) implements CustomPacketPayload {
    // Handler must use enqueueWork
}

// Triggers one-shot timeline events (max 16 bytes overhead)
public record S2CAnimationTriggerPacket(BlockPos pos, int triggerId) implements CustomPacketPayload {
    // Handler must use enqueueWork
}
```

---

## 6. Rendering Pipeline (`api.client.animation.render`)

### 6.1 Animated Block Entity Renderer
```java
public abstract class AnimatedBlockEntityRenderer<T extends BaseMachineBlockEntity & IAnimatedMachine> implements BlockEntityRenderer<T> {
    
    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // 1. Culling Check (Distance & Frustum) using blockEntity.getRenderBoundingBox()
        // 2. Fetch or update AnimationController state via partialTick
        // 3. Render Graph Execution:
        poseStack.pushPose();
        try {
            renderNode(blockEntity.getRootNode(), poseStack, ...);
        } finally {
            poseStack.popPose();
        }
    }
    
    private void renderNode(ModelPartNode node, PoseStack poseStack, ...) {
        if (!node.visible) return;
        poseStack.pushPose();
        try {
            // Apply Pivot -> Transform -> -Pivot
            // Render cached BakedModel
            // Recurse to children
        } finally {
            poseStack.popPose();
        }
    }
}
```

---

## 7. Step-by-Step Implementation Task List

### Phase 1: Math & Easing Engine Core
- [x] **Task 1.1**: Implement `Easing` functional interface and Penner implementations (Linear, Quad, Sine, Cubic).
  - *Verification*: `./gradlew compileJava`
- [x] **Task 1.2**: Implement `Transform3D` and `Pivot` classes with pooling/mutability.
  - *Verification*: `./gradlew compileJava`

### Phase 2: Hierarchy & Animation Controllers
- [ ] **Task 2.1**: Implement `ModelPartNode` structure and cached `BakedModel` resolver.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 2.2**: Implement `KinematicDriver`, `TimelineAction`, and the `AnimationController` DSL. Apply `Mth.rotLerp` for angle safety.
  - *Verification*: `./gradlew compileJava`

### Phase 3: BlockEntity & Networking Hooks
- [ ] **Task 3.1**: Create `IAnimatedMachine` and `AnimationParameterContainer`. Integrate into `BaseMachineBlockEntity` with NBT `"AnimationData"`.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 3.2**: Create `S2CAnimationParameterSyncPacket` and `S2CAnimationTriggerPacket`. Register in `ModNetworking`. Ensure `context.enqueueWork` is used.
  - *Verification*: `./gradlew compileJava`

### Phase 4: Render Pipeline & Client Integration
- [ ] **Task 4.1**: Implement `AnimatedBlockEntityRenderer` with `try-finally` safety and distance/frustum culling by overriding custom render bounds.
  - *Verification*: `./gradlew compileJava`
- [ ] **Task 4.2**: Set up an example implementation using `AlloySmelterBlockEntity` (or a mock machine) configuring `AnimationController` tracks.
  - *Verification*: `./gradlew runClient` and visual verification.

### Phase 5: Final Polish & Testing
- [ ] **Task 5.1**: Ensure all files comply with `AGENTS.md` (braces, blank lines, `var` usage).
  - *Verification*: Code review or static analysis tools.
- [ ] **Task 5.2**: Run full test suite to guarantee no network regressions or build failures.
  - *Verification*: `./gradlew build`
