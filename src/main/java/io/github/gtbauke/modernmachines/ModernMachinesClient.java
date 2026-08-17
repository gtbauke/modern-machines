package io.github.gtbauke.modernmachines;

import java.util.List;

import io.github.gtbauke.modernmachines.api.resource.Material;
import io.github.gtbauke.modernmachines.api.resource.ResourceForm;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.machine.client.AlloySmelterScreen;
import io.github.gtbauke.modernmachines.modular.client.PartBuilderScreen;
import io.github.gtbauke.modernmachines.modular.client.TinkeringTableScreen;
import io.github.gtbauke.modernmachines.modular.client.tint.ModularToolPartTintSource;
import io.github.gtbauke.modernmachines.modular.client.tint.ToolPartMaterialTintSource;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ModernMachines.MOD_ID, dist = Dist.CLIENT)
public class ModernMachinesClient {

    public ModernMachinesClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerBlockColors);
        modEventBus.addListener(this::registerItemColors);
    }

    private void registerItemColors(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "modular_part_tint"), ModularToolPartTintSource.CODEC);
        event.register(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "tool_part_material_tint"), ToolPartMaterialTintSource.CODEC);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ModernMachines.LOGGER.info("Modern Machines Client initialized");
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.PART_BUILDER.get(), PartBuilderScreen::new);
        event.register(ModMenuTypes.TINKERING_TABLE.get(), TinkeringTableScreen::new);
        event.register(ModMenuTypes.ALLOY_SMELTER.get(), AlloySmelterScreen::new);
    }

    private void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        for (Material material : ModMaterials.getAllMaterials()) {
            int tintColor = 0xFF000000 | material.colorHex();
            BlockTintSource tintSource = state -> tintColor;

            for (ResourceForm form : material.supportedForms()) {
                if (material.isRegisteredLocally(form) && form.isBlock()) {
                    Block block = material.getBlock(form);
                    if (block != null) {
                        event.getBlockColors().register(List.of(tintSource), block);
                    }
                }
            }
        }
    }
}
