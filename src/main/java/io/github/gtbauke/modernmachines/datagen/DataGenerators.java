package io.github.gtbauke.modernmachines.datagen;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::gatherDataClient);
        modEventBus.addListener(DataGenerators::gatherDataServer);
    }

    public static void gatherDataClient(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
        event.createProvider(ModLanguageProvider::new);
    }

    public static void gatherDataServer(GatherDataEvent.Server event) {
        event.createBlockAndItemTags(ModBlockTagsProvider::new, ModItemTagsProvider::new);
        event.createProvider(ModFluidTagsProvider::new);
        event.createProvider(ModLootTableProvider::new);
        event.createProvider(ModRecipeProvider.Runner::new);
    }
}
