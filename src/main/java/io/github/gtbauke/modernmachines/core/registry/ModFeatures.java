package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.worldgen.feature.LargeOreVeinConfiguration;
import io.github.gtbauke.modernmachines.worldgen.feature.LargeOreVeinFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, ModernMachines.MOD_ID);

    public static final Supplier<LargeOreVeinFeature> LARGE_ORE_VEIN =
            FEATURES.register("large_ore_vein", () -> new LargeOreVeinFeature(LargeOreVeinConfiguration.CODEC.codec()));

    public static final Supplier<io.github.gtbauke.modernmachines.worldgen.feature.SubsurfaceReservoirFeature> SUBSURFACE_RESERVOIR =
            FEATURES.register("subsurface_reservoir", () -> new io.github.gtbauke.modernmachines.worldgen.feature.SubsurfaceReservoirFeature(
                    io.github.gtbauke.modernmachines.worldgen.feature.SubsurfaceReservoirConfiguration.CODEC.codec()
            ));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
