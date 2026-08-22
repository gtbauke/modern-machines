# Coding Standards & Conventions (Layer 3 Reference)

This document defines the core programming and architectural standards for **Modern Machines**.
Target Environment: **Minecraft 26.2**, **NeoForge 26.2.0.59**, **Java 21**.

---

## 1. Package & Naming Conventions
- Base Package: `io.github.gtbauke.modernmachines`
- Subpackages:
  - `api.*` - Public interfaces and data definitions (`api.machine`, `api.modular`, `api.resource`)
  - `core.registry.*` - Registration entry points (`ModBlocks`, `ModItems`, `ModBlockEntities`, `ModMenuTypes`, `ModRecipeTypes`, `ModDataComponents`, `ModMaterials`)
  - `core.block.*` - Block implementations
  - `core.blockentity.*` - BlockEntity implementations
  - `core.item.*` - Custom Item implementations
  - `core.menu.*` - ContainerMenu implementations
  - `client.gui.*` - Flexbox UI system and screen implementations
  - `datagen.*` - NeoForge Data Generator providers
  - `network.*` - Custom network payload packets

### Naming Conventions:
- **Registry IDs**: snake_case lowercase (e.g., `basic_alloy_smelter`, `aluminum_ingot`, `reinforced_casing`).
- **Java Classes**: PascalCase (e.g., `BasicAlloySmelterBlockEntity`, `ElectricCrusherMenu`).
- **Textures**: snake_case `.png` under `src/main/resources/assets/modernmachines/textures/`.
- **Blockstates & Models**: snake_case `.json` under `src/main/resources/assets/modernmachines/models/` and `blockstates/`.

---

## 2. NeoForge 26.2 Registration Patterns
Modern Machines uses `DeferredRegister.Blocks` and `DeferredRegister.Items` bound to the mod event bus.

### Example Registration:
```java
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ModernMachines.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModernMachines.MOD_ID);

    public static final DeferredBlock<Block> CRUSHER = BLOCKS.register("crusher",
        () -> new CrusherBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(3.5f, 6.0f)
            .requiresCorrectToolForDrops()));

    public static final DeferredItem<BlockItem> CRUSHER_ITEM = ITEMS.registerSimpleBlockItem("crusher", CRUSHER);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
```

---

## 3. Capability Registration (NeoForge 26.2)
Capabilities must be registered during the `RegisterCapabilitiesEvent` in `ModBlockEntities::registerCapabilities`:
```java
event.registerBlockEntity(
    Capabilities.EnergyStorage.BLOCK,
    ModBlockEntities.CRUSHER.get(),
    (be, side) -> be.getEnergyStorage(side)
);
event.registerBlockEntity(
    Capabilities.ItemHandler.BLOCK,
    ModBlockEntities.CRUSHER.get(),
    (be, side) -> be.getItemHandler(side)
);
```

---

## 4. Nullability and Annotations
- Use `@Nullable` from `org.jetbrains.annotations.Nullable` or `javax.annotation.Nullable` for optional return values (e.g., side capabilities, null containers).
- Use `@Override` explicitly on all interface implementations.
- Avoid raw types; specify exact generics for holders, menus, and item handlers.
