package io.github.gtbauke.modernmachines.core.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.ToolPartType;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.core.item.CraftingToolItem;
import io.github.gtbauke.modernmachines.modular.item.ModularAxeItem;
import io.github.gtbauke.modernmachines.modular.item.ModularHoeItem;
import io.github.gtbauke.modernmachines.modular.item.ModularPickaxeItem;
import io.github.gtbauke.modernmachines.modular.item.ModularShovelItem;
import io.github.gtbauke.modernmachines.modular.item.ModularSwordItem;
import io.github.gtbauke.modernmachines.modular.item.PatternItem;
import io.github.gtbauke.modernmachines.modular.item.ToolPartItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ModernMachines.MOD_ID);

    // Workstation Block Items
    public static final DeferredItem<BlockItem> PART_BUILDER = ITEMS.registerSimpleBlockItem(ModBlocks.PART_BUILDER);
    public static final DeferredItem<BlockItem> TINKERING_TABLE = ITEMS.registerSimpleBlockItem(ModBlocks.TINKERING_TABLE);
    public static final DeferredItem<BlockItem> BASIC_ALLOY_SMELTER_CONTROLLER = ITEMS.registerSimpleBlockItem(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER);
    public static final DeferredItem<BlockItem> BASIC_ALLOY_SMELTER_HEATER = ITEMS.registerSimpleBlockItem(ModBlocks.BASIC_ALLOY_SMELTER_HEATER);
    public static final DeferredItem<BlockItem> ALLOY_SMELTER = BASIC_ALLOY_SMELTER_CONTROLLER;
    public static final DeferredItem<BlockItem> ENGINEERS_TERMINAL = ITEMS.registerSimpleBlockItem(ModBlocks.ENGINEERS_TERMINAL);

    // Steam Era Block Items
    public static final DeferredItem<BlockItem> ADOBE_BRICK_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.ADOBE_BRICK);
    public static final DeferredItem<BlockItem> COPPER_PIPE = ITEMS.registerSimpleBlockItem(ModBlocks.COPPER_PIPE);
    public static final DeferredItem<BlockItem> STEEL_PIPE = ITEMS.registerSimpleBlockItem(ModBlocks.STEEL_PIPE);
//    public static final DeferredItem<BlockItem> BRONZE_CASING = ITEMS.registerSimpleBlockItem(ModBlocks.BRONZE_CASING);
//    public static final DeferredItem<BlockItem> SOLID_FUEL_BOILER = ITEMS.registerSimpleBlockItem(ModBlocks.SOLID_FUEL_BOILER);
//    public static final DeferredItem<BlockItem> STEAM_TURBINE = ITEMS.registerSimpleBlockItem(ModBlocks.STEAM_TURBINE);
//    public static final DeferredItem<BlockItem> STEAM_CRUSHER = ITEMS.registerSimpleBlockItem(ModBlocks.STEAM_CRUSHER);
//    public static final DeferredItem<BlockItem> STEAM_ALLOY_SMELTER = ITEMS.registerSimpleBlockItem(ModBlocks.STEAM_ALLOY_SMELTER);
//    public static final DeferredItem<BlockItem> BRONZE_FLUID_TANK = ITEMS.registerSimpleBlockItem(ModBlocks.BRONZE_FLUID_TANK);
//    public static final DeferredItem<BlockItem> BRONZE_FLUID_PIPE = ITEMS.registerSimpleBlockItem(ModBlocks.BRONZE_FLUID_PIPE);

    // Steam Era Components
    public static final DeferredItem<Item> ADOBE_MIXTURE = ITEMS.registerItem("adobe_mixture", Item::new);
    public static final DeferredItem<Item> ADOBE_BRICKS = ITEMS.registerItem("adobe_bricks", Item::new);

//    public static final DeferredItem<Item> STEAM_PISTON = ITEMS.registerItem("steam_piston",
//            Item::new);
//    public static final DeferredItem<Item> PRESSURE_GAUGE = ITEMS.registerItem("pressure_gauge",
//            Item::new);
//    public static final DeferredItem<Item> BRONZE_VALVE = ITEMS.registerItem("bronze_valve",
//            Item::new);
//    public static final DeferredItem<BucketItem> STEAM_BUCKET = ITEMS.registerItem("steam_bucket",
//            p -> new BucketItem(ModFluids.STEAM_SOURCE.get(), p.stacksTo(1)));

    // Engineer's Tablet Item
    public static final DeferredItem<io.github.gtbauke.modernmachines.core.item.EngineersTabletItem> ENGINEERS_TABLET = ITEMS.registerItem("engineers_tablet",
            io.github.gtbauke.modernmachines.core.item.EngineersTabletItem::new, p -> p.stacksTo(1));

    // Machine Upgrades
    public static final DeferredItem<io.github.gtbauke.modernmachines.machine.upgrade.SpeedUpgradeItem> SPEED_UPGRADE =
            ITEMS.registerItem("speed_upgrade", io.github.gtbauke.modernmachines.machine.upgrade.SpeedUpgradeItem::new);

    public static final DeferredItem<io.github.gtbauke.modernmachines.machine.upgrade.EnergyEfficiencyUpgradeItem> ENERGY_EFFICIENCY_UPGRADE =
            ITEMS.registerItem("energy_efficiency_upgrade", io.github.gtbauke.modernmachines.machine.upgrade.EnergyEfficiencyUpgradeItem::new);

    // Crafting Tools
    public static final DeferredItem<CraftingToolItem> ENGINEER_HAMMER = ITEMS.registerItem("engineers_hammer",
            CraftingToolItem::new, p -> p.durability(512));

    public static final DeferredItem<CraftingToolItem> WIRE_CUTTER = ITEMS.registerItem("wire_cutter",
            CraftingToolItem::new, p -> p.durability(256));

    // Stencils & Patterns
    public static final DeferredItem<PatternItem> BLANK_PATTERN = ITEMS.registerItem("blank_pattern",
            p -> new PatternItem(Optional.empty(), p));

    public static final DeferredItem<PatternItem> PICKAXE_HEAD_PATTERN = ITEMS.registerItem("pickaxe_head_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.PICKAXE_HEAD), p));

    public static final DeferredItem<PatternItem> AXE_HEAD_PATTERN = ITEMS.registerItem("axe_head_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.AXE_HEAD), p));

    public static final DeferredItem<PatternItem> SHOVEL_HEAD_PATTERN = ITEMS.registerItem("shovel_head_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.SHOVEL_HEAD), p));

    public static final DeferredItem<PatternItem> SWORD_BLADE_PATTERN = ITEMS.registerItem("sword_blade_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.SWORD_BLADE), p));

    public static final DeferredItem<PatternItem> HOE_HEAD_PATTERN = ITEMS.registerItem("hoe_head_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.HOE_HEAD), p));

    public static final DeferredItem<PatternItem> HANDLE_PATTERN = ITEMS.registerItem("handle_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.HANDLE), p));

    public static final DeferredItem<PatternItem> BINDING_PATTERN = ITEMS.registerItem("binding_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.BINDING), p));

    public static final DeferredItem<PatternItem> TIP_PATTERN = ITEMS.registerItem("tip_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.TIP), p));

    public static final DeferredItem<PatternItem> GRIP_PATTERN = ITEMS.registerItem("grip_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.GRIP), p));

    public static final DeferredItem<PatternItem> SWORD_GUARD_PATTERN = ITEMS.registerItem("sword_guard_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.SWORD_GUARD), p));

    public static final DeferredItem<PatternItem> POMMEL_PATTERN = ITEMS.registerItem("pommel_pattern",
            p -> new PatternItem(Optional.of(ToolPartType.POMMEL), p));

    // Modular Tools
    public static final DeferredItem<ModularPickaxeItem> MODULAR_PICKAXE = ITEMS.registerItem("modular_pickaxe",
            ModularPickaxeItem::new);

    public static final DeferredItem<ModularAxeItem> MODULAR_AXE = ITEMS.registerItem("modular_axe",
            ModularAxeItem::new);

    public static final DeferredItem<ModularShovelItem> MODULAR_SHOVEL = ITEMS.registerItem("modular_shovel",
            ModularShovelItem::new);

    public static final DeferredItem<ModularSwordItem> MODULAR_SWORD = ITEMS.registerItem("modular_sword",
            ModularSwordItem::new);

    public static final DeferredItem<ModularHoeItem> MODULAR_HOE = ITEMS.registerItem("modular_hoe",
            ModularHoeItem::new);

    // Tool Parts per Material & PartType
    private static final Map<String, DeferredItem<ToolPartItem>> TOOL_PARTS = new HashMap<>();

    public static void registerToolParts() {
        for (Material material : ModMaterials.getAllMaterials()) {
            for (ToolPartType partType : ToolPartType.values()) {
                if (partType == ToolPartType.POMMEL) {
                    if (material != ModMaterials.LAPIS_LAZULI &&
                        material != ModMaterials.DIAMOND &&
                        material != ModMaterials.EMERALD &&
                        material != ModMaterials.AMETHYST) {
                        continue;
                    }
                }
                String regName = partType.getSerializedName() + "_" + material.name();
                DeferredItem<ToolPartItem> item = ITEMS.registerItem(regName,
                        p -> new ToolPartItem(partType, material, p));
                TOOL_PARTS.put(regName, item);
            }
        }
    }

    public static ToolPartItem getToolPart(ToolPartType partType, Material material) {
        String key = partType.getSerializedName() + "_" + material.name();
        DeferredItem<ToolPartItem> item = TOOL_PARTS.get(key);
        return item != null ? item.get() : null;
    }

    public static Map<String, DeferredItem<ToolPartItem>> getAllToolParts() {
        return TOOL_PARTS;
    }

    public static void register(IEventBus eventBus) {
        registerToolParts();
        ITEMS.register(eventBus);
    }
}
