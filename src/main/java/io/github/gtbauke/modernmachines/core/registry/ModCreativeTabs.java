package io.github.gtbauke.modernmachines.core.registry;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModernMachines.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCKS_TAB =
            CREATIVE_MODE_TABS.register("blocks", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modernmachines.blocks"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModBlocks.COPPER_PIPE.get().asItem().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Workstations
                        output.accept(ModBlocks.PART_BUILDER.get());
                        output.accept(ModBlocks.TINKERING_TABLE.get());
                        output.accept(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get());
                        output.accept(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());

                        // Building Blocks & Pipes
                        output.accept(ModBlocks.ADOBE_BRICK.get());
                        output.accept(ModBlocks.COPPER_PIPE.get());
                        output.accept(ModBlocks.STEEL_PIPE.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS_TAB =
            CREATIVE_MODE_TABS.register("materials", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modernmachines.materials"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModItems.ENGINEER_HAMMER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Steam Era Components & Materials
                        output.accept(ModItems.ADOBE_BRICKS);
                        output.accept(ModItems.ADOBE_MIXTURE);
                        output.accept(ModItems.ENGINEERS_TABLET);

                        // Upgrades
                        output.accept(ModItems.SPEED_UPGRADE.get());
                        output.accept(ModItems.ENERGY_EFFICIENCY_UPGRADE.get());

                        // Crafting tools
                        output.accept(ModItems.ENGINEER_HAMMER.get());
                        output.accept(ModItems.WIRE_CUTTER.get());

                        // Patterns
                        output.accept(ModItems.BLANK_PATTERN.get());
                        output.accept(ModItems.PICKAXE_HEAD_PATTERN.get());
                        output.accept(ModItems.AXE_HEAD_PATTERN.get());
                        output.accept(ModItems.SHOVEL_HEAD_PATTERN.get());
                        output.accept(ModItems.SWORD_BLADE_PATTERN.get());
                        output.accept(ModItems.HOE_HEAD_PATTERN.get());
                        output.accept(ModItems.HANDLE_PATTERN.get());
                        output.accept(ModItems.BINDING_PATTERN.get());
                        output.accept(ModItems.TIP_PATTERN.get());
                        output.accept(ModItems.GRIP_PATTERN.get());

                        // Modular Tools
                        output.accept(ModItems.MODULAR_PICKAXE.get());
                        output.accept(ModItems.MODULAR_AXE.get());
                        output.accept(ModItems.MODULAR_SHOVEL.get());
                        output.accept(ModItems.MODULAR_SWORD.get());
                        output.accept(ModItems.MODULAR_HOE.get());

                        // Modular Tool Parts
                        ModItems.getAllToolParts().values().forEach(part -> output.accept(part.get()));

                        // Iterate materials and ordered forms for manufactured parts
                        ResourceForm[] orderedManufacturedForms = {
                                ResourceForm.DUST,
                                ResourceForm.PLATE,
                                ResourceForm.ROD,
                                ResourceForm.SCREW,
                                ResourceForm.WIRE,
                                ResourceForm.GEAR
                        };

                        for (Material material : ModMaterials.getAllMaterials()) {
                            for (ResourceForm form : orderedManufacturedForms) {
                                if (material.isRegisteredLocally(form)) {
                                    DeferredItem<?> item = material.getDeferredItem(form);
                                    if (item != null) {
                                        output.accept(item.get());
                                    }
                                }
                            }
                        }
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RESOURCES_TAB =
            CREATIVE_MODE_TABS.register("resources", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modernmachines.resources"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModMaterials.TIN.getItem(ResourceForm.RAW_ORE).getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        ResourceForm[] orderedResourceForms = {
                                ResourceForm.ORE,
                                ResourceForm.DEEPSLATE_ORE,
                                ResourceForm.NETHERRACK_ORE,
                                ResourceForm.END_STONE_ORE,
                                ResourceForm.RAW_STORAGE_BLOCK,
                                ResourceForm.STORAGE_BLOCK,
                                ResourceForm.RAW_ORE,
                                ResourceForm.INGOT,
                                ResourceForm.GEM,
                                ResourceForm.NUGGET
                        };

                        for (Material material : ModMaterials.getAllMaterials()) {
                            for (ResourceForm form : orderedResourceForms) {
                                if (material.isRegisteredLocally(form)) {
                                    DeferredItem<?> item = material.getDeferredItem(form);
                                    if (item != null) {
                                        output.accept(item.get());
                                    }
                                }
                            }
                        }
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FLUIDS_TAB =
            CREATIVE_MODE_TABS.register("fluids", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modernmachines.fluids"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> {
                        net.minecraft.world.item.Item copperBucket = ModMaterials.COPPER.getItem(ResourceForm.MOLTEN);
                        return (copperBucket != null ? copperBucket : ModItems.ENGINEER_HAMMER.get()).getDefaultInstance();
                    })
                    .displayItems((parameters, output) -> {
                        for (Material material : ModMaterials.getAllMaterials()) {
                            if (material.isRegisteredLocally(ResourceForm.MOLTEN)) {
                                DeferredItem<?> bucketItem = material.getDeferredItem(ResourceForm.MOLTEN);
                                if (bucketItem != null) {
                                    output.accept(bucketItem.get());
                                }
                            }
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
