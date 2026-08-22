# Materials & Modular Tools Guide (Layer 3 Reference)

This document describes the material registration builder, resource forms, and modular tool part definitions in **Modern Machines**.

---

## 1. Material Declaration & `MaterialBuilder`

All materials are registered in `ModMaterials.java` using the fluent `MaterialBuilder` API:

```java
public class ModMaterials {
    public static Material ALUMINUM;
    public static Material TITANIUM;

    public static void init() {
        ALUMINUM = MaterialBuilder.of("aluminum")
            .displayName("Aluminum")
            .type(MaterialType.METALLIC_ORE)
            .color(0xC4D6E2)
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .miningLevel(BlockTags.NEEDS_IRON_TOOL)
            .hardness(3.0f, 4.0f)
            .smeltingXp(0.7f)
            .oreDefaults()
            .forms(ResourceForm.PLATE, ResourceForm.GEAR, ResourceForm.ROD, ResourceForm.WIRE)
            .build();
    }
}
```

### Supported Resource Forms (`ResourceForm`):
- Ores: `ORE`, `DEEPSLATE_ORE`, `RAW_ORE`, `RAW_STORAGE_BLOCK`, `STORAGE_BLOCK`
- Items: `INGOT`, `NUGGET`, `DUST`, `PLATE`, `GEAR`, `ROD`, `WIRE`, `SCREW`

---

## 2. Modular Tool System (`api.modular.*`)

Modular tools are assembled at the Tinkering Table using custom interchangeable parts:
- **`ToolPartType`**:
  - `HEAD` (Determines mining level, base speed, attack damage)
  - `HANDLE` (Determines durability multiplier, attack speed modifier)
  - `BINDING` (Provides trait slots, handling bonuses)
  - `ATTACHMENT` (Special upgrades, enchantments, elemental traits)
- **`PartSlot`**:
  - Describes required and optional part slots for tool categories (Pickaxe, Axe, Shovel, Sword, Wrench).
- **`MaterialTrait`**:
  - Passive traits granted by materials (e.g., `Lightweight` (+10% attack speed), `Magnetic` (draws items), `Reinforced` (chance not to consume durability)).
- **`MaterialStatsManager`**:
  - Reload listener (`Identifier.fromNamespaceAndPath(MOD_ID, "materials")`) syncing tool stats from JSON data (`data/modernmachines/materials/`).
