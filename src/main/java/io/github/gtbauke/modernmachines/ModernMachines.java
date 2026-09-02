package io.github.gtbauke.modernmachines;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.core.registry.ModBlockEntities;
import io.github.gtbauke.modernmachines.core.registry.ModBlocks;
import io.github.gtbauke.modernmachines.core.registry.ModCreativeTabs;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import io.github.gtbauke.modernmachines.core.registry.ModItems;
import io.github.gtbauke.modernmachines.core.registry.ModMaterials;
import io.github.gtbauke.modernmachines.core.registry.ModMenuTypes;
import io.github.gtbauke.modernmachines.core.registry.ModRecipeTypes;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import io.github.gtbauke.modernmachines.data.VirtualDataPack;
import io.github.gtbauke.modernmachines.data.VirtualResourcePack;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ModernMachines.MOD_ID)
public class ModernMachines {
    public static final String MOD_ID = "modernmachines";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ModernMachines(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModBlockEntities::registerCapabilities);

        // Initialize materials before deferred registers
        ModMaterials.init();

        io.github.gtbauke.modernmachines.core.registry.ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        io.github.gtbauke.modernmachines.network.ModNetworking.register(modEventBus);
        io.github.gtbauke.modernmachines.datagen.DataGenerators.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(this::addReloadListeners);

        modEventBus.addListener(this::addPackFinders);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(consumer -> {
                var pack = VirtualDataPack.createDataPack();
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        } else if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(consumer -> {
                var pack = VirtualResourcePack.createResourcePack();
                if (pack != null) {
                    consumer.accept(pack);
                }
            });
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Modern Machines initialized with {} materials!", ModMaterials.getAllMaterials().size());
    }

    private void addReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(MOD_ID, "materials"), MaterialStatsManager.INSTANCE);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Modern Machines server starting");
    }
}
