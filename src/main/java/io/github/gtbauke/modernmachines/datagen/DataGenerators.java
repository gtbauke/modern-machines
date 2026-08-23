package io.github.gtbauke.modernmachines.datagen;

import io.github.gtbauke.modernmachines.worldgen.ModBiomeModifiers;
import io.github.gtbauke.modernmachines.worldgen.ModConfiguredFeatures;
import io.github.gtbauke.modernmachines.worldgen.ModPlacedFeatures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DataGenerators {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DataGenerators::gatherDataClient);
        modEventBus.addListener(DataGenerators::gatherDataServer);
    }

    public static void gatherDataClient(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
        event.createProvider(ModLanguageProvider::new);
    }

    public static void gatherDataServer(GatherDataEvent.Server event) {
        event.createDatapackRegistryObjects(BUILDER);
        event.createBlockAndItemTags(ModBlockTagsProvider::new, ModItemTagsProvider::new);
        event.createProvider(ModFluidTagsProvider::new);
        event.createProvider(ModLootTableProvider::new);
        event.createProvider(ModRecipeProvider.Runner::new);
    }
}
