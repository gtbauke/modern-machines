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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS_TAB =
            CREATIVE_MODE_TABS.register("materials", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modernmachines.materials"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> ModItems.ENGINEER_HAMMER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Workstations
                        output.accept(ModBlocks.PART_BUILDER.get());
                        output.accept(ModBlocks.TINKERING_TABLE.get());
                        output.accept(ModBlocks.BASIC_ALLOY_SMELTER_CONTROLLER.get());
                        output.accept(ModBlocks.BASIC_ALLOY_SMELTER_HEATER.get());
                        output.accept(ModBlocks.ENGINEERS_TERMINAL.get());

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

                        // Iterate materials and ordered forms
                        ResourceForm[] orderedForms = {
                                ResourceForm.ORE,
                                ResourceForm.DEEPSLATE_ORE,
                                ResourceForm.RAW_STORAGE_BLOCK,
                                ResourceForm.STORAGE_BLOCK,
                                ResourceForm.RAW_ORE,
                                ResourceForm.INGOT,
                                ResourceForm.GEM,
                                ResourceForm.NUGGET,
                                ResourceForm.DUST,
                                ResourceForm.PLATE,
                                ResourceForm.ROD,
                                ResourceForm.SCREW,
                                ResourceForm.WIRE,
                                ResourceForm.GEAR
                        };

                        for (Material material : ModMaterials.getAllMaterials()) {
                            for (ResourceForm form : orderedForms) {
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

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
