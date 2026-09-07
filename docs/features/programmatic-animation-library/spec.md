# Feature Specification: Programmatic Animation Library

- **Status**: Under Review
- **Author**: Feature Planner
- **Date**: 2026-09-07
- **Target Release**: Modern Machines v1.0.0 (Minecraft 1.21.4 / 26.2 NeoForge)

---

## 1. Overview & Problem Statement

- **Summary**: A high-performance, developer-ergonomic programmatic animation library tailored for Minecraft NeoForge machines and kinetic/magic devices. It empowers developers to define complex machine animations (such as rotating crushing wheels, pulsating magical runes, reciprocating pistons, and articulated mechanical linkages) through a hybrid baked sub-model architecture, continuous kinematic drivers, and discrete keyframed timelines with Penner easing curves.
- **Problem**: In Minecraft modding, creating fluid machine animations typically forces a compromise: either using heavy external skeletal animation frameworks (like GeckoLib/AzureLib with JSON models), or manually hacking `PoseStack` translations inside `BlockEntityRenderer` classes with unmaintainable boilerplate, jittery frame rates, and difficult multiplayer synchronization.
- **Goals**:
  - Provide a clean, declarative Java DSL for multi-track, multi-layer animations.
  - Deliver 60+ FPS butter-smooth client rendering via float `partialTick` interpolation and Penner easing functions.
  - Support hierarchical part graphs with parent-child matrix inheritance and configurable local pivot points (X, Y, Z).
  - Implement a lightweight, low-bandwidth server-to-client parameter synchronization container and one-shot event trigger network channel.
  - Provide an optimized BlockEntityRenderer (BER) with built-in frustum/distance culling and an abstraction hook for Flywheel/GPU instancing.
  - Seamlessly integrate with [BaseMachineBlockEntity](file:///C:/Users/gusta/dev/Modern%20Machines/src/main/java/io/github/gtbauke/modernmachines/machine/blockentity/BaseMachineBlockEntity.java#L37).
- **Non-Goals**:
  - Replacing entity skeletal animations (living entity mob models).
  - Writing an in-game graphical animation editor GUI.
  - Ingesting raw Molang math expressions from Bedrock entity json models.

---

## 2. User Personas & User Stories

- **Primary Persona**: Mod Developer & Technical Artist building machines, multiblocks, and kinetic/magical blocks for *Modern Machines*.
- **User Story 1**: *As a mod developer, I want to create a machine with continuous spinning parts (e.g. shafts, crushing wheels, turbines) whose speed dynamically scales with machine activity, so that machines look physically responsive without manual tick math.*
- **User Story 2**: *As a mod developer, I want to trigger multi-stage action sequences (e.g., hatch opening -> piston slamming down -> particle burst -> hatch closing) when specific machine craft cycles or recipe steps occur, with automatic sound and particle triggers.*
- **User Story 3**: *As a player, I want machine animations to render smoothly at high monitor refresh rates without jitter, and with zero lag on multiplayer servers.*

---

## 3. Functional Requirements (FRs)

### 3.1 Mathematical & Easing Engine (`api.client.animation.math`)
- **FR-1.1**: Provide standard Robert Penner easing equations over normalized progress $t \in [0.0, 1.0]$:
  - Linear
  - Quadratic, Cubic, Quartic, Quintic, Exponential (`EaseIn`, `EaseOut`, `EaseInOut`)
  - Sine, Circular
  - Overshoot: `EaseInBack`, `EaseOutBack`, `EaseInOutBack`
  - Spring/Bounce: `EaseOutElastic`, `EaseOutBounce`, `EaseInOutBounce`
- **FR-1.2**: Implement `Transform3D` tracking 5 independent transformation channels:
  - Translation: $(\Delta X, \Delta Y, \Delta Z)$
  - Rotation: Pitch, Yaw, Roll (Euler angles in degrees) + Quaternion rotation support
  - Scale: $(S_X, S_Y, S_Z)$
  - Alpha transparency: $[0.0, 1.0]$
  - Emissive glow light override: $[0, 15]$
- **FR-1.3**: Implement local origin `Pivot` definitions (e.g., `Pivot.center()`, `Pivot.of(x, y, z)` in 0..16 voxel space or 0..1 normalized space) to enable rotation around arbitrary points.

### 3.2 Hierarchical Part Graph & Model Binding (`api.client.animation.model`)
- **FR-2.1**: Define `ModelPartNode` tree structures where child nodes inherit parent translation and rotation transformations via matrix multiplication:
  $$\mathbf{M}_{\text{world}} = \mathbf{M}_{\text{parent}} \times \mathbf{T}(\text{pos}) \times \mathbf{T}(\text{pivot}) \times \mathbf{R}(\text{rot}) \times \mathbf{S}(\text{scale}) \times \mathbf{T}(-\text{pivot})$$
- **FR-2.2**: Support referencing pre-baked standard Minecraft models (`BakedModel`) or dynamic model identifiers (`ModelResourceLocation`) for each sub-part.
- **FR-2.3**: Allow parts to be dynamically hidden or shown based on machine state (`visibility`).

### 3.3 Multi-Layer Animation Controller & Fluent Timeline DSL (`api.client.animation.timeline` & `controller`)
- **FR-3.1**: Support **Layer 0: Kinematic Base Drivers** for continuous state-driven motion:
  - Continuous rotation (speed-scaled or angle-integrated)
  - Sine/Cosine oscillation (bobbing, breathing, wobbling)
  - Inertia/Spin-down smoothing when a machine stops working.
- **FR-3.2**: Support **Layer 1: Discrete Action Timelines** composed of keyframed tracks:
  - Fluent builder chaining: `.track("piston").translateY(0, 0.5f).duration(10).ease(Easing.EASE_OUT_BOUNCE)`
  - Sequence chaining: `.then().translateY(0.5f, 0).duration(5).ease(Easing.EASE_IN_CUBIC)`
  - Looping or one-shot playback modes with configurable end behaviors (`HOLD`, `RESET`, `LOOP`).
- **FR-3.3**: Support **Layer Mixing & Blending** to combine continuous base rotation with transient action clips without popping or transform conflicts.
- **FR-3.4**: Support **Timeline Event Hooks** for triggering `SoundEvents` and `ParticleOptions` at specific timeline timestamps or keyframes on the client.

### 3.4 Server-Client Synchronization & Networking (`api.animation` & `network.packet`)
- **FR-4.1**: Implement `AnimationParameterContainer` on the block entity to manage tracked parameters:
  - `FloatParameter` (e.g. `speed`, `progress`, `temperature`)
  - `BoolParameter` (e.g. `active`, `jammed`, `overheating`)
  - `EnumParameter<E>` (e.g. `MachineStage`)
- **FR-4.2**: Automatic dirty-checking on server tick; dirty parameters are automatically serialized and synced to client tracking chunks via `S2CAnimationParameterSyncPacket` or standard block entity update tags.
- **FR-4.3**: Implement `S2CAnimationTriggerPacket` for one-shot client event triggers (e.g. `be.triggerAnimation("CRUSH_SLAM")`), sending a compact 16-byte packet (`BlockPos`, `int triggerId`) with client-side prediction.

### 3.5 Rendering Pipeline & Performance (`api.client.animation.render`)
- **FR-5.1**: Implement `AnimatedBlockEntityRenderer<T>` extending NeoForge's `BlockEntityRenderer<T>` with:
  - Distance culling (configurable max animation LOD distance)
  - Frustum culling before running matrix math
  - Smooth partial tick interpolation for all transform channels:
    $$V_{\text{render}} = V_{\text{prev}} + (V_{\text{current}} - V_{\text{prev}}) \times \text{partialTick}$$
- **FR-5.2**: Provide `InstancedAnimationHook` interface to allow seamless integration with Flywheel / GPU instanced batch rendering when present.

---

## 4. Non-Functional Requirements (NFRs)

- **Performance**:
  - Zero matrix computation overhead for culled or off-screen block entities.
  - Less than 0.05ms frame render time per animated block entity on standard hardware.
  - Minimal heap allocations during tick and render cycles (reusable `Matrix4f`, `Quaternionf`, `Transform3D` object pools/mutables).
- **Thread Safety & Client-Server Separation**:
  - Strict separation of `@OnlyIn(Dist.CLIENT)` / client-only classes from common/server code.
  - Safe network packet decoding on client network thread with thread-safe dispatching to the client world.
- **Reliability & Smoothness**:
  - Glitch-free interpolation during sudden server lag spikes (partial tick clamping and lerp smoothing).
  - Graceful fallback when a sub-model resource is missing (render missing-texture cube or log warning once).
- **Code Standards**:
  - Full compliance with `AGENTS.md` (mandatory braces, empty lines after code blocks, guard clauses, `var` for local variables, mandatory `@Override`/`@Nullable` annotations).

---

## 5. Integration with Modern Machines Architecture

- **Block Entity Integration**:
  - Interface `IAnimatedMachine` added to `io.github.gtbauke.modernmachines.api.animation`.
  - [BaseMachineBlockEntity](file:///C:/Users/gusta/dev/Modern%20Machines/src/main/java/io/github/gtbauke/modernmachines/machine/blockentity/BaseMachineBlockEntity.java) implements `IAnimatedMachine` with a built-in `AnimationParameterContainer`.
- **Client Registration**:
  - Client setup in [ModernMachinesClient](file:///C:/Users/gusta/dev/Modern%20Machines/src/main/java/io/github/gtbauke/modernmachines/ModernMachinesClient.java) registers `BlockEntityRenderer` instances for animated machines.
- **Reference Implementation / Showcase**:
  - Provide an animated showcase machine block (e.g. `AlloySmelterBlockEntity` or a new Kinetic Machine) demonstrating both continuous rotation and one-shot action keyframe timelines.

---

## 6. Scope & Boundaries

| In Scope | Out of Scope (Future Work) |
| :--- | :--- |
| • Penner easing math engine with 30+ curve variants | • In-game visual GUI keyframe timeline editor |
| • `Transform3D` and `Pivot` local origin systems | • Living entity skeletal bones / skinning |
| • Hierarchical part graph with parent-child matrix inheritance | • Real-time inverse kinematics (IK) physics solver |
| • Kinematic drivers (continuous spin, bob, sine pulse) | • Skeletal Molang/Bedrock JSON model importer |
| • Fluent timeline action builder with audio/particle hooks | • Custom GLSL compute shader pipelines |
| • `AnimationParameterContainer` & network sync packets | |
| • Optimized `AnimatedBlockEntityRenderer` with culling | |
| • Flywheel / instancing abstraction hook | |

---

## 7. Acceptance Criteria

- [ ] **AC-1 (Math & Easing)**: All Penner easing functions evaluated at $t=0.0$, $t=0.5$, and $t=1.0$ yield exact expected mathematical curve boundaries with unit test coverage.
- [ ] **AC-2 (Hierarchy & Pivots)**: A child part attached to a parent node rotating around `Pivot.center()` rotates around the parent's pivot and inherits all parent translations without matrix drift.
- [ ] **AC-3 (Smooth Lerping)**: Kinematic drivers and action timelines render smoothly across variable client framerates (30, 60, 144, 240 FPS) using `partialTick` without jitter or snapping.
- [ ] **AC-4 (Multi-Layer Blending)**: Playing an action clip (e.g. piston crush) on Layer 1 while Layer 0 continuously rotates the core results in simultaneous, clean composite transformations.
- [ ] **AC-5 (Network Sync & Trigger)**: Mutating a float parameter on the server updates the client parameter within 1 network tick; triggering `triggerAnimation()` on the server starts client timeline playback immediately on all tracking clients.
- [ ] **AC-6 (Frustum & Distance Culling)**: Machines located behind the player camera or beyond the configured animation render distance bypass matrix calculations and draw calls entirely.
