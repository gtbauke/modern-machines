package io.github.gtbauke.modernmachines.core.registry;

import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.worldgen.placement.AdjacentBlockPlacementModifier;
import io.github.gtbauke.modernmachines.worldgen.placement.DimensionFilterPlacementModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, ModernMachines.MOD_ID);

    public static final Supplier<PlacementModifierType<DimensionFilterPlacementModifier>> DIMENSION_FILTER =
            PLACEMENT_MODIFIERS.register("dimension_filter", () -> registerType(DimensionFilterPlacementModifier.CODEC));

    public static final Supplier<PlacementModifierType<AdjacentBlockPlacementModifier>> ADJACENT_BLOCK =
            PLACEMENT_MODIFIERS.register("adjacent_block", () -> registerType(AdjacentBlockPlacementModifier.CODEC));

    private static <P extends PlacementModifier> PlacementModifierType<P> registerType(MapCodec<P> codec) {
        return () -> codec;
    }

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIERS.register(eventBus);
    }
}
